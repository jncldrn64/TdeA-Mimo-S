package co.edu.tdea.heladosmimos.web.seguridad.casosdeuso;

import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioCarritoCompras;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PROPÓSITO: Capa de seguridad que valida acceso al carrito de compras
 *
 * DECISIÓN TÉCNICA:
 * - Actúa como intermediario entre el Controller y el Servicio
 * - Valida que el usuario esté autenticado antes de operar el carrito
 * - Centraliza las validaciones de negocio relacionadas con permisos
 * - Por ahora permite acceso libre (autenticación se implementará después)
 *
 * DEPENDENCIAS:
 * - ServicioCarritoCompras: Lógica real del carrito
 *
 * CONEXIÓN CON OTROS MÓDULOS:
 * - ControladorCarrito: Llama a este caso de uso en lugar del servicio directo
 * - ServicioCarritoCompras: Ejecuta la lógica de negocio
 *
 * GENERADO POR: Claude - 2024-11-08
 */
@Service
public class CasoDeUsoAccesoCarrito {

    @Autowired
    private ServicioCarritoCompras servicioCarritoCompras;

    /**
     * Ejecuta la acción de obtener el carrito actual
     * 
     * VALIDACIONES:
     * - Usuario autenticado (por implementar con Spring Security)
     * 
     * @return Lista de items en el carrito
     * @throws Exception si el usuario no tiene permisos
     */
    public List<ItemCarrito> ejecutarObtenerCarrito() throws Exception {
        // TODO: Validar autenticación cuando se implemente Spring Security
        // if (!usuarioEstaAutenticado()) {
        //     throw new Exception("Debe iniciar sesión para ver el carrito");
        // }
        
        return servicioCarritoCompras.obtenerItemsDelCarrito();
    }

    /**
     * Ejecuta la acción de obtener el total del carrito
     * 
     * @return Total calculado del carrito
     * @throws Exception si hay error de acceso
     */
    public Double ejecutarObtenerTotal() throws Exception {
        // TODO: Validar autenticación
        return servicioCarritoCompras.obtenerTotalDelCarrito();
    }

    /**
     * Ejecuta la acción de agregar un producto al carrito
     * 
     * VALIDACIONES:
     * - Usuario autenticado
     * - Producto existe y está activo
     * - Stock disponible suficiente
     * 
     * @param idProducto ID del producto a agregar
     * @param cantidad Cantidad a agregar
     * @throws Exception si falla alguna validación
     */
    public void ejecutarAgregarProducto(Long idProducto, Integer cantidad) throws Exception {
        // TODO: Validar autenticación
        
        // Validación básica de parámetros
        if (idProducto == null || idProducto <= 0) {
            throw new Exception("ID de producto inválido");
        }
        
        if (cantidad == null || cantidad <= 0) {
            throw new Exception("Cantidad debe ser mayor a 0");
        }
        
        // Delegar al servicio
        servicioCarritoCompras.agregarProductoAlCarrito(idProducto, cantidad);
    }

    /**
     * Ejecuta la acción de modificar cantidad de un item
     * 
     * @param idProducto ID del producto a modificar
     * @param nuevaCantidad Nueva cantidad deseada
     * @throws Exception si falla la validación
     */
    public void ejecutarModificarCantidad(Long idProducto, Integer nuevaCantidad) throws Exception {
        // TODO: Validar autenticación
        
        if (idProducto == null || idProducto <= 0) {
            throw new Exception("ID de producto inválido");
        }
        
        if (nuevaCantidad == null || nuevaCantidad <= 0) {
            throw new Exception("Cantidad debe ser mayor a 0");
        }
        
        servicioCarritoCompras.modificarCantidadDeItem(idProducto, nuevaCantidad);
    }

    /**
     * Ejecuta la acción de eliminar un producto del carrito
     * 
     * @param idProducto ID del producto a eliminar
     * @throws Exception si falla la operación
     */
    public void ejecutarEliminarProducto(Long idProducto) throws Exception {
        // TODO: Validar autenticación
        
        if (idProducto == null || idProducto <= 0) {
            throw new Exception("ID de producto inválido");
        }
        
        servicioCarritoCompras.eliminarProductoDelCarrito(idProducto);
    }

    /**
     * Ejecuta la acción de vaciar completamente el carrito
     * 
     * @throws Exception si falla la operación
     */
    public void ejecutarVaciarCarrito() throws Exception {
        // TODO: Validar autenticación
        servicioCarritoCompras.vaciarCarritoCompleto();
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Valida si el usuario actual está autenticado
     * 
     * NOTA: Por implementar cuando se integre Spring Security
     * 
     * @return true si está autenticado, false en caso contrario
     */
    private boolean usuarioEstaAutenticado() {
        // TODO: Implementar con Spring Security
        // SecurityContext context = SecurityContextHolder.getContext();
        // Authentication auth = context.getAuthentication();
        // return auth != null && auth.isAuthenticated();
        
        return true; // Por ahora permite acceso libre
    } 
}