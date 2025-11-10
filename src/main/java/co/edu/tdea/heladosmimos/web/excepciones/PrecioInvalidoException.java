package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando el precio es inválido (negativo o cero).
 */
public class PrecioInvalidoException extends Exception {
    public PrecioInvalidoException(String mensaje) {
        super(mensaje);
    }
}
