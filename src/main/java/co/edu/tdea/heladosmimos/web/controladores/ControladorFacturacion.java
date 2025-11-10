package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.Factura;
import co.edu.tdea.heladosmimos.web.entidades.Pedido;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioPedido;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.DatosFacturacion;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioFacturacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para el módulo de facturación (RF-04).
 * 
 * PROPÓSITO: Gestionar las peticiones HTTP relacionadas con la generación
 * y visualización de facturas electrónicas.
 * 
 * FLUJO:
 * 1. Usuario completa pago exitoso (RF-02)
 * 2. Redirige a /factura/formulario/{idPedido}
 * 3. Usuario completa datos fiscales
 * 4. POST /factura/generar
 * 5. Muestra factura generada en /factura/{id}
 * 
 * DEPENDENCIAS:
 * - ServicioFacturacion: Lógica de negocio
 * - RepositorioPedido: Para validar pedido
 * 
 * GENERADO POR: Claude - 2024-11-09
 */
@Controller
@RequestMapping("/factura")
public class ControladorFacturacion {
    
    @Autowired
    private ServicioFacturacion servicioFacturacion;
    
    @Autowired
    private RepositorioPedido repositorioPedido;
    
    /**
     * Muestra el formulario de datos fiscales para generar factura.
     * 
     * RUTA: GET /factura/formulario/{idPedido}
     * 
     * Este endpoint se llama después de confirmar el pago exitoso.
     */
    @GetMapping("/formulario/{idPedido}")
    public String mostrarFormularioFacturacion(
            @PathVariable Long idPedido,
            Model modelo) {
        
        try {
            // Validar que el pedido existe
            Pedido pedido = repositorioPedido.buscarPorId(idPedido)
                .orElseThrow(() -> new IllegalArgumentException(
                    "No existe pedido con ID: " + idPedido));
            
            // Crear objeto vacío para el formulario
            DatosFacturacion datosFacturacion = new DatosFacturacion();
            
            // Enviar datos al template
            modelo.addAttribute("pedido", pedido);
            modelo.addAttribute("datosFacturacion", datosFacturacion);
            
            return "facturacion/formulario-factura";
            
        } catch (Exception e) {
            modelo.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    /**
     * Procesa los datos del formulario y genera la factura.
     * 
     * RUTA: POST /factura/generar
     */
    @PostMapping("/generar")
    public String generarFactura(
            @RequestParam Long idPedido,
            @ModelAttribute DatosFacturacion datosFacturacion,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Generar factura
            Factura factura = servicioFacturacion.generarFacturaParaPedido(
                idPedido, datosFacturacion);
            
            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("mensaje", 
                "Factura generada exitosamente: " + factura.getNumeroFactura());
            
            // Redirigir a ver la factura
            return "redirect:/factura/" + factura.getIdFactura();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/factura/formulario/" + idPedido;
        }
    }
    
    /**
     * Muestra los detalles de una factura generada.
     * 
     * RUTA: GET /factura/{idFactura}
     */
    @GetMapping("/{idFactura}")
    public String verFactura(
            @PathVariable Long idFactura,
            Model modelo) {
        
        try {
            // Buscar factura
            Factura factura = servicioFacturacion.obtenerFacturaPorNumero(
                servicioFacturacion.obtenerFacturaDePedido(idFactura).getNumeroFactura());
            
            // Obtener pedido relacionado
            Pedido pedido = repositorioPedido.buscarPorId(factura.getIdPedido())
                .orElseThrow(() -> new IllegalArgumentException(
                    "No existe pedido con ID: " + factura.getIdPedido()));
            
            // Enviar al template
            modelo.addAttribute("factura", factura);
            modelo.addAttribute("pedido", pedido);
            
            return "facturacion/detalle-factura";
            
        } catch (Exception e) {
            modelo.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    /**
     * Busca factura por número.
     * 
     * RUTA: GET /factura/buscar?numero=FACT-20241109-00001
     */
    @GetMapping("/buscar")
    public String buscarFacturaPorNumero(
            @RequestParam String numero,
            RedirectAttributes redirectAttributes) {
        
        try {
            Factura factura = servicioFacturacion.obtenerFacturaPorNumero(numero);
            return "redirect:/factura/" + factura.getIdFactura();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "No se encontró factura con número: " + numero);
            return "redirect:/";
        }
    }
    
    /**
     * Descarga la factura en PDF (FUTURO - no implementado todavía).
     * 
     * RUTA: GET /factura/{idFactura}/descargar
     */
    @GetMapping("/{idFactura}/descargar")
    public String descargarPDF(
            @PathVariable Long idFactura,
            RedirectAttributes redirectAttributes) {
        
        // TODO: Implementar generación de PDF con iText
        redirectAttributes.addFlashAttribute("info", 
            "Generación de PDF en desarrollo. Por ahora puedes imprimir la página.");
        
        return "redirect:/factura/" + idFactura;
    }
}