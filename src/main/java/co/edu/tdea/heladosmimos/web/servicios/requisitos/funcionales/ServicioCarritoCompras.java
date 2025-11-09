// ============================================================
// ARCHIVO 3: ServicioCarritoCompras.java
// Ubicación: src/main/java/co/edu/tdea/heladosmimos/web/servicios/requisitos/funcionales/
// ============================================================
package co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales;

import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

/**
 * PROPÓSITO: Gestión del carrito de compras en sesión HTTP
 * 
 * DECISIÓN TÉCNICA: 
 * - @SessionScope: Cada usuario tiene su propio carrito en memoria durante la sesión
 * - NO persiste en BD hasta confirmar pedido (según RF-05 del documento)
 * - Valida stock en tiempo real antes de agregar
 * 
 * DEPENDENCIAS:
 * - RepositorioProducto: Para validar existencia y stock
 * 
 * CONEXIÓN CON OTROS MÓDULOS:
 * - CasoDeUsoAccesoCarrito: Valida autenticación antes de usar este servicio
 * - ControladorCarrito: Recibe peticiones HTTP y delega aquí
 * 
 * GENERADO POR: Claude - 2024-11-08
 */
@Service
@SessionScope
public class ServicioCarritoCompras {
    
    private final List<ItemCarrito> itemsDelCarrito = new ArrayList<>();
    private Double totalCalculado = 0.0;
    
    @Autowired
    private RepositorioProducto repositorioProducto;
    
    /**
     * Agrega un producto al carrito o incrementa cantidad si ya existe
     * 
     * VALIDACIONES:
     * - Producto existe en BD
     * - Producto está activo
     * - Stock disponible suficiente
     */
    public void agregarProductoAlCarrito(Long idProducto, Integer cantidad) {
        Producto producto = repositorioProducto.buscarPorId(idProducto)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + idProducto));
        
        if (!producto.getEstaActivo()) {
            throw new RuntimeException("Producto no disponible");
        }
        
        if (producto.getStockDisponible() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + 
                producto.getStockDisponible());
        }
        
        ItemCarrito itemExistente = buscarItemPorProducto(idProducto);
        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setIdProducto(idProducto);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitarioAlAgregar(producto.getPrecioUnitario());
            itemsDelCarrito.add(nuevoItem);
        }
        
        recalcularTotal();
    }
    
    /**
     * Modifica la cantidad de un producto ya agregado
     */
    public void modificarCantidadDeItem(Long idProducto, Integer nuevaCantidad) {
        if (nuevaCantidad < 1) {
            throw new RuntimeException("Cantidad debe ser mayor a 0");
        }
        
        Producto producto = repositorioProducto.buscarPorId(idProducto)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (producto.getStockDisponible() < nuevaCantidad) {
            throw new RuntimeException("Stock insuficiente para cantidad: " + nuevaCantidad);
        }
        
        ItemCarrito item = buscarItemPorProducto(idProducto);
        if (item != null) {
            item.setCantidad(nuevaCantidad);
            recalcularTotal();
        }
    }
    
    /**
     * Elimina un producto específico del carrito
     */
    public void eliminarProductoDelCarrito(Long idProducto) {
        itemsDelCarrito.removeIf(item -> item.getIdProducto().equals(idProducto));
        recalcularTotal();
    }
    
    /**
     * Vacía completamente el carrito
     */
    public void vaciarCarritoCompleto() {
        itemsDelCarrito.clear();
        totalCalculado = 0.0;
    }
    
    /**
     * Obtiene todos los items del carrito actual
     */
    public List<ItemCarrito> obtenerItemsDelCarrito() {
        return new ArrayList<>(itemsDelCarrito);
    }
    
    /**
     * Retorna el total calculado del carrito
     */
    public Double obtenerTotalDelCarrito() {
        return totalCalculado;
    }
    
    /**
     * Cuenta total de productos (suma de cantidades)
     */
    public Integer contarTotalDeProductos() {
        return itemsDelCarrito.stream()
            .mapToInt(ItemCarrito::getCantidad)
            .sum();
    }
    
    // ==================== MÉTODOS PRIVADOS ====================
    
    private ItemCarrito buscarItemPorProducto(Long idProducto) {
        return itemsDelCarrito.stream()
            .filter(item -> item.getIdProducto().equals(idProducto))
            .findFirst()
            .orElse(null);
    }
    
    private void recalcularTotal() {
        totalCalculado = itemsDelCarrito.stream()
            .mapToDouble(item -> item.getPrecioUnitarioAlAgregar() * item.getCantidad())
            .sum();
    }
}
