package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando un producto existe pero no está activo para venta.
 */
public class ProductoNoDisponibleException extends Exception {
    public ProductoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
