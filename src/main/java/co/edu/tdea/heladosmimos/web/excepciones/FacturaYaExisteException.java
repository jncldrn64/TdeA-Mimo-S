package co.edu.tdea.heladosmimos.web.excepciones;

// Lanzada cuando se intenta generar una factura para un pedido que ya tiene una
public class FacturaYaExisteException extends Exception {
    public FacturaYaExisteException(String mensaje) {
        super(mensaje);
    }
}
