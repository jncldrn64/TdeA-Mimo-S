// ============================================================
// ARCHIVO 5: ControladorCarrito.java
// Ubicación: src/main/java/co/edu/tdea/heladosmimos/web/
// ============================================================
package co.edu.tdea.heladosmimos.web;

import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import co.edu.tdea.heladosmimos.web.seguridad.casosdeuso.CasoDeUsoAccesoCarrito;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioCarritoCompras;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PROPÓSITO: Gestionar peticiones HTTP relacionadas con el carrito de compras
 *
 * DECISIÓN TÉCNICA:
 * - Todas las operaciones pasan por CasoDeUsoAccesoCarrito (validación de seguridad)
 * - Manejo de errores con try-catch para mostrar mensajes al usuario
 * - Enriquece items del carrito con datos completos del producto para la vista
 * - ID de producto en edición se guarda en sesión HTTP (no en variable de instancia)
 *
 * DEPENDENCIAS:
 * - CasoDeUsoAccesoCarrito: Punto de entrada validado
 * - ServicioCarritoCompras: Para obtener contador de items (navbar)
 * - RepositorioProducto: Para obtener detalles completos de productos
 *
 * CONEXIÓN CON OTROS MÓDULOS:
 * - carrito.html: Template de vista del carrito
 * - base.html: Muestra contador de items en navbar
 *
 * GENERADO POR: Claude - 2024-11-08
 * CORREGIDO POR: Claude - 2024-11-09 (bug variable compartida)
 */
@Controller
@RequestMapping("/carrito")
public class ControladorCarrito {
    
    @Autowired
    private CasoDeUsoAccesoCarrito casoDeUsoAccesoCarrito;
    
    @Autowired
    private ServicioCarritoCompras servicioCarritoCompras;
    
    @Autowired
    private RepositorioProducto repositorioProducto;

    /**
     * Muestra la vista del carrito con items y total
     */
    @GetMapping
    public String mostrarCarrito(HttpSession sesion, Model model) {
        try {
            List<ItemCarrito> items = casoDeUsoAccesoCarrito.ejecutarObtenerCarrito();
            Double total = casoDeUsoAccesoCarrito.ejecutarObtenerTotal();

            // Enriquecer items con datos completos del producto
            List<Map<String, Object>> itemsEnriquecidos = enriquecerItemsConProductos(items);

            Long idProductoEnEdicion = (Long) sesion.getAttribute("idProductoEnEdicion");

            model.addAttribute("carrito", itemsEnriquecidos);
            model.addAttribute("totalFinal", total);
            model.addAttribute("idProductoEnEdicion", idProductoEnEdicion);
            model.addAttribute("cantidadTotalItems", servicioCarritoCompras.contarTotalDeProductos());

            return "carrito";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "carrito";
        }
    }
    
    /**
     * Agrega un producto al carrito desde el catálogo
     */
    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam Long idProducto, 
                                   @RequestParam(defaultValue = "1") Integer cantidad,
                                   Model model) {
        try {
            casoDeUsoAccesoCarrito.ejecutarAgregarProducto(idProducto, cantidad);
            return "redirect:/carrito";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/catalogo?error=" + e.getMessage();
        }
    }
    
    /**
     * Prepara la edición de un item (guarda ID y recarga vista)
     */
    @PostMapping("/preparar-edicion")
    public String prepararEdicion(@RequestParam Long idProducto, HttpSession sesion) {
        sesion.setAttribute("idProductoEnEdicion", idProducto);
        return "redirect:/carrito";
    }

    /**
     * Confirma la edición de cantidad de un item
     */
    @PostMapping("/editar")
    public String editarCantidad(@RequestParam Long idProducto,
                                  @RequestParam Integer nuevaCantidad,
                                  HttpSession sesion,
                                  Model model) {
        try {
            casoDeUsoAccesoCarrito.ejecutarModificarCantidad(idProducto, nuevaCantidad);
            sesion.removeAttribute("idProductoEnEdicion");
            return "redirect:/carrito";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/carrito?error=" + e.getMessage();
        }
    }
    
    /**
     * Elimina un producto específico del carrito
     */
    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Long idProducto) {
        try {
            casoDeUsoAccesoCarrito.ejecutarEliminarProducto(idProducto);
            return "redirect:/carrito";
        } catch (Exception e) {
            return "redirect:/carrito?error=" + e.getMessage();
        }
    }
    
    /**
     * Vacía completamente el carrito
     */
    @PostMapping("/vaciar")
    public String vaciarCarrito() {
        try {
            casoDeUsoAccesoCarrito.ejecutarVaciarCarrito();
            return "redirect:/carrito";
        } catch (Exception e) {
            return "redirect:/carrito?error=" + e.getMessage();
        }
    }
    
    // ==================== MÉTODOS PRIVADOS ====================
    
    /**
     * Combina datos del ItemCarrito con información completa del Producto
     */
    private List<Map<String, Object>> enriquecerItemsConProductos(List<ItemCarrito> items) {
        return items.stream().map(item -> {
            Map<String, Object> itemEnriquecido = new HashMap<>();
            
            Producto producto = repositorioProducto.buscarPorId(item.getIdProducto())
                .orElse(null);
            
            itemEnriquecido.put("idProducto", item.getIdProducto());
            itemEnriquecido.put("cantidad", item.getCantidad());
            itemEnriquecido.put("precioUnitario", item.getPrecioUnitarioAlAgregar());
            itemEnriquecido.put("subtotal", item.getPrecioUnitarioAlAgregar() * item.getCantidad());
            
            if (producto != null) {
                itemEnriquecido.put("nombreProducto", producto.getNombreProducto());
                itemEnriquecido.put("urlImagen", producto.getUrlImagen());
                itemEnriquecido.put("stockDisponible", producto.getStockDisponible());
            } else {
                itemEnriquecido.put("nombreProducto", "Producto no disponible");
                itemEnriquecido.put("urlImagen", "/images/placeholder.png");
                itemEnriquecido.put("stockDisponible", 0);
            }
            
            return itemEnriquecido;
        }).toList();
    }
}