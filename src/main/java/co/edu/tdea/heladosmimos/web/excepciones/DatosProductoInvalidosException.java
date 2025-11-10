package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando los datos del producto son inválidos o incompletos.
 */
public class DatosProductoInvalidosException extends Exception {
    public DatosProductoInvalidosException(String mensaje) {
        super(mensaje);
    }
}
