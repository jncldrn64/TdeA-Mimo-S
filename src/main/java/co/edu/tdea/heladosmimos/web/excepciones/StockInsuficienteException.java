package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando se solicita una cantidad mayor al stock disponible.
 */
public class StockInsuficienteException extends Exception {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
