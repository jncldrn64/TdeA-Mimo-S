package co.edu.tdea.heladosmimos.web.adaptadores;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA del repositorio de productos.
 * Mapea métodos del puerto a operaciones JPA automáticas.
 */
@Repository
public interface AdaptadorRepositorioProducto
    extends JpaRepository<Producto, Long>, RepositorioProducto {

    // ==================== MÉTODOS JPA ====================

    List<Producto> findByEstaActivo(Boolean estaActivo);

    List<Producto> findByNombreProductoContainingIgnoreCase(String nombre);

    // ==================== IMPLEMENTACIÓN DEL PUERTO ====================

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
        return findByEstaActivo(true);
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