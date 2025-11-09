package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando faltan datos obligatorios en el registro.
 */
public class DatosRegistroIncompletosException extends Exception {
    public DatosRegistroIncompletosException(String mensaje) {
        super(mensaje);
    }
}
