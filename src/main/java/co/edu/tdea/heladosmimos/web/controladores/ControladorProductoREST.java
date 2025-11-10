package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.casosdeuso.*;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de inventario (RF-01).
 * Excepciones manejadas por @ControllerAdvice global (SOLID).
 */
@RestController
@RequestMapping("/api/productos")
public class ControladorProductoREST {

    @Autowired
    private CasoDeUsoConsultarProductos casoDeUsoConsultarProductos;

    @Autowired
    private CasoDeUsoRegistrarProducto casoDeUsoRegistrarProducto;

    @Autowired
    private CasoDeUsoActualizarProducto casoDeUsoActualizarProducto;

    @Autowired
    private CasoDeUsoGestionarStock casoDeUsoGestionarStock;

    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(casoDeUsoConsultarProductos.ejecutarListarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Producto>> listarActivos() {
        return ResponseEntity.ok(casoDeUsoConsultarProductos.ejecutarListarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id)
            throws ProductoNoEncontradoException {
        return ResponseEntity.ok(casoDeUsoConsultarProductos.ejecutarBuscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> registrarProducto(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam Double precio,
            @RequestParam(defaultValue = "0") Integer stock,
            @RequestParam(required = false) String urlImagen)
            throws DatosProductoInvalidosException, ProductoDuplicadoException,
                   PrecioInvalidoException, StockNegativoException {

        Producto producto = casoDeUsoRegistrarProducto.ejecutar(nombre, descripcion,
                                                                precio, stock, urlImagen);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Producto registrado exitosamente");
        respuesta.put("producto", producto);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam Double precio,
            @RequestParam(required = false) String urlImagen)
            throws ProductoNoEncontradoException, DatosProductoInvalidosException,
                   ProductoDuplicadoException, PrecioInvalidoException {

        Producto producto = casoDeUsoActualizarProducto.ejecutar(id, nombre, descripcion,
                                                                 precio, urlImagen);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Producto actualizado exitosamente");
        respuesta.put("producto", producto);

        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer stock)
            throws ProductoNoEncontradoException, StockNegativoException {

        Producto producto = casoDeUsoGestionarStock.ejecutarActualizarStock(id, stock);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Stock actualizado exitosamente");
        respuesta.put("producto", producto);

        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activarProducto(@PathVariable Long id)
            throws ProductoNoEncontradoException {

        Producto producto = casoDeUsoGestionarStock.ejecutarActivar(id);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Producto activado");
        respuesta.put("producto", producto);

        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarProducto(@PathVariable Long id)
            throws ProductoNoEncontradoException {

        Producto producto = casoDeUsoGestionarStock.ejecutarDesactivar(id);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Producto desactivado");
        respuesta.put("producto", producto);

        return ResponseEntity.ok(respuesta);
    }

    // ==================== ENDPOINT DE PRUEBAS (LEGACY) ====================

    @PostMapping("/crear-prueba")
    public ResponseEntity<?> crearProductoDePrueba(
            @RequestParam(defaultValue = "Helado de Vainilla") String nombre,
            @RequestParam(defaultValue = "5000") Double precio,
            @RequestParam(defaultValue = "50") Integer stock)
            throws DatosProductoInvalidosException, ProductoDuplicadoException,
                   PrecioInvalidoException, StockNegativoException {

        Producto producto = casoDeUsoRegistrarProducto.ejecutar(
            nombre, "Producto de prueba", precio, stock, null);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Producto de prueba creado");
        respuesta.put("producto", producto);

        return ResponseEntity.ok(respuesta);
    }
}
