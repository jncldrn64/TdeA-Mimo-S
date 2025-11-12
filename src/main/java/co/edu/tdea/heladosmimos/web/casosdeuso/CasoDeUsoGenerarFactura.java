package co.edu.tdea.heladosmimos.web.casosdeuso;

import co.edu.tdea.heladosmimos.web.entidades.Factura;
import co.edu.tdea.heladosmimos.web.entidades.DatosFacturacion;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioFacturacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Orquesta la generación de una factura para un pedido confirmado
@Service
public class CasoDeUsoGenerarFactura {

    @Autowired
    private ServicioFacturacion servicioFacturacion;

    public Factura ejecutar(Long idPedido, DatosFacturacion datosFacturacion)
            throws ProductoNoEncontradoException, FacturaYaExisteException,
                   DatosFacturacionInvalidosException {
        return servicioFacturacion.generarFacturaParaPedido(idPedido, datosFacturacion);
    }
}
