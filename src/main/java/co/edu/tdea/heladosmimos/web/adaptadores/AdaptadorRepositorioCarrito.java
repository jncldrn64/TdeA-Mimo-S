package co.edu.tdea.heladosmimos.web.adaptadores;

import co.edu.tdea.heladosmimos.web.entidades.Carrito;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementación JPA del repositorio de carritos.
 * Spring Data JPA genera queries automáticamente.
 */
@Repository
public interface AdaptadorRepositorioCarrito
    extends JpaRepository<Carrito, Long>, RepositorioCarrito {

    Optional<Carrito> findByIdUsuario(Long idUsuario);

    boolean existsByIdUsuario(Long idUsuario);

    @Override
    default Optional<Carrito> buscarPorId(Long idCarrito) {
        return findById(idCarrito);
    }

    @Override
    default Optional<Carrito> buscarPorIdUsuario(Long idUsuario) {
        return findByIdUsuario(idUsuario);
    }

    @Override
    default Carrito guardar(Carrito carrito) {
        return save(carrito);
    }

    @Override
    default void eliminar(Long idCarrito) {
        deleteById(idCarrito);
    }

    @Override
    default boolean existePorIdUsuario(Long idUsuario) {
        return existsByIdUsuario(idUsuario);
    }
}
