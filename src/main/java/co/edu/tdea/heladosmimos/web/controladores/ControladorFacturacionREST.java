package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.Factura;
import co.edu.tdea.heladosmimos.web.entidades.Pedido;
import co.edu.tdea.heladosmimos.web.entidades.DatosFacturacion;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoGenerarFactura;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoConsultarFactura;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioPedido;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * API REST para facturación (RF-04).
 * Excepciones manejadas por @ControllerAdvice global (SOLID).
 */
@RestController
@RequestMapping("/api/factura")
public class ControladorFacturacionREST {

    @Autowired
    private CasoDeUsoGenerarFactura casoDeUsoGenerarFactura;

    @Autowired
    private CasoDeUsoConsultarFactura casoDeUsoConsultarFactura;

    @Autowired
    private RepositorioPedido repositorioPedido;

    @PostMapping("/generar")
    public ResponseEntity<?> generarFactura(
            @RequestParam Long idPedido,
            @RequestParam String nit,
            @RequestParam String razonSocial,
            @RequestParam String nombreCompleto,
            @RequestParam String direccionCalle,
            @RequestParam String ciudad,
            @RequestParam String codigoPostal,
            @RequestParam String estado,
            @RequestParam String telefono,
            @RequestParam String correoElectronico)
            throws ProductoNoEncontradoException, FacturaYaExisteException,
                   DatosFacturacionInvalidosException {

        // Construir DTO con los datos recibidos
        DatosFacturacion datosFacturacion = new DatosFacturacion();
        datosFacturacion.setNit(nit);
        datosFacturacion.setRazonSocial(razonSocial);
        datosFacturacion.setNombreCompleto(nombreCompleto);
        datosFacturacion.setDireccionCalle(direccionCalle);
        datosFacturacion.setCiudad(ciudad);
        datosFacturacion.setCodigoPostal(codigoPostal);
        datosFacturacion.setEstado(estado);
        datosFacturacion.setTelefono(telefono);
        datosFacturacion.setCorreoElectronico(correoElectronico);

        // Generar factura a través del caso de uso
        Factura factura = casoDeUsoGenerarFactura.ejecutar(idPedido, datosFacturacion);

        // Obtener pedido para incluir en respuesta
        Pedido pedido = repositorioPedido.buscarPorId(factura.getIdPedido())
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "No existe pedido con ID: " + factura.getIdPedido()));

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Factura generada exitosamente");
        respuesta.put("factura", Map.of(
            "idFactura", factura.getIdFactura(),
            "idPedido", factura.getIdPedido(),
            "numeroFactura", factura.getNumeroFactura(),
            "nitCliente", factura.getNitCliente(),
            "razonSocial", factura.getRazonSocial(),
            "direccionFiscal", factura.getDireccionFiscal(),
            "fechaEmision", factura.getFechaEmision().toString(),
            "subtotal", factura.getSubtotal(),
            "iva", factura.getIva(),
            "total", factura.getTotal()
        ));

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{idFactura}")
    public ResponseEntity<?> obtenerFactura(@PathVariable Long idFactura)
            throws FacturaNoEncontradaException, ProductoNoEncontradoException {

        Factura factura = casoDeUsoConsultarFactura.ejecutarPorPedido(idFactura);

        Pedido pedido = repositorioPedido.buscarPorId(factura.getIdPedido())
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "No existe pedido con ID: " + factura.getIdPedido()));

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("factura", Map.of(
            "idFactura", factura.getIdFactura(),
            "idPedido", factura.getIdPedido(),
            "numeroFactura", factura.getNumeroFactura(),
            "nitCliente", factura.getNitCliente(),
            "razonSocial", factura.getRazonSocial(),
            "direccionFiscal", factura.getDireccionFiscal(),
            "fechaEmision", factura.getFechaEmision().toString(),
            "subtotal", factura.getSubtotal(),
            "iva", factura.getIva(),
            "total", factura.getTotal()
        ));
        respuesta.put("pedido", Map.of(
            "idPedido", pedido.getIdPedido(),
            "total", pedido.getTotal()
        ));

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarFacturaPorNumero(@RequestParam String numero)
            throws FacturaNoEncontradaException, ProductoNoEncontradoException {

        Factura factura = casoDeUsoConsultarFactura.ejecutarPorNumero(numero);

        Pedido pedido = repositorioPedido.buscarPorId(factura.getIdPedido())
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "No existe pedido con ID: " + factura.getIdPedido()));

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("factura", Map.of(
            "idFactura", factura.getIdFactura(),
            "idPedido", factura.getIdPedido(),
            "numeroFactura", factura.getNumeroFactura(),
            "nitCliente", factura.getNitCliente(),
            "razonSocial", factura.getRazonSocial(),
            "direccionFiscal", factura.getDireccionFiscal(),
            "fechaEmision", factura.getFechaEmision().toString(),
            "subtotal", factura.getSubtotal(),
            "iva", factura.getIva(),
            "total", factura.getTotal()
        ));
        respuesta.put("pedido", Map.of(
            "idPedido", pedido.getIdPedido(),
            "total", pedido.getTotal()
        ));

        return ResponseEntity.ok(respuesta);
    }
}
