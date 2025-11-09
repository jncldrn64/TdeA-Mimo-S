package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoAccesoCarrito;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioCarritoCompras;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para pruebas del carrito sin frontend.
 * Endpoints JSON para validar funcionalidad del backend.
 */
@RestController
@RequestMapping("/api/carrito")
public class ControladorCarritoREST {

    @Autowired
    private CasoDeUsoAccesoCarrito casoDeUsoAccesoCarrito;

    @Autowired
    private ServicioCarritoCompras servicioCarritoCompras;

    @GetMapping
    public ResponseEntity<?> obtenerCarrito() {
        try {
            List<ItemCarrito> items = casoDeUsoAccesoCarrito.ejecutarObtenerCarrito();
            Double total = casoDeUsoAccesoCarrito.ejecutarObtenerTotal();
            Integer cantidadItems = servicioCarritoCompras.contarTotalDeProductos();

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("items", items);
            respuesta.put("total", total);
            respuesta.put("cantidadItems", cantidadItems);
            respuesta.put("success", true);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProducto(@RequestParam Long idProducto,
                                              @RequestParam(defaultValue = "1") Integer cantidad) {
        try {
            casoDeUsoAccesoCarrito.ejecutarAgregarProducto(idProducto, cantidad);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("mensaje", "Producto agregado correctamente");
            respuesta.put("idProducto", idProducto);
            respuesta.put("cantidad", cantidad);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            error.put("tipoExcepcion", e.getClass().getSimpleName());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/modificar")
    public ResponseEntity<?> modificarCantidad(@RequestParam Long idProducto,
                                                @RequestParam Integer nuevaCantidad) {
        try {
            casoDeUsoAccesoCarrito.ejecutarModificarCantidad(idProducto, nuevaCantidad);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("mensaje", "Cantidad modificada correctamente");

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            error.put("tipoExcepcion", e.getClass().getSimpleName());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/eliminar/{idProducto}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long idProducto) {
        try {
            casoDeUsoAccesoCarrito.ejecutarEliminarProducto(idProducto);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("mensaje", "Producto eliminado del carrito");

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<?> vaciarCarrito() {
        try {
            casoDeUsoAccesoCarrito.ejecutarVaciarCarrito();

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("mensaje", "Carrito vaciado correctamente");

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
