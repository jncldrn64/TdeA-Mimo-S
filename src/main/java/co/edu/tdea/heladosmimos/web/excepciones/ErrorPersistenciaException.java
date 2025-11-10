package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando ocurre un error en la capa de persistencia.
 */
public class ErrorPersistenciaException extends RuntimeException {
    public ErrorPersistenciaException(String mensaje) {
        super(mensaje);
    }

    public ErrorPersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
