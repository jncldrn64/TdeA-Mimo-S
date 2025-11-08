package co.edu.tdea.heladosmimos.web.entidades;

import co.edu.tdea.heladosmimos.web.entidades.enums.EstadoPedido;
import co.edu.tdea.heladosmimos.web.entidades.enums.MetodoPago;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;
    @Column(name = "id_usuario")
    private Long idUsuario;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pedido")
    private EstadoPedido estadoPedido;
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago")
    private MetodoPago metodoPago;
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    @Column(name = "fecha_confirmacion_pago")
    private LocalDateTime fechaConfirmacionPago;
    private Double subtotal;
    private Double iva;
    @Column(name = "costo_envio")
    private Double costoEnvio;
    private Double total;
    @Column(name = "direccion_envio")
    private String direccionEnvio;
    @Column(name = "telefono_contacto")
    private String telefonoContacto;
}