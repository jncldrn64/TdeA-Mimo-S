package co.edu.tdea.heladosmimos.web.excepciones;

// Lanzada cuando se intenta usar un método de pago no implementado
public class MetodoPagoNoSoportadoException extends Exception {
    public MetodoPagoNoSoportadoException(String mensaje) {
        super(mensaje);
    }
}
