package co.edu.tdea.heladosmimos.web.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;
    @Column(name = "nombre_producto")
    private String nombreProducto;
    @Column(name = "descripcion_detallada", length = 1000)
    private String descripcionDetallada;
    @Column(name = "precio_unitario")
    private Double precioUnitario;
    @Column(name = "stock_disponible")
    private Integer stockDisponible;
    @Column(name = "url_imagen")
    private String urlImagen;
    @Column(name = "fecha_ingreso")
    private LocalDateTime fechaIngreso;
    @Column(name = "fecha_ultimo_restock")
    private LocalDateTime fechaUltimoRestock;
    @Column(name = "esta_activo")
    private Boolean estaActivo;
}
