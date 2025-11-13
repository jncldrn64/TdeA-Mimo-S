package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.Factura;
import co.edu.tdea.heladosmimos.web.entidades.Pedido;
import co.edu.tdea.heladosmimos.web.entidades.DatosFacturacion;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoGenerarFactura;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoConsultarFactura;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioPedido;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de facturación siguiendo arquitectura hexagonal.
 * Usa casos de uso para orquestar lógica de negocio.
 */
@Controller
@RequestMapping("/factura")
public class ControladorFacturacion {

    @Autowired
    private CasoDeUsoGenerarFactura casoDeUsoGenerarFactura;

    @Autowired
    private CasoDeUsoConsultarFactura casoDeUsoConsultarFactura;

    @Autowired
    private RepositorioPedido repositorioPedido;

    @GetMapping("/formulario/{idPedido}")
    public String mostrarFormularioFacturacion(
            @PathVariable Long idPedido,
            Model modelo) throws ProductoNoEncontradoException {

        Pedido pedido = repositorioPedido.buscarPorId(idPedido)
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "No existe pedido con ID: " + idPedido));

        DatosFacturacion datosFacturacion = new DatosFacturacion();

        modelo.addAttribute("pedido", pedido);
        modelo.addAttribute("datosFacturacion", datosFacturacion);

        return "facturacion/formulario-factura";
    }

    @PostMapping("/generar")
    public String generarFactura(
            @RequestParam Long idPedido,
            @ModelAttribute DatosFacturacion datosFacturacion,
            Model modelo)
            throws ProductoNoEncontradoException, FacturaYaExisteException,
                   DatosFacturacionInvalidosException {

        Factura factura = casoDeUsoGenerarFactura.ejecutar(idPedido, datosFacturacion);

        modelo.addAttribute("mensaje",
            "Factura generada exitosamente: " + factura.getNumeroFactura());
        modelo.addAttribute("factura", factura);

        return "redirect:/factura/" + factura.getIdFactura();
    }

    @GetMapping("/{idFactura}")
    public String verFactura(@PathVariable Long idFactura, Model modelo)
            throws FacturaNoEncontradaException, ProductoNoEncontradoException {

        Factura factura = casoDeUsoConsultarFactura.ejecutarPorId(idFactura);

        Pedido pedido = repositorioPedido.buscarPorId(factura.getIdPedido())
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "No existe pedido con ID: " + factura.getIdPedido()));

        modelo.addAttribute("factura", factura);
        modelo.addAttribute("pedido", pedido);

        return "facturacion/detalle-factura";
    }

    @GetMapping("/buscar")
    public String buscarFacturaPorNumero(@RequestParam String numero)
            throws FacturaNoEncontradaException {

        Factura factura = casoDeUsoConsultarFactura.ejecutarPorNumero(numero);
        return "redirect:/factura/" + factura.getIdFactura();
    }
}
