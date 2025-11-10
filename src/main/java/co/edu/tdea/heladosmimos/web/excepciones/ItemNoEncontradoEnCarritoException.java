package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando se intenta modificar/eliminar un item que no existe en el carrito.
 */
public class ItemNoEncontradoEnCarritoException extends Exception {
    public ItemNoEncontradoEnCarritoException(String mensaje) {
        super(mensaje);
    }
}
