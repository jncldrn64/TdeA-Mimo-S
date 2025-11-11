package co.edu.tdea.heladosmimos.web.casosdeuso;

import co.edu.tdea.heladosmimos.web.entidades.DatosPago;
import co.edu.tdea.heladosmimos.web.entidades.enums.MetodoPago;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioPagos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

// Orquesta el procesamiento de pagos para un pedido
@Service
public class CasoDeUsoProcesarPago {

    @Autowired
    private ServicioPagos servicioPagos;

    public Map<String, Object> ejecutar(Long idPedido, MetodoPago metodoPago, DatosPago datosPago)
            throws ProductoNoEncontradoException, PedidoYaPagadoException,
                   DatosTarjetaInvalidosException, PagoRechazadoException,
                   MetodoPagoNoSoportadoException, StockInsuficienteException {

        return servicioPagos.procesarPago(idPedido, metodoPago, datosPago);
    }
}
