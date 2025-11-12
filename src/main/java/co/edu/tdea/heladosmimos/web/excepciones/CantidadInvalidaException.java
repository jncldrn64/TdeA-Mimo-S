package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando se proporciona una cantidad inválida (menor o igual a cero).
 */
public class CantidadInvalidaException extends Exception {
    public CantidadInvalidaException(String mensaje) {
        super(mensaje);
    }
}
