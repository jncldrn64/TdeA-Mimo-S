package co.edu.tdea.heladosmimos.web.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "carritos")
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long idCarrito;
    @Column(name = "id_usuario")
    private Long idUsuario;
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}