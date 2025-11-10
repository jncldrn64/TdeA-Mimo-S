package co.edu.tdea.heladosmimos.web.excepciones;

// Lanzada cuando los datos del formulario de facturación son inválidos o incompletos
public class DatosFacturacionInvalidosException extends Exception {
    public DatosFacturacionInvalidosException(String mensaje) {
        super(mensaje);
    }
}
