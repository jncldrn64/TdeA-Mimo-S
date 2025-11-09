package co.edu.tdea.heladosmimos.web.puertos;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import java.util.List;
import java.util.Optional;

/**
 * Contrato para persistencia de productos.
 * Usado por ServicioCarritoCompras y ServicioCatalogo.
 * Implementado por: AdaptadorRepositorioProducto
 */
public interface RepositorioProducto {

    Optional<Producto> buscarPorId(Long idProducto);

    Producto guardar(Producto producto);

    List<Producto> buscarTodos();

    List<Producto> buscarProductosActivos();

    List<Producto> buscarPorNombreContiene(String nombre);

    void eliminar(Long idProducto);

    boolean existePorId(Long idProducto);
}
