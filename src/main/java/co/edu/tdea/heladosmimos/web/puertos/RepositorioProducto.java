package co.edu.tdea.heladosmimos.web.puertos;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import java.util.List;
import java.util.Optional;

/**
 * PROPÓSITO: Contrato para acceso a datos de productos
 *
 * DECISIÓN TÉCNICA:
 * - Interface para desacoplar lógica de negocio de persistencia
 * - Permite múltiples implementaciones (JPA, MongoDB, APIs externas)
 * - Sigue patrón de arquitectura hexagonal
 *
 * DEPENDENCIAS:
 * - Entidad Producto
 *
 * CONEXIÓN CON OTROS MÓDULOS:
 * - AdaptadorRepositorioProducto: Implementación concreta con JPA
 * - ServicioCarritoCompras: Valida existencia y stock de productos
 * - ServicioCatalogo: Lista productos disponibles
 *
 * GENERADO POR: Claude - 2024-11-08
 */
public interface RepositorioProducto {
    
    /**
     * Busca un producto por su ID
     * 
     * @param idProducto ID del producto a buscar
     * @return Optional con el producto si existe, vacío si no
     */
    Optional<Producto> buscarPorId(Long idProducto);
    
    /**
     * Guarda o actualiza un producto en la base de datos
     * 
     * @param producto Producto a guardar
     * @return Producto guardado con ID generado (si es nuevo)
     */
    Producto guardar(Producto producto);
    
    /**
     * Obtiene todos los productos de la base de datos
     * 
     * UTILIDAD: Listar catálogo completo
     * 
     * @return Lista de todos los productos (puede estar vacía)
     */
    List<Producto> buscarTodos();
    
    /**
     * Busca productos activos (disponibles para venta)
     * 
     * UTILIDAD: Mostrar solo productos disponibles en catálogo
     * 
     * @return Lista de productos con estaActivo = true
     */
    List<Producto> buscarProductosActivos();
    
    /**
     * Busca productos por nombre (búsqueda parcial)
     * 
     * UTILIDAD: Barra de búsqueda en el catálogo
     * 
     * @param nombre Texto a buscar en el nombre del producto
     * @return Lista de productos que coinciden
     */
    List<Producto> buscarPorNombreContiene(String nombre);
    
    /**
     * Elimina un producto de la base de datos
     * 
     * NOTA: Preferir desactivar (estaActivo = false) en lugar de eliminar
     * para mantener historial
     * 
     * @param idProducto ID del producto a eliminar
     */
    void eliminar(Long idProducto);
    
    /**
     * Verifica si existe un producto con el ID dado
     * 
     * @param idProducto ID del producto
     * @return true si existe, false si no
     */
    boolean existePorId(Long idProducto);
}
