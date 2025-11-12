package co.edu.tdea.heladosmimos.web.excepciones;

// Lanzada cuando se detecta conflicto de concurrencia al actualizar stock
public class ConflictoConcurrenciaException extends Exception {
    public ConflictoConcurrenciaException(String mensaje) {
        super(mensaje);
    }
}
