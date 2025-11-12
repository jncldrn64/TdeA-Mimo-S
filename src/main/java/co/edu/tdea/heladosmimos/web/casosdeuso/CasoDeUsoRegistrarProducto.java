package co.edu.tdea.heladosmimos.web.casosdeuso;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioInventario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Registrar nuevo producto en inventario.
 * Valida parámetros y delega lógica de negocio al servicio.
 */
@Service
public class CasoDeUsoRegistrarProducto {

    @Autowired
    private ServicioInventario servicioInventario;

    public Producto ejecutar(String nombre, String descripcion, Double precio,
                            Integer stock, String urlImagen)
            throws DatosProductoInvalidosException, ProductoDuplicadoException,
                   PrecioInvalidoException, StockNegativoException {

        return servicioInventario.registrarProducto(nombre, descripcion, precio,
                                                   stock, urlImagen);
    }
}
