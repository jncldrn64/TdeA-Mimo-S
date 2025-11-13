package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import co.edu.tdea.heladosmimos.web.entidades.Pedido;
import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoAccesoCarrito;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioCarritoCompras;
import jakarta.servlet.http.HttpSession;
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

    @Autowired
    private co.edu.tdea.heladosmimos.web.puertos.RepositorioUsuario repositorioUsuario;

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

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarCantidad(
            @RequestBody Map<String, Object> datos)
            throws ProductoNoEncontradoException, StockInsuficienteException,
                   CantidadInvalidaException {

        Long idProducto = Long.valueOf(datos.get("idProducto").toString());
        Integer nuevaCantidad = Integer.valueOf(datos.get("cantidad").toString());

        casoDeUsoAccesoCarrito.ejecutarModificarCantidad(idProducto, nuevaCantidad);

        // Obtener item actualizado para devolver
        List<ItemCarrito> items = casoDeUsoAccesoCarrito.ejecutarObtenerCarrito();
        ItemCarrito itemActualizado = items.stream()
            .filter(i -> i.getIdProducto().equals(idProducto))
            .findFirst()
            .orElse(null);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Cantidad actualizada correctamente");

        if (itemActualizado != null) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("idProducto", itemActualizado.getIdProducto());
            itemData.put("cantidad", itemActualizado.getCantidad());
            itemData.put("subtotal", itemActualizado.getPrecioUnitarioAlAgregar() * itemActualizado.getCantidad());
            respuesta.put("item", itemData);
        }

        // Calcular resumen
        Double subtotal = items.stream()
            .mapToDouble(i -> i.getPrecioUnitarioAlAgregar() * i.getCantidad())
            .sum();
        Double costoEnvio = 5000.0;
        Double iva = subtotal * 0.19;
        Double descuento = 0.0;
        Double total = subtotal + costoEnvio + iva - descuento;

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("subtotal", subtotal);
        resumen.put("costoEnvio", costoEnvio);
        resumen.put("iva", iva);
        resumen.put("descuento", descuento);
        resumen.put("total", total);
        respuesta.put("resumen", resumen);

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
    public ResponseEntity<?> procesarCheckout(HttpSession sesion)
            throws CarritoVacioException, StockInsuficienteException,
                   ProductoNoEncontradoException, ProductoNoDisponibleException,
                   ConflictoConcurrenciaException {

        // Obtener usuario de la sesión
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        if (usuario == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Usuario no autenticado");
            return ResponseEntity.status(401).body(error);
        }

        // Recargar usuario desde BD para obtener datos más recientes (incluyendo datos de envío)
        Usuario usuarioDB = repositorioUsuario.buscarPorId(usuario.getIdUsuario());
        if (usuarioDB != null) {
            usuario = usuarioDB;
            sesion.setAttribute("usuario", usuario); // Actualizar sesión
        }

        // Validar si tiene datos de envío completos
        boolean requiereDatosEnvio = esNuloOVacio(usuario.getTelefono()) ||
                                     esNuloOVacio(usuario.getDireccion()) ||
                                     esNuloOVacio(usuario.getCiudad()) ||
                                     esNuloOVacio(usuario.getEstadoProvincia());

        if (requiereDatosEnvio) {
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", false);
            respuesta.put("requiereDatosEnvio", true);
            respuesta.put("mensaje", "Completa tus datos de envío para continuar");
            return ResponseEntity.ok(respuesta);
        }

        // Procesar checkout y crear pedido
        Pedido pedido = servicioCarritoCompras.procesarCheckout(usuario);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Checkout procesado exitosamente. Stock actualizado y carrito vaciado.");
        respuesta.put("idPedido", pedido.getIdPedido());
        respuesta.put("total", pedido.getTotal());

        return ResponseEntity.ok(respuesta);
    }

    private boolean esNuloOVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
