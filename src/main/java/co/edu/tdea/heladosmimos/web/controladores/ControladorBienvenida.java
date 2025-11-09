package co.edu.tdea.heladosmimos.web.controladores;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller de bienvenida - muestra endpoints disponibles.
 */
@RestController
public class ControladorBienvenida {

    @GetMapping("/")
    public Map<String, Object> bienvenida() {
        Map<String, Object> info = new HashMap<>();
        info.put("proyecto", "Sistema Helados Mimo's");
        info.put("version", "1.0.0");
        info.put("status", "running");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("productos", "GET /api/productos");
        endpoints.put("productosActivos", "GET /api/productos/activos");
        endpoints.put("crearProductoPrueba", "POST /api/productos/crear-prueba");
        endpoints.put("carrito", "GET /api/carrito");
        endpoints.put("agregarAlCarrito", "POST /api/carrito/agregar?idProducto={id}&cantidad={n}");
        endpoints.put("modificarCantidad", "POST /api/carrito/modificar?idProducto={id}&nuevaCantidad={n}");
        endpoints.put("eliminarDelCarrito", "DELETE /api/carrito/eliminar/{id}");
        endpoints.put("vaciarCarrito", "DELETE /api/carrito/vaciar");

        info.put("endpoints", endpoints);

        Map<String, String> ejemplos = new HashMap<>();
        ejemplos.put("1_crear_producto", "curl -X POST 'http://localhost:8080/api/productos/crear-prueba?nombre=Helado&precio=5000&stock=50'");
        ejemplos.put("2_listar_productos", "curl http://localhost:8080/api/productos");
        ejemplos.put("3_agregar_al_carrito", "curl -X POST 'http://localhost:8080/api/carrito/agregar?idProducto=1&cantidad=2'");
        ejemplos.put("4_ver_carrito", "curl http://localhost:8080/api/carrito");

        info.put("ejemplos", ejemplos);

        return info;
    }
}
