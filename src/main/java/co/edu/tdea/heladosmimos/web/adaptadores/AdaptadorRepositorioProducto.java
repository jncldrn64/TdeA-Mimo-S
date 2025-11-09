package co.edu.tdea.heladosmimos.web.adaptadores;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de repositorio para Producto usando Spring Data JPA.
 * Implementa tanto JpaRepository como el puerto RepositorioProducto.
 */
@Repository
public interface AdaptadorRepositorioProducto
    extends JpaRepository<Producto, Long>, RepositorioProducto {

    List<Producto> findByEstaActivoTrue();
    List<Producto> findByNombreProductoContainingIgnoreCase(String nombre);

    @Override
    default Optional<Producto> buscarPorId(Long idProducto) {
        return findById(idProducto);
    }

    @Override
    default Producto guardar(Producto producto) {
        return save(producto);
    }

    @Override
    default List<Producto> buscarTodos() {
        return findAll();
    }

    @Override
    default List<Producto> buscarProductosActivos() {
        return findByEstaActivoTrue();
    }

    @Override
    default List<Producto> buscarPorNombreContiene(String nombre) {
        return findByNombreProductoContainingIgnoreCase(nombre);
    }

    @Override
    default void eliminar(Long idProducto) {
        deleteById(idProducto);
    }

    @Override
    default boolean existePorId(Long idProducto) {
        return existsById(idProducto);
    }
}
