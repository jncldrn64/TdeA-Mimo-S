package co.edu.tdea.heladosmimos.web.entidades;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) para capturar datos del formulario de facturación.
 * Corresponde a los campos de la imagen del formulario.
 * 
 * PROPÓSITO: Transportar datos del formulario HTML al servicio sin exponer
 * entidades directamente al controller.
 */
@Getter
@Setter
public class DatosFacturacion {
    
    private String nit;
    private String razonSocial; // Persona física o moral
    private String nombreCompleto;
    private String direccionCalle;
    private String ciudad;
    private String codigoPostal;
    private String estado;
    private String telefono;
    private String correoElectronico;
}