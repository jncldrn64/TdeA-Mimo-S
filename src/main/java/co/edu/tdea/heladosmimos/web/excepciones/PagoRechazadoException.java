package co.edu.tdea.heladosmimos.web.excepciones;

// Lanzada cuando un pago es rechazado por el sistema
public class PagoRechazadoException extends Exception {
    public PagoRechazadoException(String mensaje) {
        super(mensaje);
    }
}
