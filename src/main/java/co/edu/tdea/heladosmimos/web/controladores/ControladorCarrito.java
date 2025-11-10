package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoAccesoCarrito;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioCarritoCompras;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para gestión del carrito de compras.
 * Maneja peticiones HTTP y delega lógica al caso de uso.
 * Enriquece items con datos completos del producto para la vista.
 */
@Controller
@RequestMapping("/carrito")
public class ControladorCarrito {

    private static final Logger logger = LoggerFactory.getLogger(ControladorCarrito.class);

    @Autowired
    private CasoDeUsoAccesoCarrito casoDeUsoAccesoCarrito;

    @Autowired
    private ServicioCarritoCompras servicioCarritoCompras;

    @Autowired
    private RepositorioProducto repositorioProducto;

    @GetMapping
    public String mostrarCarrito(Model model, HttpSession session) {
        try {
            List<ItemCarrito> items = casoDeUsoAccesoCarrito.ejecutarObtenerCarrito();
            Double total = casoDeUsoAccesoCarrito.ejecutarObtenerTotal();

            List<Map<String, Object>> itemsEnriquecidos = enriquecerItemsConProductos(items);

            model.addAttribute("carrito", itemsEnriquecidos);
            model.addAttribute("totalFinal", total);
            model.addAttribute("idProductoEnEdicion", session.getAttribute("idProductoEnEdicion"));
            model.addAttribute("cantidadTotalItems", servicioCarritoCompras.contarTotalDeProductos());

            return "carrito";
        } catch (Exception e) {
            logger.error("Error al mostrar carrito: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "carrito";
        }
    }

    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam Long idProducto,
                                   @RequestParam(defaultValue = "1") Integer cantidad,
                                   Model model) {
        try {
            casoDeUsoAccesoCarrito.ejecutarAgregarProducto(idProducto, cantidad);
            return "redirect:/carrito";
        } catch (ProductoNoEncontradoException | ProductoNoDisponibleException |
                 StockInsuficienteException | CantidadInvalidaException e) {
            logger.warn("Error al agregar producto {}: {}", idProducto, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "redirect:/catalogo?error=" + e.getMessage();
        }
    }

    @PostMapping("/preparar-edicion")
    public String prepararEdicion(@RequestParam Long idProducto, HttpSession session) {
        session.setAttribute("idProductoEnEdicion", idProducto);
        return "redirect:/carrito";
    }

    @PostMapping("/editar")
    public String editarCantidad(@RequestParam Long idProducto,
                                  @RequestParam Integer nuevaCantidad,
                                  HttpSession session,
                                  Model model) {
        try {
            casoDeUsoAccesoCarrito.ejecutarModificarCantidad(idProducto, nuevaCantidad);
            session.removeAttribute("idProductoEnEdicion");
            return "redirect:/carrito";
        } catch (ProductoNoEncontradoException | StockInsuficienteException |
                 CantidadInvalidaException e) {
            logger.warn("Error al editar cantidad producto {}: {}", idProducto, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "redirect:/carrito?error=" + e.getMessage();
        }
    }

    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Long idProducto) {
        try {
            casoDeUsoAccesoCarrito.ejecutarEliminarProducto(idProducto);
            return "redirect:/carrito";
        } catch (ProductoNoEncontradoException e) {
            logger.warn("Error al eliminar producto {}: {}", idProducto, e.getMessage());
            return "redirect:/carrito?error=" + e.getMessage();
        }
    }

    @PostMapping("/vaciar")
    public String vaciarCarrito() {
        logger.info("Vaciando carrito");
        casoDeUsoAccesoCarrito.ejecutarVaciarCarrito();
        return "redirect:/carrito";
    }

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
