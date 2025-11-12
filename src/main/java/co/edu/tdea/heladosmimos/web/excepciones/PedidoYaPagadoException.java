package co.edu.tdea.heladosmimos.web.excepciones;

// Lanzada cuando se intenta pagar un pedido que ya está pagado
public class PedidoYaPagadoException extends Exception {
    public PedidoYaPagadoException(String mensaje) {
        super(mensaje);
    }
}
