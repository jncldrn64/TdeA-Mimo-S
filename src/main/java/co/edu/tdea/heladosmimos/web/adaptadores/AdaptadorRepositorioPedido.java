package co.edu.tdea.heladosmimos.web.adaptadores;

import co.edu.tdea.heladosmimos.web.entidades.Pedido;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Implementación JPA del repositorio de pedidos
@Repository
public interface AdaptadorRepositorioPedido
    extends JpaRepository<Pedido, Long>, RepositorioPedido {

    @Override
    default Optional<Pedido> buscarPorId(Long idPedido) {
        return findById(idPedido);
    }

    @Override
    default Pedido guardar(Pedido pedido) {
        return save(pedido);
    }

    @Override
    @Query("SELECT p FROM Pedido p WHERE p.idUsuario = :idUsuario ORDER BY p.fechaCreacion DESC")
    List<Pedido> buscarPorUsuario(@Param("idUsuario") Long idUsuario);
}
