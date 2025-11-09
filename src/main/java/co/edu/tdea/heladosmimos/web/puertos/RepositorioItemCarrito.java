package co.edu.tdea.heladosmimos.web.puertos;

import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import java.util.List;
import java.util.Optional;

/**
 * Contrato para persistencia de items del carrito.
 * Maneja operaciones CRUD de productos agregados al carrito.
 * Implementado por: AdaptadorRepositorioItemCarrito
 */
public interface RepositorioItemCarrito {

    Optional<ItemCarrito> buscarPorId(Long idItemCarrito);

    List<ItemCarrito> buscarPorIdCarrito(Long idCarrito);

    Optional<ItemCarrito> buscarPorIdCarritoYIdProducto(Long idCarrito, Long idProducto);

    ItemCarrito guardar(ItemCarrito itemCarrito);

    void eliminar(Long idItemCarrito);

    void eliminarPorIdCarrito(Long idCarrito);
}
