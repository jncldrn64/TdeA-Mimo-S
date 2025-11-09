package co.edu.tdea.heladosmimos.web.entidades;

import co.edu.tdea.heladosmimos.web.entidades.enums.RolUsuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    private String nombre;
    private String apellido;
    @Column(unique = true)
    private String correoElectronico;
    private String contrasenaEncriptada;
    @Enumerated(EnumType.STRING)
    private RolUsuario rol;
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    @Column(name = "esta_activo")
    private Boolean estaActivo;
    private String telefono;
    private String direccion;
    private String nit;
}