package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.DatosPago;
import co.edu.tdea.heladosmimos.web.entidades.enums.MetodoPago;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoProcesarPago;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoConsultarEstadoPago;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * API REST para pasarela de pagos (RF-02).
 * Excepciones manejadas por @ControllerAdvice global.
 */
@RestController
@RequestMapping("/api/pago")
public class ControladorPagosREST {

    @Autowired
    private CasoDeUsoProcesarPago casoDeUsoProcesarPago;

    @Autowired
    private CasoDeUsoConsultarEstadoPago casoDeUsoConsultarEstadoPago;

    /**
     * Procesa pago para un pedido.
     *
     * Parámetros:
     * - idPedido: ID del pedido a pagar
     * - metodoPago: TARJETA_DEBITO_EN_LINEA, TARJETA_CREDITO_EN_LINEA,
     *               EFECTIVO_CONTRA_ENTREGA, DATAFONO_CONTRA_ENTREGA
     *
     * Para tarjetas (opcional, se envía como JSON):
     * - numeroTarjeta: 16 dígitos (ej: 4111111111111111)
     * - fechaExpiracion: MM/AA (ej: 12/25)
     * - codigoCVV: 3 dígitos (ej: 123)
     * - nombreTitular: Nombre completo
     * - tipoDocumento: CC, CE, NIT
     * - numeroDocumento: Número de documento
     */
    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPago(
            @RequestParam Long idPedido,
            @RequestParam String metodoPago,
            @RequestParam(required = false) String numeroTarjeta,
            @RequestParam(required = false) String fechaExpiracion,
            @RequestParam(required = false) String codigoCVV,
            @RequestParam(required = false) String nombreTitular,
            @RequestParam(required = false) String tipoDocumento,
            @RequestParam(required = false) String numeroDocumento)
            throws ProductoNoEncontradoException, PedidoYaPagadoException,
                   DatosTarjetaInvalidosException, PagoRechazadoException,
                   MetodoPagoNoSoportadoException, StockInsuficienteException {

        // Convertir string a enum
        MetodoPago metodo = MetodoPago.valueOf(metodoPago);

        // Construir DTO con datos de pago
        DatosPago datosPago = new DatosPago();
        datosPago.setNumeroTarjeta(numeroTarjeta);
        datosPago.setFechaExpiracion(fechaExpiracion);
        datosPago.setCodigoCVV(codigoCVV);
        datosPago.setNombreTitular(nombreTitular);
        datosPago.setTipoDocumento(tipoDocumento);
        datosPago.setNumeroDocumento(numeroDocumento);

        // Procesar pago
        Map<String, Object> resultado = casoDeUsoProcesarPago.ejecutar(
            idPedido, metodo, datosPago);

        return ResponseEntity.ok(resultado);
    }

    /**
     * Consulta estado de pago de un pedido.
     */
    @GetMapping("/estado/{idPedido}")
    public ResponseEntity<?> consultarEstadoPago(@PathVariable Long idPedido)
            throws ProductoNoEncontradoException {

        Map<String, Object> estado = casoDeUsoConsultarEstadoPago.ejecutar(idPedido);
        return ResponseEntity.ok(estado);
    }
}
