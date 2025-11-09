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
 * Implementación JPA del repositorio de items del carrito.
 * Usa @Modifying y @Transactional para operaciones de escritura.
 */
@Repository
public interface AdaptadorRepositorioItemCarrito
    extends JpaRepository<ItemCarrito, Long>, RepositorioItemCarrito {

    List<ItemCarrito> findByIdCarrito(Long idCarrito);

    Optional<ItemCarrito> findByIdCarritoAndIdProducto(Long idCarrito, Long idProducto);

    @Modifying
    @Transactional
    @Query("DELETE FROM ItemCarrito i WHERE i.idCarrito = ?1")
    void deleteByIdCarrito(Long idCarrito);

    @Override
    default Optional<ItemCarrito> buscarPorId(Long idItemCarrito) {
        return findById(idItemCarrito);
    }

    @Override
    default List<ItemCarrito> buscarPorIdCarrito(Long idCarrito) {
        return findByIdCarrito(idCarrito);
    }

    @Override
    default Optional<ItemCarrito> buscarPorIdCarritoYIdProducto(Long idCarrito, Long idProducto) {
        return findByIdCarritoAndIdProducto(idCarrito, idProducto);
    }

    @Override
    default ItemCarrito guardar(ItemCarrito itemCarrito) {
        return save(itemCarrito);
    }

    @Override
    default void eliminar(Long idItemCarrito) {
        deleteById(idItemCarrito);
    }

    @Override
    default void eliminarPorIdCarrito(Long idCarrito) {
        deleteByIdCarrito(idCarrito);
    }
}