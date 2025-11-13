package co.edu.tdea.heladosmimos.web.casosdeuso;

import co.edu.tdea.heladosmimos.web.entidades.Factura;
import co.edu.tdea.heladosmimos.web.excepciones.FacturaNoEncontradaException;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioFacturacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Orquesta la consulta de facturas
@Service
public class CasoDeUsoConsultarFactura {

    @Autowired
    private ServicioFacturacion servicioFacturacion;

    public Factura ejecutarPorId(Long idFactura) throws FacturaNoEncontradaException {
        return servicioFacturacion.obtenerFacturaPorId(idFactura);
    }

    public Factura ejecutarPorPedido(Long idPedido) throws FacturaNoEncontradaException {
        return servicioFacturacion.obtenerFacturaDePedido(idPedido);
    }

    public Factura ejecutarPorNumero(String numeroFactura) throws FacturaNoEncontradaException {
        return servicioFacturacion.obtenerFacturaPorNumero(numeroFactura);
    }
}
