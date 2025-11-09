package co.edu.tdea.heladosmimos.web.adaptadores;

import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * PROPÓSITO: Implementación JPA del repositorio de items del carrito
 *
 * DECISIÓN TÉCNICA:
 * - Operaciones CRUD automáticas vía JpaRepository
 * - Queries personalizadas para operaciones específicas
 * - @Modifying para operaciones de escritura (DELETE)
 * - @Transactional para garantizar atomicidad
 *
 * DEPENDENCIAS:
 * - Spring Data JPA
 * - Entidad ItemCarrito
 * - Puerto RepositorioItemCarrito
 *
 * CONEXIÓN CON OTROS MÓDULOS:
 * - ServicioCarritoCompras: Principal consumidor
 * - Base de datos SQL Server: Tabla items_carrito
 *
 * GENERADO POR: Claude - 2024-11-08
 */
@Repository
public interface AdaptadorRepositorioItemCarrito 
    extends JpaRepository<ItemCarrito, Long>, RepositorioItemCarrito {
    
    /**
     * Busca todos los items de un carrito específico
     * 
     * QUERY GENERADA:
     * SELECT * FROM items_carrito WHERE id_carrito = ?
     * 
     * @param idCarrito ID del carrito
     * @return Lista de items (vacía si no hay items)
     */
    List<ItemCarrito> findByIdCarrito(Long idCarrito);
    
    /**
     * Busca un item específico por carrito y producto
     * 
     * QUERY GENERADA:
     * SELECT * FROM items_carrito 
     * WHERE id_carrito = ? AND id_producto = ?
     * 
     * UTILIDAD: Verificar si un producto ya está en el carrito
     * antes de agregarlo (para incrementar cantidad en lugar de duplicar)
     * 
     * @param idCarrito ID del carrito
     * @param idProducto ID del producto
     * @return Optional con el item si existe
     */
    Optional<ItemCarrito> findByIdCarritoAndIdProducto(Long idCarrito, Long idProducto);
    
    /**
     * Elimina todos los items de un carrito
     * 
     * DECISIÓN TÉCNICA:
     * - @Modifying: Indica que es una operación de modificación
     * - @Transactional: Garantiza que se ejecute en una transacción
     * - @Query: Query personalizada en JPQL
     * 
     * QUERY EJECUTADA:
     * DELETE FROM ItemCarrito i WHERE i.idCarrito = ?
     * 
     * UTILIDAD: Al vaciar carrito o confirmar pedido
     * 
     * @param idCarrito ID del carrito
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ItemCarrito i WHERE i.idCarrito = ?1")
    void deleteByIdCarrito(Long idCarrito);
    
    // ==================== IMPLEMENTACIÓN DEL PUERTO ====================
    
    /**
     * Implementa buscarPorId del puerto
     */
    @Override
    default Optional<ItemCarrito> buscarPorId(Long idItemCarrito) {
        return findById(idItemCarrito);
    }
    
    /**
     * Implementa buscarPorIdCarrito del puerto
     */
    @Override
    default List<ItemCarrito> buscarPorIdCarrito(Long idCarrito) {
        return findByIdCarrito(idCarrito);
    }
    
    /**
     * Implementa buscarPorIdCarritoYIdProducto del puerto
     */
    @Override
    default Optional<ItemCarrito> buscarPorIdCarritoYIdProducto(Long idCarrito, Long idProducto) {
        return findByIdCarritoAndIdProducto(idCarrito, idProducto);
    }
    
    /**
     * Implementa guardar del puerto
     */
    @Override
    default ItemCarrito guardar(ItemCarrito itemCarrito) {
        return save(itemCarrito);
    }
    
    /**
     * Implementa eliminar del puerto
     */
    @Override
    default void eliminar(Long idItemCarrito) {
        deleteById(idItemCarrito);
    }
    
    /**
     * Implementa eliminarPorIdCarrito del puerto
     */
    @Override
    default void eliminarPorIdCarrito(Long idCarrito) {
        deleteByIdCarrito(idCarrito);
    }
}