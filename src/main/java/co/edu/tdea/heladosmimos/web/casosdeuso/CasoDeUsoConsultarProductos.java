package co.edu.tdea.heladosmimos.web.casosdeuso;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.excepciones.ProductoNoEncontradoException;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioInventario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso: Consultar productos del inventario.
 */
@Service
public class CasoDeUsoConsultarProductos {

    @Autowired
    private ServicioInventario servicioInventario;

    public List<Producto> ejecutarListarTodos() {
        return servicioInventario.listarTodos();
    }

    public List<Producto> ejecutarListarActivos() {
        return servicioInventario.listarActivos();
    }

    public Producto ejecutarBuscarPorId(Long idProducto) throws ProductoNoEncontradoException {
        return servicioInventario.buscarPorId(idProducto);
    }
}
