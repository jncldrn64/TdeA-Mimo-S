package co.edu.tdea.heladosmimos.web.excepciones;

/**
 * Excepción lanzada cuando se intenta operar con un carrito vacío.
 */
public class CarritoVacioException extends Exception {
    public CarritoVacioException(String mensaje) {
        super(mensaje);
    }
}
