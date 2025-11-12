package co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales;

import co.edu.tdea.heladosmimos.web.entidades.DatosPago;
import co.edu.tdea.heladosmimos.web.entidades.Pedido;
import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.entidades.enums.EstadoPedido;
import co.edu.tdea.heladosmimos.web.entidades.enums.MetodoPago;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioPedido;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Random;

/**
 * Servicio de procesamiento de pagos (RF-02).
 * Implementa validación ficticia para testing sin integración real.
 *
 * TARJETAS DE PRUEBA ACEPTADAS:
 * - 4111111111111111 (Visa)
 * - 5500000000000004 (Mastercard)
 *
 * MÉTODOS SOPORTADOS:
 * - Tarjeta débito/crédito (validación ficticia)
 * - Efectivo contra entrega (genera código)
 * - Datáfono contra entrega (genera código)
 */
@Service
public class ServicioPagos {

    private static final Logger logger = LoggerFactory.getLogger(ServicioPagos.class);

    // Tarjetas de prueba aprobadas (hardcoded)
    private static final String VISA_PRUEBA = "4111111111111111";
    private static final String MASTERCARD_PRUEBA = "5500000000000004";

    @Autowired
    private RepositorioPedido repositorioPedido;

    @Autowired
    private RepositorioProducto repositorioProducto;

    /**
     * Procesa pago para un pedido pendiente.
     * Actualiza estado del pedido y reduce stock si pago exitoso.
     */
    @Transactional
    public Map<String, Object> procesarPago(Long idPedido, MetodoPago metodoPago, DatosPago datosPago)
            throws ProductoNoEncontradoException, PedidoYaPagadoException,
                   DatosTarjetaInvalidosException, PagoRechazadoException,
                   MetodoPagoNoSoportadoException, StockInsuficienteException {

        // Buscar pedido
        Pedido pedido = repositorioPedido.buscarPorId(idPedido)
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "No existe pedido con ID: " + idPedido));

        // Validar que no esté pagado
        if (pedido.getEstadoPedido() == EstadoPedido.PAGO_CONFIRMADO) {
            throw new PedidoYaPagadoException(
                "El pedido " + idPedido + " ya fue pagado");
        }

        String codigoConfirmacion = null;
        String instrucciones = null;

        // Procesar según método de pago
        switch (metodoPago) {
            case TARJETA_DEBITO_EN_LINEA:
            case TARJETA_CREDITO_EN_LINEA:
                validarYProcesarTarjeta(datosPago);
                instrucciones = "Pago procesado exitosamente. " +
                               "Tu pedido será enviado a: " + pedido.getDireccionEnvio();
                break;

            case EFECTIVO_CONTRA_ENTREGA:
                codigoConfirmacion = generarCodigoConfirmacion();
                instrucciones = String.format(
                    "Pago contra entrega en EFECTIVO. " +
                    "Código de confirmación: %s. " +
                    "Presenta este código al recibir tu pedido en: %s. " +
                    "Total a pagar: $%.2f",
                    codigoConfirmacion,
                    pedido.getDireccionEnvio(),
                    pedido.getTotal()
                );
                logger.info("[PAGO CONTRA ENTREGA] Código de confirmación: {} - Pedido: {}",
                    codigoConfirmacion, idPedido);
                break;

            case DATAFONO_CONTRA_ENTREGA:
                codigoConfirmacion = generarCodigoConfirmacion();
                instrucciones = String.format(
                    "Pago contra entrega con DATÁFONO. " +
                    "Código de confirmación: %s. " +
                    "El repartidor llevará datáfono para pago con tarjeta. " +
                    "Dirección de entrega: %s. " +
                    "Total a pagar: $%.2f",
                    codigoConfirmacion,
                    pedido.getDireccionEnvio(),
                    pedido.getTotal()
                );
                logger.info("[PAGO DATÁFONO] Código de confirmación: {} - Pedido: {}",
                    codigoConfirmacion, idPedido);
                break;

            case TRANSFERENCIA_EN_LINEA:
            case PAYPAL_EN_LINEA:
                throw new MetodoPagoNoSoportadoException(
                    "El método de pago " + metodoPago + " no está implementado aún");

            default:
                throw new MetodoPagoNoSoportadoException(
                    "Método de pago no reconocido: " + metodoPago);
        }

        // Actualizar pedido
        pedido.setMetodoPago(metodoPago);
        pedido.setEstadoPedido(EstadoPedido.PAGO_CONFIRMADO);
        pedido.setFechaConfirmacionPago(LocalDateTime.now());
        repositorioPedido.guardar(pedido);

        // Reducir stock (solo después de confirmar pago)
        reducirStockDesdePedido(pedido);

        logger.info("Pago confirmado para pedido {} - Método: {}", idPedido, metodoPago);

        // Construir respuesta
        return Map.of(
            "success", true,
            "mensaje", "Pago procesado exitosamente",
            "idPedido", idPedido,
            "metodoPago", metodoPago.toString(),
            "codigoConfirmacion", codigoConfirmacion != null ? codigoConfirmacion : "",
            "instrucciones", instrucciones,
            "total", pedido.getTotal()
        );
    }

    /**
     * Valida formato de tarjeta y procesa pago ficticio.
     * TARJETAS DE PRUEBA: 4111111111111111 (Visa), 5500000000000004 (Mastercard)
     */
    private void validarYProcesarTarjeta(DatosPago datosPago)
            throws DatosTarjetaInvalidosException, PagoRechazadoException {

        String numero = datosPago.getNumeroTarjeta();
        String fecha = datosPago.getFechaExpiracion();
        String cvv = datosPago.getCodigoCVV();

        // Validar número de tarjeta
        if (numero == null || !numero.matches("\\d{16}")) {
            throw new DatosTarjetaInvalidosException(
                "Número de tarjeta inválido. Debe tener 16 dígitos");
        }

        // Validar fecha de expiración
        if (fecha == null || !fecha.matches("\\d{2}/\\d{2}")) {
            throw new DatosTarjetaInvalidosException(
                "Fecha de expiración inválida. Formato esperado: MM/AA");
        }

        // Validar que no esté vencida
        try {
            String[] partes = fecha.split("/");
            int mes = Integer.parseInt(partes[0]);
            int anio = 2000 + Integer.parseInt(partes[1]);

            if (mes < 1 || mes > 12) {
                throw new DatosTarjetaInvalidosException("Mes inválido: " + mes);
            }

            YearMonth fechaTarjeta = YearMonth.of(anio, mes);
            YearMonth fechaActual = YearMonth.now();

            if (fechaTarjeta.isBefore(fechaActual)) {
                throw new DatosTarjetaInvalidosException("Tarjeta vencida");
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new DatosTarjetaInvalidosException("Fecha de expiración inválida");
        }

        // Validar CVV
        if (cvv == null || !cvv.matches("\\d{3}")) {
            throw new DatosTarjetaInvalidosException(
                "CVV inválido. Debe tener 3 dígitos");
        }

        // Validar tarjeta de prueba (hardcoded)
        if (!numero.equals(VISA_PRUEBA) && !numero.equals(MASTERCARD_PRUEBA)) {
            logger.warn("Tarjeta rechazada: {} - No es tarjeta de prueba", numero);
            throw new PagoRechazadoException(
                "Tarjeta rechazada. Para pruebas use: " +
                VISA_PRUEBA + " (Visa) o " +
                MASTERCARD_PRUEBA + " (Mastercard)");
        }

        logger.info("Tarjeta validada exitosamente: {} (prueba)", numero);
    }

    /**
     * Genera código de confirmación aleatorio de 6 dígitos.
     */
    private String generarCodigoConfirmacion() {
        Random random = new Random();
        int codigo = 100000 + random.nextInt(900000); // 6 dígitos
        return String.valueOf(codigo);
    }

    /**
     * Reduce stock de productos asociados al pedido.
     * NOTA: Esta lógica debería estar en ServicioCarritoCompras,
     * pero la movemos aquí para ejecutarla SOLO después de confirmar pago.
     */
    private void reducirStockDesdePedido(Pedido pedido)
            throws ProductoNoEncontradoException, StockInsuficienteException {

        // TODO: Obtener items del pedido desde tabla intermedia pedido_items
        // Por ahora, el stock ya se redujo en checkout (problema conocido)
        // Esta implementación completa requiere tabla pedido_items

        logger.info("Stock reducido para pedido {} (lógica pendiente de implementar)",
            pedido.getIdPedido());
    }

    /**
     * Consulta estado de pago de un pedido.
     */
    public Map<String, Object> consultarEstadoPago(Long idPedido)
            throws ProductoNoEncontradoException {

        Pedido pedido = repositorioPedido.buscarPorId(idPedido)
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "No existe pedido con ID: " + idPedido));

        return Map.of(
            "idPedido", idPedido,
            "estadoPedido", pedido.getEstadoPedido().toString(),
            "metodoPago", pedido.getMetodoPago() != null ? pedido.getMetodoPago().toString() : "SIN_ASIGNAR",
            "fechaCreacion", pedido.getFechaCreacion().toString(),
            "fechaConfirmacion", pedido.getFechaConfirmacionPago() != null ?
                pedido.getFechaConfirmacionPago().toString() : "",
            "total", pedido.getTotal()
        );
    }
}
