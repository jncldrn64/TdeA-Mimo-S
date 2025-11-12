package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando se intenta acceder a un producto que no existe.
 */
public class ProductoNoEncontradoException extends Exception {
    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
