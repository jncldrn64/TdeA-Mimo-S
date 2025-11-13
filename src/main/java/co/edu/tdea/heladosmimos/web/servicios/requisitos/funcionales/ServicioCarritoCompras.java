package co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales;

import co.edu.tdea.heladosmimos.web.entidades.Carrito;
import co.edu.tdea.heladosmimos.web.entidades.ItemCarrito;
import co.edu.tdea.heladosmimos.web.entidades.Pedido;
import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.entidades.enums.EstadoPedido;
import co.edu.tdea.heladosmimos.web.entidades.enums.MetodoPago;
import co.edu.tdea.heladosmimos.web.excepciones.CantidadInvalidaException;
import co.edu.tdea.heladosmimos.web.excepciones.CarritoVacioException;
import co.edu.tdea.heladosmimos.web.excepciones.ConflictoConcurrenciaException;
import co.edu.tdea.heladosmimos.web.excepciones.ProductoNoDisponibleException;
import co.edu.tdea.heladosmimos.web.excepciones.ProductoNoEncontradoException;
import co.edu.tdea.heladosmimos.web.excepciones.StockInsuficienteException;
import jakarta.persistence.OptimisticLockException;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioCarrito;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioItemCarrito;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioPedido;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de gestión del carrito de compras.
 * ENFOQUE HÍBRIDO: @SessionScope para operaciones rápidas en memoria,
 * con persistencia en BD cuando se confirma pedido.
 * LIMITACIÓN: No escala horizontalmente sin Redis/sesiones distribuidas.
 */
@Service
@SessionScope
public class ServicioCarritoCompras {

    private final List<ItemCarrito> itemsDelCarrito = new ArrayList<>();
    private Double totalCalculado = 0.0;
    private Long idCarritoPersistido = null;

    @Autowired
    private RepositorioProducto repositorioProducto;

    @Autowired
    private RepositorioCarrito repositorioCarrito;

    @Autowired
    private RepositorioItemCarrito repositorioItemCarrito;

    @Autowired
    private RepositorioPedido repositorioPedido;

    public void agregarProductoAlCarrito(Long idProducto, Integer cantidad)
            throws ProductoNoEncontradoException, ProductoNoDisponibleException, StockInsuficienteException {

        Producto producto = repositorioProducto.buscarPorId(idProducto)
            .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado: " + idProducto));

        if (!producto.getEstaActivo()) {
            throw new ProductoNoDisponibleException("Producto no disponible");
        }

        if (producto.getStockDisponible() < cantidad) {
            throw new StockInsuficienteException("Stock insuficiente. Disponible: " +
                producto.getStockDisponible());
        }

        ItemCarrito itemExistente = buscarItemPorProducto(idProducto);

        if (itemExistente != null) {
            Integer nuevaCantidad = itemExistente.getCantidad() + cantidad;

            if (producto.getStockDisponible() < nuevaCantidad) {
                throw new StockInsuficienteException("Stock insuficiente para cantidad total: " + nuevaCantidad);
            }

            itemExistente.setCantidad(nuevaCantidad);
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setIdProducto(idProducto);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitarioAlAgregar(producto.getPrecioUnitario());

            if (idCarritoPersistido != null) {
                nuevoItem.setIdCarrito(idCarritoPersistido);
            }

            itemsDelCarrito.add(nuevoItem);
        }

        recalcularTotal();
    }

    public void modificarCantidadDeItem(Long idProducto, Integer nuevaCantidad)
            throws CantidadInvalidaException, ProductoNoEncontradoException, StockInsuficienteException {

        if (nuevaCantidad < 1) {
            throw new CantidadInvalidaException("Cantidad debe ser mayor a 0");
        }

        Producto producto = repositorioProducto.buscarPorId(idProducto)
            .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado"));

        if (producto.getStockDisponible() < nuevaCantidad) {
            throw new StockInsuficienteException("Stock insuficiente para cantidad: " + nuevaCantidad);
        }

        ItemCarrito item = buscarItemPorProducto(idProducto);
        if (item != null) {
            item.setCantidad(nuevaCantidad);
            recalcularTotal();
        } else {
            throw new ProductoNoEncontradoException("Producto no encontrado en el carrito");
        }
    }

    public void eliminarProductoDelCarrito(Long idProducto) {
        itemsDelCarrito.removeIf(item -> item.getIdProducto().equals(idProducto));
        recalcularTotal();
    }

    public void vaciarCarritoCompleto() {
        itemsDelCarrito.clear();
        totalCalculado = 0.0;
    }

    public List<ItemCarrito> obtenerItemsDelCarrito() {
        return new ArrayList<>(itemsDelCarrito);
    }

    public Double obtenerTotalDelCarrito() {
        return totalCalculado;
    }

    public Integer contarTotalDeProductos() {
        return itemsDelCarrito.stream()
            .mapToInt(ItemCarrito::getCantidad)
            .sum();
    }

    @Transactional
    public Long persistirCarritoEnBaseDeDatos(Long idUsuario) {
        Carrito carrito = repositorioCarrito.buscarPorIdUsuario(idUsuario)
            .orElse(new Carrito());

        carrito.setIdUsuario(idUsuario);
        carrito.setFechaCreacion(carrito.getFechaCreacion() != null
            ? carrito.getFechaCreacion()
            : LocalDateTime.now());

        carrito = repositorioCarrito.guardar(carrito);
        idCarritoPersistido = carrito.getIdCarrito();

        repositorioItemCarrito.eliminarPorIdCarrito(idCarritoPersistido);

        for (ItemCarrito item : itemsDelCarrito) {
            item.setIdCarrito(idCarritoPersistido);
            repositorioItemCarrito.guardar(item);
        }

        return idCarritoPersistido;
    }

    public boolean cargarCarritoDesdeBaseDeDatos(Long idUsuario) {
        Carrito carrito = repositorioCarrito.buscarPorIdUsuario(idUsuario)
            .orElse(null);

        if (carrito == null) {
            return false;
        }

        itemsDelCarrito.clear();

        List<ItemCarrito> itemsGuardados = repositorioItemCarrito
            .buscarPorIdCarrito(carrito.getIdCarrito());

        itemsDelCarrito.addAll(itemsGuardados);
        idCarritoPersistido = carrito.getIdCarrito();

        recalcularTotal();

        return true;
    }

    @Transactional
    public void vaciarCarritoYPersistir(Long idUsuario) {
        vaciarCarritoCompleto();

        Carrito carrito = repositorioCarrito.buscarPorIdUsuario(idUsuario)
            .orElse(null);

        if (carrito != null) {
            repositorioItemCarrito.eliminarPorIdCarrito(carrito.getIdCarrito());
        }

        idCarritoPersistido = null;
    }

    @Transactional
    public Pedido procesarCheckout(Usuario usuario)
            throws CarritoVacioException, StockInsuficienteException,
                   ProductoNoEncontradoException, ProductoNoDisponibleException,
                   ConflictoConcurrenciaException {

        if (itemsDelCarrito.isEmpty()) {
            throw new CarritoVacioException("El carrito está vacío");
        }

        try {
            // Validar disponibilidad y reducir stock ATÓMICAMENTE
            // TODO ARQUITECTURAL: Stock debería reducirse en ServicioPagos DESPUÉS de confirmar pago,
            // no en checkout. Requiere implementar tabla pedido_items para persistir items del carrito
            // en el pedido. Por ahora, reducimos stock aquí como solución temporal.
            for (ItemCarrito item : itemsDelCarrito) {
                Producto producto = repositorioProducto.buscarPorId(item.getIdProducto())
                    .orElseThrow(() -> new ProductoNoEncontradoException(
                        "Producto no encontrado: " + item.getIdProducto()));

                if (!producto.getEstaActivo()) {
                    throw new ProductoNoDisponibleException(
                        "Producto no disponible: " + producto.getNombreProducto());
                }

                if (producto.getStockDisponible() < item.getCantidad()) {
                    throw new StockInsuficienteException(
                        "Stock insuficiente para " + producto.getNombreProducto() +
                        ". Disponible: " + producto.getStockDisponible() +
                        ", solicitado: " + item.getCantidad());
                }

                // Reducir stock (si otro usuario modificó, OptimisticLockException)
                producto.setStockDisponible(producto.getStockDisponible() - item.getCantidad());
                repositorioProducto.guardar(producto);
            }

            // Crear pedido con los datos del carrito
            // NOTA: El pedido se crea en estado PAGO_PENDIENTE
            // ServicioPagos lo actualizará a PAGO_CONFIRMADO después de validar pago
            Pedido pedido = new Pedido();
            pedido.setIdUsuario(usuario.getIdUsuario());
            pedido.setEstadoPedido(EstadoPedido.PAGO_PENDIENTE);
            pedido.setMetodoPago(null); // Se asignará en ServicioPagos según método elegido
            pedido.setFechaCreacion(LocalDateTime.now());
            // fechaConfirmacionPago se asigna en ServicioPagos después del pago exitoso

            // Calcular subtotal, IVA y total
            // totalCalculado ya es el subtotal (suma de precio * cantidad de cada item)
            Double subtotal = totalCalculado;
            Double costoEnvio = 0.0; // Sin costo de envío por ahora
            Double iva = subtotal * 0.19; // IVA 19% Colombia
            Double total = subtotal + iva + costoEnvio;

            pedido.setSubtotal(subtotal);
            pedido.setIva(iva);
            pedido.setCostoEnvio(costoEnvio);
            pedido.setTotal(total);
            pedido.setDireccionEnvio(usuario.getDireccion());
            pedido.setTelefonoContacto(usuario.getTelefono());

            // Guardar pedido en BD
            Pedido pedidoGuardado = repositorioPedido.guardar(pedido);

            // Si llegamos aquí, checkout exitoso - limpiar carrito en BD y memoria
            if (idCarritoPersistido != null) {
                repositorioItemCarrito.eliminarPorIdCarrito(idCarritoPersistido);
                idCarritoPersistido = null;
            }

            vaciarCarritoCompleto();

            return pedidoGuardado;

        } catch (OptimisticLockException e) {
            throw new ConflictoConcurrenciaException(
                "Otro usuario modificó el stock al mismo tiempo. " +
                "Por favor, revisa tu carrito e intenta nuevamente.");
        }
    }

    public List<String> validarDisponibilidadItems() {
        List<String> advertencias = new ArrayList<>();

        for (ItemCarrito item : itemsDelCarrito) {
            Producto producto = repositorioProducto.buscarPorId(item.getIdProducto())
                .orElse(null);

            if (producto == null) {
                advertencias.add("Producto con ID " + item.getIdProducto() + " ya no existe");
                continue;
            }

            if (!producto.getEstaActivo()) {
                advertencias.add(producto.getNombreProducto() + " ya no está disponible");
            }

            if (producto.getStockDisponible() < item.getCantidad()) {
                advertencias.add(producto.getNombreProducto() +
                    ": solo quedan " + producto.getStockDisponible() +
                    " unidades (tienes " + item.getCantidad() + " en carrito)");
            }
        }

        return advertencias;
    }

    private ItemCarrito buscarItemPorProducto(Long idProducto) {
        return itemsDelCarrito.stream()
            .filter(item -> item.getIdProducto().equals(idProducto))
            .findFirst()
            .orElse(null);
    }

    private void recalcularTotal() {
        totalCalculado = itemsDelCarrito.stream()
            .mapToDouble(item -> item.getPrecioUnitarioAlAgregar() * item.getCantidad())
            .sum();
    }
}
