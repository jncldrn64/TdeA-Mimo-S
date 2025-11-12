package co.edu.tdea.heladosmimos.web.adaptadores;

import co.edu.tdea.heladosmimos.web.entidades.Factura;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA para RepositorioFactura.
 * Spring Data JPA implementa automáticamente los métodos CRUD básicos.
 * 
 * DECISIÓN TÉCNICA: Extendemos JpaRepository para obtener save(), findById(), etc.
 * Los métodos personalizados se implementan por convención de nombres.
 */
@Repository
public interface AdaptadorRepositorioFactura 
    extends JpaRepository<Factura, Long>, RepositorioFactura {
    
    // Spring JPA implementa automáticamente estos métodos por nombre:
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    Optional<Factura> findByIdPedido(Long idPedido);
    List<Factura> findByNitCliente(String nitCliente);
    Boolean existsByIdPedido(Long idPedido);
    
    // Mapeo de métodos del puerto a métodos JPA:
    @Override
    default Factura guardar(Factura factura) {
        return save(factura);
    }
    
    @Override
    default Optional<Factura> buscarPorId(Long idFactura) {
        return findById(idFactura);
    }
    
    @Override
    default Optional<Factura> buscarPorNumeroFactura(String numeroFactura) {
        return findByNumeroFactura(numeroFactura);
    }
    
    @Override
    default Optional<Factura> buscarPorIdPedido(Long idPedido) {
        return findByIdPedido(idPedido);
    }
    
    @Override
    default List<Factura> buscarPorNitCliente(String nitCliente) {
        return findByNitCliente(nitCliente);
    }
    
    @Override
    default List<Factura> obtenerTodasLasFacturas() {
        return findAll();
    }
    
    @Override
    default Boolean existeFacturaParaPedido(Long idPedido) {
        return existsByIdPedido(idPedido);
    }
}