package co.edu.tdea.heladosmimos.web.excepciones;

// Lanzada cuando los datos de tarjeta no cumplen formato esperado
public class DatosTarjetaInvalidosException extends Exception {
    public DatosTarjetaInvalidosException(String mensaje) {
        super(mensaje);
    }
}
