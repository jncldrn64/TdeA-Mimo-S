package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando se intenta establecer stock negativo.
 */
public class StockNegativoException extends Exception {
    public StockNegativoException(String mensaje) {
        super(mensaje);
    }
}
