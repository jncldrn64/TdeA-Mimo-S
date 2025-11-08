package co.edu.tdea.heladosmimos.web.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "items_pedido")
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_pedido")
    private Long idItemPedido;
    @Column(name = "id_pedido")
    private Long idPedido;
    @Column(name = "id_producto")
    private Long idProducto;
    @Column(name = "nombre_producto_al_comprar")
    private String nombreProductoAlComprar;
    private Integer cantidad;
    @Column(name = "precio_unitario_al_comprar")
    private Double precioUnitarioAlComprar;
}