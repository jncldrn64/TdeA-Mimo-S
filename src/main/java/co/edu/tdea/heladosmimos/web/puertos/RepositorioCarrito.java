package co.edu.tdea.heladosmimos.web.puertos;

import co.edu.tdea.heladosmimos.web.entidades.Carrito;
import java.util.Optional;

/**
 * Contrato para persistencia de carritos de compras.
 * Desacopla lógica de negocio de la implementación de BD.
 * Implementado por: AdaptadorRepositorioCarrito
 */
public interface RepositorioCarrito {

    Optional<Carrito> buscarPorId(Long idCarrito);

    Optional<Carrito> buscarPorIdUsuario(Long idUsuario);

    Carrito guardar(Carrito carrito);

    void eliminar(Long idCarrito);

    boolean existePorIdUsuario(Long idUsuario);
}