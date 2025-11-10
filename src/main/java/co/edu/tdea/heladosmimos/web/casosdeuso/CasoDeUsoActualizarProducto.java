package co.edu.tdea.heladosmimos.web.casosdeuso;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioInventario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Actualizar información de un producto existente.
 */
@Service
public class CasoDeUsoActualizarProducto {

    @Autowired
    private ServicioInventario servicioInventario;

    public Producto ejecutar(Long idProducto, String nombre, String descripcion,
                            Double precio, String urlImagen)
            throws ProductoNoEncontradoException, DatosProductoInvalidosException,
                   ProductoDuplicadoException, PrecioInvalidoException {

        return servicioInventario.actualizarProducto(idProducto, nombre, descripcion,
                                                    precio, urlImagen);
    }
}
