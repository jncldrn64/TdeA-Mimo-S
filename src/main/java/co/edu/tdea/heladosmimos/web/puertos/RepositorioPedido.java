package co.edu.tdea.heladosmimos.web.puertos;

import co.edu.tdea.heladosmimos.web.entidades.Pedido;
import java.util.Optional;

public interface RepositorioPedido {
    Optional<Pedido> buscarPorId(Long idPedido);
    Pedido guardar(Pedido pedido);
}