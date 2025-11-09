package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando se intenta registrar un correo que ya existe.
 */
public class CorreoYaRegistradoException extends Exception {
    public CorreoYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}
