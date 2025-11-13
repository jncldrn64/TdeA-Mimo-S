package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.DatosPago;
import co.edu.tdea.heladosmimos.web.entidades.Pedido;
import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.entidades.enums.MetodoPago;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoProcesarPago;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioPedido;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioUsuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador HTML para pasarela de pagos.
 * Gestiona vistas Thymeleaf para procesamiento de pagos.
 */
@Controller
@RequestMapping("/pasarela")
public class ControladorPagos {

    private static final Logger logger = LoggerFactory.getLogger(ControladorPagos.class);

    @Autowired
    private RepositorioPedido repositorioPedido;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private CasoDeUsoProcesarPago casoDeUsoProcesarPago;

    /**
     * Muestra formulario de pasarela de pagos para un pedido.
     */
    @GetMapping("/{idPedido}")
    public String mostrarPasarela(@PathVariable Long idPedido, Model modelo) {
        try {
            Pedido pedido = repositorioPedido.buscarPorId(idPedido)
                .orElseThrow(() -> new ProductoNoEncontradoException(
                    "No existe pedido con ID: " + idPedido));

            // Obtener usuario del pedido
            Usuario usuario = repositorioUsuario.buscarPorId(pedido.getIdUsuario());

            // Agregar variables individuales para el HTML
            modelo.addAttribute("idPedido", pedido.getIdPedido());
            modelo.addAttribute("usuario", usuario);
            modelo.addAttribute("subtotal", pedido.getSubtotal() != null ? pedido.getSubtotal() : 0.0);
            modelo.addAttribute("iva", pedido.getIva() != null ? pedido.getIva() : 0.0);
            modelo.addAttribute("costoEnvio", pedido.getCostoEnvio() != null ? pedido.getCostoEnvio() : 0.0);
            modelo.addAttribute("descuento", pedido.getDescuento() != null ? pedido.getDescuento() : 0.0);
            modelo.addAttribute("total", pedido.getTotal() != null ? pedido.getTotal() : 0.0);

            // Mantener compatibilidad con posibles otros usos
            modelo.addAttribute("pedido", pedido);
            modelo.addAttribute("metodosPago", MetodoPago.values());
            modelo.addAttribute("datosPago", new DatosPago());

            return "pasarela-pagos";

        } catch (ProductoNoEncontradoException e) {
            logger.error("Error al mostrar pasarela: {}", e.getMessage());
            modelo.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Procesa pago desde formulario HTML.
     */
    @PostMapping("/procesar")
    public String procesarPago(
            @RequestParam Long idPedido,
            @RequestParam MetodoPago metodoPago,
            @ModelAttribute DatosPago datosPago,
            Model modelo) {

        try {
            Map<String, Object> resultado = casoDeUsoProcesarPago.ejecutar(
                idPedido, metodoPago, datosPago);

            modelo.addAttribute("resultado", resultado);
            return "redirect:/pago/confirmacion?idPedido=" + idPedido;

        } catch (PedidoYaPagadoException | DatosTarjetaInvalidosException |
                 PagoRechazadoException | MetodoPagoNoSoportadoException |
                 ProductoNoEncontradoException | StockInsuficienteException e) {

            logger.warn("Error al procesar pago pedido {}: {}", idPedido, e.getMessage());
            modelo.addAttribute("error", e.getMessage());
            return "redirect:/pasarela/" + idPedido + "?error=" + e.getMessage();
        }
    }

    /**
     * Muestra página de confirmación de pago.
     */
    @GetMapping("/confirmacion")
    public String mostrarConfirmacion(@RequestParam Long idPedido, Model modelo) {
        try {
            Pedido pedido = repositorioPedido.buscarPorId(idPedido)
                .orElseThrow(() -> new ProductoNoEncontradoException(
                    "No existe pedido con ID: " + idPedido));

            modelo.addAttribute("pedido", pedido);
            return "confirmacion-pago";

        } catch (ProductoNoEncontradoException e) {
            logger.error("Error al mostrar confirmación: {}", e.getMessage());
            modelo.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
