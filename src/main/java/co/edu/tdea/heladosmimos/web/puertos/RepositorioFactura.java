package co.edu.tdea.heladosmimos.web.puertos;

import co.edu.tdea.heladosmimos.web.entidades.Factura;
import java.util.List;
import java.util.Optional;

/**
 * Puerto (contrato) para operaciones de persistencia de facturas.
 * Define QUÉ se puede hacer sin especificar CÓMO.
 * 
 * DECISIÓN TÉCNICA: Interface simple que Spring JPA implementará automáticamente.
 */
public interface RepositorioFactura {
    
    Factura guardar(Factura factura);
    
    Optional<Factura> buscarPorId(Long idFactura);
    
    Optional<Factura> buscarPorNumeroFactura(String numeroFactura);
    
    Optional<Factura> buscarPorIdPedido(Long idPedido);
    
    List<Factura> buscarPorNitCliente(String nitCliente);
    
    List<Factura> obtenerTodasLasFacturas();
    
    Boolean existeFacturaParaPedido(Long idPedido);
}