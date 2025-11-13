package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.entidades.enums.RolUsuario;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controlador REST para operaciones CRUD de productos por administradores.
 * Solo accesible para usuarios con rol ADMINISTRADOR_VENTAS.
 */
@RestController
@RequestMapping("/admin/api/productos")
public class ControladorAdminREST {

    private static final Logger logger = LoggerFactory.getLogger(ControladorAdminREST.class);

    @Autowired
    private RepositorioProducto repositorioProducto;

    /**
     * Crear un nuevo producto.
     */
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto, HttpSession session) {
        // Validar autenticación y rol
        if (!esAdministrador(session)) {
            logger.warn("Intento no autorizado de crear producto");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            // Establecer valores por defecto
            producto.setFechaCreacion(LocalDateTime.now());
            if (producto.getEstaActivo() == null) {
                producto.setEstaActivo(true);
            }

            // Guardar producto
            Producto productoGuardado = repositorioProducto.guardar(producto);

            logger.info("Administrador creó nuevo producto: {} (ID: {})",
                productoGuardado.getNombreProducto(), productoGuardado.getIdProducto());

            return ResponseEntity.ok(productoGuardado);

        } catch (Exception e) {
            logger.error("Error al crear producto: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear producto: " + e.getMessage());
        }
    }

    /**
     * Actualizar un producto existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto producto,
            HttpSession session) {

        // Validar autenticación y rol
        if (!esAdministrador(session)) {
            logger.warn("Intento no autorizado de actualizar producto ID: {}", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            // Buscar producto existente
            Producto productoExistente = repositorioProducto.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Actualizar campos
            productoExistente.setNombreProducto(producto.getNombreProducto());
            productoExistente.setDescripcionDetallada(producto.getDescripcionDetallada());
            productoExistente.setPrecioUnitario(producto.getPrecioUnitario());
            productoExistente.setStockDisponible(producto.getStockDisponible());
            productoExistente.setUrlImagen(producto.getUrlImagen());
            productoExistente.setEstaActivo(producto.getEstaActivo());

            // Guardar cambios
            Producto productoActualizado = repositorioProducto.guardar(productoExistente);

            logger.info("Administrador actualizó producto: {} (ID: {})",
                productoActualizado.getNombreProducto(), productoActualizado.getIdProducto());

            return ResponseEntity.ok(productoActualizado);

        } catch (Exception e) {
            logger.error("Error al actualizar producto ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar producto: " + e.getMessage());
        }
    }

    /**
     * Eliminar un producto.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id, HttpSession session) {
        // Validar autenticación y rol
        if (!esAdministrador(session)) {
            logger.warn("Intento no autorizado de eliminar producto ID: {}", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            // Verificar que el producto existe
            Producto producto = repositorioProducto.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Eliminar producto
            repositorioProducto.eliminar(id);

            logger.info("Administrador eliminó producto: {} (ID: {})",
                producto.getNombreProducto(), producto.getIdProducto());

            return ResponseEntity.ok("Producto eliminado exitosamente");

        } catch (Exception e) {
            logger.error("Error al eliminar producto ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar producto: " + e.getMessage());
        }
    }

    /**
     * Validar si el usuario es administrador.
     */
    private boolean esAdministrador(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        return usuario != null && usuario.getRol() == RolUsuario.ADMINISTRADOR_VENTAS;
    }
}
