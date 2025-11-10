package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoAccesoCarrito;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioCarritoCompras;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para pruebas del carrito sin frontend.
 * Excepciones manejadas por @ControllerAdvice global (SOLID).
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
        List<ItemCarrito> items = casoDeUsoAccesoCarrito.ejecutarObtenerCarrito();
        Double total = casoDeUsoAccesoCarrito.ejecutarObtenerTotal();
        Integer cantidadItems = servicioCarritoCompras.contarTotalDeProductos();
        List<String> advertencias = servicioCarritoCompras.validarDisponibilidadItems();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("items", items);
        respuesta.put("total", total);
        respuesta.put("cantidadItems", cantidadItems);
        respuesta.put("success", true);

        if (!advertencias.isEmpty()) {
            respuesta.put("advertencias", advertencias);
        }

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProducto(
            @RequestParam Long idProducto,
            @RequestParam(defaultValue = "1") Integer cantidad)
            throws ProductoNoEncontradoException, ProductoNoDisponibleException,
                   StockInsuficienteException, CantidadInvalidaException {

        casoDeUsoAccesoCarrito.ejecutarAgregarProducto(idProducto, cantidad);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Producto agregado correctamente");
        respuesta.put("idProducto", idProducto);
        respuesta.put("cantidad", cantidad);

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/modificar")
    public ResponseEntity<?> modificarCantidad(
            @RequestParam Long idProducto,
            @RequestParam Integer nuevaCantidad)
            throws ProductoNoEncontradoException, StockInsuficienteException,
                   CantidadInvalidaException {

        casoDeUsoAccesoCarrito.ejecutarModificarCantidad(idProducto, nuevaCantidad);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Cantidad modificada correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/eliminar/{idProducto}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long idProducto)
            throws ProductoNoEncontradoException {

        casoDeUsoAccesoCarrito.ejecutarEliminarProducto(idProducto);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Producto eliminado del carrito");

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<?> vaciarCarrito() {
        casoDeUsoAccesoCarrito.ejecutarVaciarCarrito();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Carrito vaciado correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> procesarCheckout()
            throws CarritoVacioException, StockInsuficienteException,
                   ProductoNoEncontradoException, ProductoNoDisponibleException,
                   ConflictoConcurrenciaException {

        servicioCarritoCompras.procesarCheckout();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Checkout procesado exitosamente. Stock actualizado y carrito vaciado.");

        return ResponseEntity.ok(respuesta);
    }
}
