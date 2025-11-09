package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de productos (pruebas).
 * Permite crear productos de prueba para validar el carrito.
 */
@RestController
@RequestMapping("/api/productos")
public class ControladorProductoREST {

    @Autowired
    private RepositorioProducto repositorioProducto;

    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(repositorioProducto.buscarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Producto>> listarActivos() {
        return ResponseEntity.ok(repositorioProducto.buscarProductosActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return repositorioProducto.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/crear-prueba")
    public ResponseEntity<?> crearProductoDePrueba(
            @RequestParam(defaultValue = "Helado de Vainilla") String nombre,
            @RequestParam(defaultValue = "5000") Double precio,
            @RequestParam(defaultValue = "50") Integer stock) {

        Producto producto = new Producto();
        producto.setNombreProducto(nombre);
        producto.setDescripcionDetallada("Producto de prueba");
        producto.setPrecioUnitario(precio);
        producto.setStockDisponible(stock);
        producto.setUrlImagen("/img/default.jpg");
        producto.setFechaIngreso(LocalDateTime.now());
        producto.setEstaActivo(true);

        Producto guardado = repositorioProducto.guardar(producto);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Producto de prueba creado");
        respuesta.put("producto", guardado);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (repositorioProducto.existePorId(id)) {
            repositorioProducto.eliminar(id);
            return ResponseEntity.ok(Map.of("success", true, "mensaje", "Producto eliminado"));
        }
        return ResponseEntity.notFound().build();
    }
}
