package co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales;

import co.edu.tdea.heladosmimos.web.entidades.Carrito;
import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioCarrito;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioItemCarrito;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PROPÓSITO: Gestión del carrito de compras con enfoque híbrido
 *
 * DECISIÓN TÉCNICA (ENFOQUE HÍBRIDO):
 * - @SessionScope: Cada usuario tiene su carrito en memoria durante la sesión
 * - Persistencia en BD: El carrito se guarda en SQL Server cuando:
 *   1. Usuario confirma el pedido
 *   2. Usuario cierra sesión (opcional, para recuperar después)
 *   3. Cada vez que se modifica (tiempo real)
 * 
 * VENTAJAS DE ESTE ENFOQUE:
 * - Rápido: Operaciones en memoria durante navegación
 * - Persistente: Se guarda en BD para no perder datos
 * - Recuperable: Usuario puede retomar carrito en otra sesión
 * - Escalable: Fácil migrar a Redis después si es necesario
 *
 * DEPENDENCIAS:
 * - RepositorioProducto: Validar existencia y stock
 * - RepositorioCarrito: Persistir carrito en BD
 * - RepositorioItemCarrito: Persistir items en BD
 *
 * CONEXIÓN CON OTROS MÓDULOS:
 * - CasoDeUsoAccesoCarrito: Valida autenticación antes de usar este servicio
 * - ControladorCarrito: Recibe peticiones HTTP y delega aquí
 * - ServicioPedidos: Consume este carrito al confirmar compra
 *
 * GENERADO POR: Claude - 2024-11-08
 */
@Service
@SessionScope
public class ServicioCarritoCompras {

    // ==================== ALMACENAMIENTO EN SESIÓN ====================
    
    /**
     * Items del carrito almacenados en memoria durante la sesión HTTP
     * 
     * DECISIÓN: Se mantiene en memoria para operaciones rápidas
     * Se sincroniza con BD al confirmar pedido o cerrar sesión
     */
    private final List<ItemCarrito> itemsDelCarrito = new ArrayList<>();
    
    /**
     * Total calculado del carrito actual
     * Se recalcula cada vez que se modifica el carrito
     */
    private Double totalCalculado = 0.0;
    
    /**
     * ID del carrito en base de datos (si ya fue persistido)
     * null si aún no se ha guardado en BD
     */
    private Long idCarritoPersistido = null;

    // ==================== DEPENDENCIAS ====================
    
    @Autowired
    private RepositorioProducto repositorioProducto;
    
    @Autowired
    private RepositorioCarrito repositorioCarrito;
    
    @Autowired
    private RepositorioItemCarrito repositorioItemCarrito;

    // ==================== MÉTODOS PÚBLICOS ====================

    /**
     * Agrega un producto al carrito o incrementa cantidad si ya existe
     *
     * FLUJO:
     * 1. Validar que el producto existe y está activo
     * 2. Validar stock disponible
     * 3. Buscar si el producto ya está en el carrito
     * 4. Si existe: incrementar cantidad
     * 5. Si no existe: crear nuevo item
     * 6. Recalcular total
     * 
     * VALIDACIONES:
     * - Producto existe en BD
     * - Producto está activo
     * - Stock disponible suficiente
     * 
     * @param idProducto ID del producto a agregar
     * @param cantidad Cantidad a agregar
     * @throws RuntimeException si falla alguna validación
     */
    public void agregarProductoAlCarrito(Long idProducto, Integer cantidad) {
        // Validar que el producto existe
        Producto producto = repositorioProducto.buscarPorId(idProducto)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + idProducto));

        // Validar que el producto está activo
        if (!producto.getEstaActivo()) {
            throw new RuntimeException("Producto no disponible");
        }

        // Validar stock disponible
        if (producto.getStockDisponible() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " +
                producto.getStockDisponible());
        }

        // Buscar si el producto ya está en el carrito
        ItemCarrito itemExistente = buscarItemPorProducto(idProducto);
        
        if (itemExistente != null) {
            // Producto ya existe: incrementar cantidad
            Integer nuevaCantidad = itemExistente.getCantidad() + cantidad;
            
            // Validar que la nueva cantidad no exceda el stock
            if (producto.getStockDisponible() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente para cantidad total: " + nuevaCantidad);
            }
            
            itemExistente.setCantidad(nuevaCantidad);
        } else {
            // Producto nuevo: crear item
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setIdProducto(idProducto);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitarioAlAgregar(producto.getPrecioUnitario());
            
            // Si ya hay un carrito persistido, asociar el item
            if (idCarritoPersistido != null) {
                nuevoItem.setIdCarrito(idCarritoPersistido);
            }
            
            itemsDelCarrito.add(nuevoItem);
        }

        // Recalcular el total
        recalcularTotal();
    }

    /**
     * Modifica la cantidad de un producto ya agregado
     * 
     * VALIDACIONES:
     * - Cantidad mayor a 0
     * - Producto existe
     * - Stock suficiente para nueva cantidad
     * 
     * @param idProducto ID del producto a modificar
     * @param nuevaCantidad Nueva cantidad deseada
     * @throws RuntimeException si falla alguna validación
     */
    public void modificarCantidadDeItem(Long idProducto, Integer nuevaCantidad) {
        // Validar cantidad mínima
        if (nuevaCantidad < 1) {
            throw new RuntimeException("Cantidad debe ser mayor a 0");
        }

        // Validar que el producto existe
        Producto producto = repositorioProducto.buscarPorId(idProducto)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Validar stock disponible
        if (producto.getStockDisponible() < nuevaCantidad) {
            throw new RuntimeException("Stock insuficiente para cantidad: " + nuevaCantidad);
        }

        // Buscar el item en el carrito
        ItemCarrito item = buscarItemPorProducto(idProducto);
        if (item != null) {
            item.setCantidad(nuevaCantidad);
            recalcularTotal();
        } else {
            throw new RuntimeException("Producto no encontrado en el carrito");
        }
    }

    /**
     * Elimina un producto específico del carrito
     * 
     * @param idProducto ID del producto a eliminar
     */
    public void eliminarProductoDelCarrito(Long idProducto) {
        itemsDelCarrito.removeIf(item -> item.getIdProducto().equals(idProducto));
        recalcularTotal();
    }

    /**
     * Vacía completamente el carrito
     * 
     * NOTA: Solo limpia la sesión, no elimina de BD
     * Para eliminar de BD usar vaciarCarritoYPersistir()
     */
    public void vaciarCarritoCompleto() {
        itemsDelCarrito.clear();
        totalCalculado = 0.0;
    }

    /**
     * Obtiene todos los items del carrito actual
     * 
     * @return Copia de la lista de items (no modificable directamente)
     */
    public List<ItemCarrito> obtenerItemsDelCarrito() {
        return new ArrayList<>(itemsDelCarrito);
    }

    /**
     * Retorna el total calculado del carrito
     * 
     * @return Total en pesos colombianos
     */
    public Double obtenerTotalDelCarrito() {
        return totalCalculado;
    }

    /**
     * Cuenta total de productos (suma de cantidades)
     * 
     * UTILIDAD: Mostrar badge en navbar con cantidad de items
     * 
     * @return Suma de todas las cantidades
     */
    public Integer contarTotalDeProductos() {
        return itemsDelCarrito.stream()
            .mapToInt(ItemCarrito::getCantidad)
            .sum();
    }

    // ==================== MÉTODOS DE PERSISTENCIA ====================

    /**
     * Persiste el carrito actual en la base de datos
     * 
     * FLUJO:
     * 1. Crear o actualizar registro en tabla carritos
     * 2. Guardar cada item en tabla items_carrito
     * 3. Almacenar ID del carrito persistido
     * 
     * UTILIDAD:
     * - Al confirmar pedido
     * - Al cerrar sesión (para recuperar después)
     * - Periódicamente durante navegación (opcional)
     * 
     * @param idUsuario ID del usuario propietario del carrito
     * @return ID del carrito guardado en BD
     */
    public Long persistirCarritoEnBaseDeDatos(Long idUsuario) {
        // Buscar si ya existe un carrito para este usuario
        Carrito carrito = repositorioCarrito.buscarPorIdUsuario(idUsuario)
            .orElse(new Carrito());
        
        // Configurar carrito
        carrito.setIdUsuario(idUsuario);
        carrito.setFechaCreacion(carrito.getFechaCreacion() != null 
            ? carrito.getFechaCreacion() 
            : LocalDateTime.now());
        
        // Guardar carrito en BD
        carrito = repositorioCarrito.guardar(carrito);
        idCarritoPersistido = carrito.getIdCarrito();
        
        // Eliminar items antiguos del carrito en BD
        repositorioItemCarrito.eliminarPorIdCarrito(idCarritoPersistido);
        
        // Guardar items actuales en BD
        for (ItemCarrito item : itemsDelCarrito) {
            item.setIdCarrito(idCarritoPersistido);
            repositorioItemCarrito.guardar(item);
        }
        
        return idCarritoPersistido;
    }

    /**
     * Carga el carrito de un usuario desde la base de datos
     * 
     * UTILIDAD: Recuperar carrito abandonado en sesión anterior
     * 
     * @param idUsuario ID del usuario
     * @return true si se cargó un carrito, false si no existe
     */
    public boolean cargarCarritoDesdeBaseDeDatos(Long idUsuario) {
        // Buscar carrito del usuario
        Carrito carrito = repositorioCarrito.buscarPorIdUsuario(idUsuario)
            .orElse(null);
        
        if (carrito == null) {
            return false;
        }
        
        // Limpiar carrito actual en memoria
        itemsDelCarrito.clear();
        
        // Cargar items desde BD
        List<ItemCarrito> itemsGuardados = repositorioItemCarrito
            .buscarPorIdCarrito(carrito.getIdCarrito());
        
        itemsDelCarrito.addAll(itemsGuardados);
        idCarritoPersistido = carrito.getIdCarrito();
        
        // Recalcular total
        recalcularTotal();
        
        return true;
    }

    /**
     * Vacía el carrito en memoria Y en base de datos
     * 
     * UTILIDAD: Después de confirmar un pedido
     * 
     * @param idUsuario ID del usuario
     */
    public void vaciarCarritoYPersistir(Long idUsuario) {
        // Vaciar memoria
        vaciarCarritoCompleto();
        
        // Buscar carrito en BD
        Carrito carrito = repositorioCarrito.buscarPorIdUsuario(idUsuario)
            .orElse(null);
        
        if (carrito != null) {
            // Eliminar items de la BD
            repositorioItemCarrito.eliminarPorIdCarrito(carrito.getIdCarrito());
            
            // Opcional: Eliminar el carrito completo
            // repositorioCarrito.eliminar(carrito.getIdCarrito());
        }
        
        idCarritoPersistido = null;
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Busca un item en el carrito por ID de producto
     * 
     * @param idProducto ID del producto a buscar
     * @return Item encontrado o null si no existe
     */
    private ItemCarrito buscarItemPorProducto(Long idProducto) {
        return itemsDelCarrito.stream()
            .filter(item -> item.getIdProducto().equals(idProducto))
            .findFirst()
            .orElse(null);
    }

    /**
     * Recalcula el total del carrito
     * 
     * FÓRMULA: Suma de (precioUnitario * cantidad) de cada item
     */
    private void recalcularTotal() {
        totalCalculado = itemsDelCarrito.stream()
            .mapToDouble(item -> item.getPrecioUnitarioAlAgregar() * item.getCantidad())
            .sum();
    }
}