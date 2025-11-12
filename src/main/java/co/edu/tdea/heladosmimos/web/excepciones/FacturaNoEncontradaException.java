package co.edu.tdea.heladosmimos.web.excepciones;

// Lanzada cuando no se encuentra una factura solicitada
public class FacturaNoEncontradaException extends Exception {
    public FacturaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
