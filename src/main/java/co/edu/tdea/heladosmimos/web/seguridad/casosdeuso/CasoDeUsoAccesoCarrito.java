package co.edu.tdea.heladosmimos.web.seguridad.casosdeuso;

import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioCarritoCompras;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso para acceso al carrito de compras.
 * Valida parámetros y autenticación antes de delegar al servicio.
 * TODO: Integrar Spring Security para validar sesión de usuario.
 */
@Service
public class CasoDeUsoAccesoCarrito {

    @Autowired
    private ServicioCarritoCompras servicioCarritoCompras;

    public List<ItemCarrito> ejecutarObtenerCarrito() {
        return servicioCarritoCompras.obtenerItemsDelCarrito();
    }

    public Double ejecutarObtenerTotal() {
        return servicioCarritoCompras.obtenerTotalDelCarrito();
    }

    public void ejecutarAgregarProducto(Long idProducto, Integer cantidad)
            throws ProductoNoEncontradoException, ProductoNoDisponibleException,
                   StockInsuficienteException, CantidadInvalidaException {

        if (idProducto == null || idProducto <= 0) {
            throw new ProductoNoEncontradoException("ID de producto inválido");
        }

        if (cantidad == null || cantidad <= 0) {
            throw new CantidadInvalidaException("Cantidad debe ser mayor a 0");
        }

        servicioCarritoCompras.agregarProductoAlCarrito(idProducto, cantidad);
    }

    public void ejecutarModificarCantidad(Long idProducto, Integer nuevaCantidad)
            throws ProductoNoEncontradoException, StockInsuficienteException, CantidadInvalidaException {

        if (idProducto == null || idProducto <= 0) {
            throw new ProductoNoEncontradoException("ID de producto inválido");
        }

        if (nuevaCantidad == null || nuevaCantidad <= 0) {
            throw new CantidadInvalidaException("Cantidad debe ser mayor a 0");
        }

        servicioCarritoCompras.modificarCantidadDeItem(idProducto, nuevaCantidad);
    }

    public void ejecutarEliminarProducto(Long idProducto) throws ProductoNoEncontradoException {
        if (idProducto == null || idProducto <= 0) {
            throw new ProductoNoEncontradoException("ID de producto inválido");
        }

        servicioCarritoCompras.eliminarProductoDelCarrito(idProducto);
    }

    public void ejecutarVaciarCarrito() {
        servicioCarritoCompras.vaciarCarritoCompleto();
    }
}
