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

        Map<String, String> endpointsAuth = new HashMap<>();
        endpointsAuth.put("validarCorreo", "POST /api/auth/validar-correo");
        endpointsAuth.put("registrar", "POST /api/auth/registrar");
        endpointsAuth.put("login", "POST /api/auth/login");
        endpointsAuth.put("logout", "POST /api/auth/logout");
        endpointsAuth.put("sesionActual", "GET /api/auth/session");

        Map<String, String> endpointsInventario = new HashMap<>();
        endpointsInventario.put("listar", "GET /api/productos");
        endpointsInventario.put("listarActivos", "GET /api/productos/activos");
        endpointsInventario.put("obtenerPorId", "GET /api/productos/{id}");
        endpointsInventario.put("registrar", "POST /api/productos");
        endpointsInventario.put("actualizar", "PUT /api/productos/{id}");
        endpointsInventario.put("actualizarStock", "PATCH /api/productos/{id}/stock");
        endpointsInventario.put("activar", "PATCH /api/productos/{id}/activar");
        endpointsInventario.put("desactivar", "PATCH /api/productos/{id}/desactivar");

        Map<String, String> endpointsCarrito = new HashMap<>();
        endpointsCarrito.put("ver", "GET /api/carrito");
        endpointsCarrito.put("agregar", "POST /api/carrito/agregar?idProducto={id}&cantidad={n}");
        endpointsCarrito.put("modificar", "POST /api/carrito/modificar?idProducto={id}&nuevaCantidad={n}");
        endpointsCarrito.put("eliminar", "DELETE /api/carrito/eliminar/{id}");
        endpointsCarrito.put("vaciar", "DELETE /api/carrito/vaciar");
        endpointsCarrito.put("checkout", "POST /api/carrito/checkout");

        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("autenticacion", endpointsAuth);
        endpoints.put("inventario", endpointsInventario);
        endpoints.put("carrito", endpointsCarrito);

        info.put("endpoints", endpoints);

        Map<String, String> ejemplos = new HashMap<>();
        ejemplos.put("1_registrar_usuario", "curl -X POST 'http://localhost:8080/api/auth/registrar' -d 'correo=test@mimo.com&contrasena=Test123&nombre=Juan&apellido=Perez&telefono=3001234567&direccion=Calle%2010&nit=123456'");
        ejemplos.put("2_login", "curl -X POST 'http://localhost:8080/api/auth/login' -d 'correo=test@mimo.com&contrasena=Test123'");
        ejemplos.put("3_registrar_producto", "curl -X POST 'http://localhost:8080/api/productos?nombre=Helado%20Vainilla&precio=5500&stock=100&descripcion=Delicioso'");
        ejemplos.put("4_listar_productos", "curl http://localhost:8080/api/productos");
        ejemplos.put("5_actualizar_stock", "curl -X PATCH 'http://localhost:8080/api/productos/1/stock?stock=200'");
        ejemplos.put("6_agregar_al_carrito", "curl -X POST 'http://localhost:8080/api/carrito/agregar?idProducto=1&cantidad=2'");
        ejemplos.put("7_ver_carrito", "curl http://localhost:8080/api/carrito");

        info.put("ejemplos", ejemplos);

        return info;
    }
}
