package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando se intenta crear un producto con nombre duplicado.
 */
public class ProductoDuplicadoException extends Exception {
    public ProductoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
