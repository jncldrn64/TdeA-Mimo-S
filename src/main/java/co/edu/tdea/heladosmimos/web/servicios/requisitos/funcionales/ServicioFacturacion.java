package co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales;

import co.edu.tdea.heladosmimos.web.entidades.*;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.puertos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servicio encargado de la generación y gestión de facturas electrónicas.
 * 
 * DECISIÓN TÉCNICA: 
 * - IVA Colombia: 19% (tarifa estándar)
 * - Número de factura: FACT-YYYYMMDD-XXXXX (auto-generado)
 * - No genera PDF todavía (se implementará después)
 * 
 * DEPENDENCIAS:
 * - RepositorioFactura: Persistencia de facturas
 * - RepositorioPedido: Obtener datos del pedido
 * - RepositorioUsuario: Obtener datos del cliente
 * 
 * CONEXIÓN CON OTROS MÓDULOS:
 * - Se invoca después de confirmar pago exitoso (RF-02)
 * - Usa datos del pedido (RF-05 Carrito)
 * - Usa datos del usuario (RF-03 Login)
 * 
 * GENERADO POR: Claude - 2024-11-09
 */
@Service
public class ServicioFacturacion {
    
    private static final Double IVA_COLOMBIA = 0.19; // 19%
    
    @Autowired
    private RepositorioFactura repositorioFactura;
    
    @Autowired
    private RepositorioPedido repositorioPedido;
    
    @Autowired
    private RepositorioUsuario repositorioUsuario;
    
    /**
     * Genera una factura para un pedido confirmado.
     * 
     * VALIDACIONES:
     * - Pedido debe existir
     * - Pedido no debe tener factura previa
     * - Usuario debe existir
     * - Datos fiscales completos
     */
    public Factura generarFacturaParaPedido(Long idPedido, DatosFacturacion datosFacturacion)
            throws ProductoNoEncontradoException, FacturaYaExisteException,
                   DatosFacturacionInvalidosException {

        // Validar que el pedido existe (RepositorioPedido retorna Optional)
        Pedido pedido = repositorioPedido.buscarPorId(idPedido)
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "No existe pedido con ID: " + idPedido));

        // Validar que no exista factura previa
        if (repositorioFactura.existeFacturaParaPedido(idPedido)) {
            throw new FacturaYaExisteException(
                "El pedido " + idPedido + " ya tiene una factura generada");
        }

        // Obtener usuario (RepositorioUsuario retorna Usuario directo)
        Usuario usuario = repositorioUsuario.buscarPorId(pedido.getIdUsuario());

        if (usuario == null) {
            throw new ProductoNoEncontradoException(
                "No existe usuario con ID: " + pedido.getIdUsuario());
        }

        // Validar datos de facturación completos
        validarDatosFacturacion(datosFacturacion, usuario);

        // Crear factura
        Factura factura = new Factura();
        factura.setIdPedido(idPedido);
        factura.setNumeroFactura(generarNumeroFactura());
        factura.setFechaEmision(LocalDateTime.now());
        
        // Datos del cliente (del formulario o del usuario)
        factura.setNitCliente(datosFacturacion.getNit() != null 
            ? datosFacturacion.getNit() 
            : usuario.getNit());
        factura.setRazonSocial(datosFacturacion.getRazonSocial());
        factura.setDireccionFiscal(construirDireccionCompleta(datosFacturacion));
        
        // Calcular totales desde el pedido
        factura.setSubtotal(pedido.getSubtotal());
        factura.setIva(calcularIVA(pedido.getSubtotal()));
        factura.setTotal(calcularTotalConIVA(pedido.getSubtotal()));
        
        // Guardar factura
        return repositorioFactura.guardar(factura);
    }
    
    /**
     * Genera número único de factura: FACT-YYYYMMDD-XXXXX
     */
    private String generarNumeroFactura() {
        String fechaFormato = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Obtener último número del día
        Long contadorDelDia = repositorioFactura.obtenerTodasLasFacturas()
            .stream()
            .filter(f -> f.getNumeroFactura().contains(fechaFormato))
            .count();
        
        Long siguienteNumero = contadorDelDia + 1;
        
        return String.format("FACT-%s-%05d", fechaFormato, siguienteNumero);
    }
    
    /**
     * Calcula IVA del 19% sobre el subtotal.
     */
    private Double calcularIVA(Double subtotal) {
        if (subtotal == null) {
            return 0.0;
        }
        return Math.round(subtotal * IVA_COLOMBIA * 100.0) / 100.0;
    }
    
    /**
     * Calcula total incluyendo IVA.
     */
    private Double calcularTotalConIVA(Double subtotal) {
        if (subtotal == null) {
            return 0.0;
        }
        Double iva = calcularIVA(subtotal);
        return Math.round((subtotal + iva) * 100.0) / 100.0;
    }
    
    /**
     * Construye dirección fiscal completa desde los datos del formulario.
     */
    private String construirDireccionCompleta(DatosFacturacion datos) {
        return String.format("%s, %s, %s, %s - Código Postal: %s",
            datos.getDireccionCalle(),
            datos.getCiudad(),
            datos.getEstado(),
            "Colombia",
            datos.getCodigoPostal());
    }

    /**
     * Valida que los datos de facturación estén completos.
     * Si falta algún campo requerido, lanza DatosFacturacionInvalidosException.
     */
    private void validarDatosFacturacion(DatosFacturacion datos, Usuario usuario)
            throws DatosFacturacionInvalidosException {

        // Validar NIT (puede venir de formulario o de usuario)
        String nit = datos.getNit() != null ? datos.getNit() : usuario.getNit();
        if (nit == null || nit.trim().isEmpty()) {
            throw new DatosFacturacionInvalidosException("El NIT es obligatorio");
        }

        // Validar razón social
        if (datos.getRazonSocial() == null || datos.getRazonSocial().trim().isEmpty()) {
            throw new DatosFacturacionInvalidosException("La razón social es obligatoria");
        }

        // Validar dirección
        if (datos.getDireccionCalle() == null || datos.getDireccionCalle().trim().isEmpty()) {
            throw new DatosFacturacionInvalidosException("La dirección es obligatoria");
        }

        // Validar ciudad
        if (datos.getCiudad() == null || datos.getCiudad().trim().isEmpty()) {
            throw new DatosFacturacionInvalidosException("La ciudad es obligatoria");
        }

        // Validar estado/departamento
        if (datos.getEstado() == null || datos.getEstado().trim().isEmpty()) {
            throw new DatosFacturacionInvalidosException("El estado/departamento es obligatorio");
        }

        // Validar código postal
        if (datos.getCodigoPostal() == null || datos.getCodigoPostal().trim().isEmpty()) {
            throw new DatosFacturacionInvalidosException("El código postal es obligatorio");
        }
    }

    /**
     * Obtiene factura por ID de pedido.
     */
    public Factura obtenerFacturaDePedido(Long idPedido) throws FacturaNoEncontradaException {
        return repositorioFactura.buscarPorIdPedido(idPedido)
            .orElseThrow(() -> new FacturaNoEncontradaException(
                "No existe factura para el pedido: " + idPedido));
    }

    /**
     * Obtiene factura por número.
     */
    public Factura obtenerFacturaPorNumero(String numeroFactura) throws FacturaNoEncontradaException {
        return repositorioFactura.buscarPorNumeroFactura(numeroFactura)
            .orElseThrow(() -> new FacturaNoEncontradaException(
                "No existe factura con número: " + numeroFactura));
    }
}