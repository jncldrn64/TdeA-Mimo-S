package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando las credenciales de login son inválidas.
 * Por seguridad, no especifica si es el correo o la contraseña.
 */
public class CredencialesInvalidasException extends Exception {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
