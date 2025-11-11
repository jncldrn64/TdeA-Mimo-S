package co.edu.tdea.heladosmimos.web.entidades;

import lombok.Getter;
import lombok.Setter;

// DTO para capturar datos de pago desde formulario
@Getter
@Setter
public class DatosPago {

    // Datos de tarjeta (débito/crédito)
    private String numeroTarjeta;      // 16 dígitos
    private String fechaExpiracion;    // MM/AA
    private String codigoCVV;          // 3 dígitos
    private String nombreTitular;
    private String tipoDocumento;      // CC, CE, NIT
    private String numeroDocumento;

    // Datos para contra entrega
    private String direccionEntrega;
    private String telefonoContacto;
    private String instruccionesEspeciales;
}
