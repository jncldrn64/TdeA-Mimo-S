package co.edu.tdea.heladosmimos.web.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "items_carrito")
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_carrito")
    private Long idItemCarrito;
    @Column(name = "id_carrito")
    private Long idCarrito;
    @Column(name = "id_producto")
    private Long idProducto;
    private Integer cantidad;
    @Column(name = "precio_unitario_al_agregar")
    private Double precioUnitarioAlAgregar;
}