package co.edu.tdea.heladosmimos.web.puertos;

import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import java.util.List;
import java.util.Optional;

/**
 * PROPÓSITO: Contrato para acceso a datos de items del carrito
 *
 * DECISIÓN TÉCNICA:
 * - Maneja la persistencia de items individuales del carrito
 * - Permite operaciones CRUD sobre los items
 * - Desacopla la lógica de negocio de la implementación de persistencia
 *
 * DEPENDENCIAS:
 * - Entidad ItemCarrito
 *
 * CONEXIÓN CON OTROS MÓDULOS:
 * - AdaptadorRepositorioItemCarrito: Implementación JPA
 * - ServicioCarritoCompras: Usa este contrato para persistir items
 *
 * GENERADO POR: Claude - 2024-11-08
 */
public interface RepositorioItemCarrito {
    
    /**
     * Busca un item por su ID
     * 
     * @param idItemCarrito ID del item a buscar
     * @return Optional con el item si existe, vacío si no
     */
    Optional<ItemCarrito> buscarPorId(Long idItemCarrito);
    
    /**
     * Obtiene todos los items de un carrito específico
     * 
     * @param idCarrito ID del carrito
     * @return Lista de items del carrito (puede estar vacía)
     */
    List<ItemCarrito> buscarPorIdCarrito(Long idCarrito);
    
    /**
     * Busca un item específico por carrito y producto
     * 
     * UTILIDAD: Verificar si un producto ya está en el carrito
     * antes de agregarlo nuevamente
     * 
     * @param idCarrito ID del carrito
     * @param idProducto ID del producto
     * @return Optional con el item si existe, vacío si no
     */
    Optional<ItemCarrito> buscarPorIdCarritoYIdProducto(Long idCarrito, Long idProducto);
    
    /**
     * Guarda o actualiza un item en la base de datos
     * 
     * @param itemCarrito Item a guardar
     * @return Item guardado con ID generado
     */
    ItemCarrito guardar(ItemCarrito itemCarrito);
    
    /**
     * Elimina un item del carrito
     * 
     * @param idItemCarrito ID del item a eliminar
     */
    void eliminar(Long idItemCarrito);
    
    /**
     * Elimina todos los items de un carrito
     * 
     * UTILIDAD: Al vaciar el carrito o al confirmar pedido
     * 
     * @param idCarrito ID del carrito
     */
    void eliminarPorIdCarrito(Long idCarrito);
}
