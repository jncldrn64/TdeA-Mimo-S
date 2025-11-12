package co.edu.tdea.heladosmimos.web.casosdeuso;

import co.edu.tdea.heladosmimos.web.excepciones.ProductoNoEncontradoException;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioPagos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

// Orquesta la consulta de estado de pago
@Service
public class CasoDeUsoConsultarEstadoPago {

    @Autowired
    private ServicioPagos servicioPagos;

    public Map<String, Object> ejecutar(Long idPedido)
            throws ProductoNoEncontradoException {

        return servicioPagos.consultarEstadoPago(idPedido);
    }
}
