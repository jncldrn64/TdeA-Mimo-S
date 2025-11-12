package co.edu.tdea.heladosmimos.web.casosdeuso;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioInventario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Gestionar stock de productos (restock).
 */
@Service
public class CasoDeUsoGestionarStock {

    @Autowired
    private ServicioInventario servicioInventario;

    public Producto ejecutarActualizarStock(Long idProducto, Integer nuevoStock)
            throws ProductoNoEncontradoException, StockNegativoException {

        return servicioInventario.actualizarStock(idProducto, nuevoStock);
    }

    public Producto ejecutarActivar(Long idProducto) throws ProductoNoEncontradoException {
        return servicioInventario.activarProducto(idProducto);
    }

    public Producto ejecutarDesactivar(Long idProducto) throws ProductoNoEncontradoException {
        return servicioInventario.desactivarProducto(idProducto);
    }
}
