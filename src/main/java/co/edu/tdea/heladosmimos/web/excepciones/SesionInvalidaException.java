package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando se intenta acceder a un recurso sin sesión válida.
 */
public class SesionInvalidaException extends Exception {
    public SesionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
