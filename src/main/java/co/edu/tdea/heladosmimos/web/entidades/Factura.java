package co.edu.tdea.heladosmimos.web.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "facturas")
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Long idFactura;
    @Column(name = "id_pedido")
    private Long idPedido;
    @Column(name = "numero_factura", unique = true)
    private String numeroFactura;
    @Column(name = "nit_cliente")
    private String nitCliente;
    @Column(name = "razon_social")
    private String razonSocial;
    @Column(name = "direccion_fiscal")
    private String direccionFiscal;
    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;
    private Double subtotal;
    private Double iva;
    private Double total;
    @Column(name = "ruta_archivo_pdf")
    private String rutaArchivoPdf;
}