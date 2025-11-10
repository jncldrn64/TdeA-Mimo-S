package co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.excepciones.*;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de gestión de inventario (RF-01).
 * Valida reglas de negocio antes de persistir productos.
 */
@Service
public class ServicioInventario {

    @Autowired
    private RepositorioProducto repositorioProducto;

    /**
     * Registra un nuevo producto en el inventario.
     * Valida: nombre único, precio válido, stock no negativo.
     */
    @Transactional
    public Producto registrarProducto(String nombre, String descripcion, Double precio,
                                     Integer stock, String urlImagen)
            throws DatosProductoInvalidosException, ProductoDuplicadoException,
                   PrecioInvalidoException, StockNegativoException {

        validarDatosObligatorios(nombre, precio, stock);
        validarPrecio(precio);
        validarStock(stock);
        validarNombreUnico(nombre, null);

        Producto producto = new Producto();
        producto.setNombreProducto(nombre);
        producto.setDescripcionDetallada(descripcion);
        producto.setPrecioUnitario(precio);
        producto.setStockDisponible(stock);
        producto.setUrlImagen(urlImagen != null ? urlImagen : "/img/default.jpg");
        producto.setFechaIngreso(LocalDateTime.now());
        producto.setEstaActivo(true);

        return repositorioProducto.guardar(producto);
    }

    /**
     * Actualiza información de un producto existente.
     */
    @Transactional
    public Producto actualizarProducto(Long idProducto, String nombre, String descripcion,
                                      Double precio, String urlImagen)
            throws ProductoNoEncontradoException, DatosProductoInvalidosException,
                   ProductoDuplicadoException, PrecioInvalidoException {

        Producto producto = repositorioProducto.buscarPorId(idProducto)
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "Producto no encontrado: " + idProducto));

        validarDatosObligatorios(nombre, precio, null);
        validarPrecio(precio);
        validarNombreUnico(nombre, idProducto);

        producto.setNombreProducto(nombre);
        producto.setDescripcionDetallada(descripcion);
        producto.setPrecioUnitario(precio);

        if (urlImagen != null && !urlImagen.isBlank()) {
            producto.setUrlImagen(urlImagen);
        }

        return repositorioProducto.guardar(producto);
    }

    /**
     * Actualiza el stock de un producto (restock).
     */
    @Transactional
    public Producto actualizarStock(Long idProducto, Integer nuevoStock)
            throws ProductoNoEncontradoException, StockNegativoException {

        validarStock(nuevoStock);

        Producto producto = repositorioProducto.buscarPorId(idProducto)
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "Producto no encontrado: " + idProducto));

        producto.setStockDisponible(nuevoStock);
        producto.setFechaUltimoRestock(LocalDateTime.now());

        return repositorioProducto.guardar(producto);
    }

    /**
     * Desactiva un producto (no lo elimina de BD).
     */
    @Transactional
    public Producto desactivarProducto(Long idProducto) throws ProductoNoEncontradoException {
        Producto producto = repositorioProducto.buscarPorId(idProducto)
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "Producto no encontrado: " + idProducto));

        producto.setEstaActivo(false);
        return repositorioProducto.guardar(producto);
    }

    /**
     * Activa un producto previamente desactivado.
     */
    @Transactional
    public Producto activarProducto(Long idProducto) throws ProductoNoEncontradoException {
        Producto producto = repositorioProducto.buscarPorId(idProducto)
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "Producto no encontrado: " + idProducto));

        producto.setEstaActivo(true);
        return repositorioProducto.guardar(producto);
    }

    public List<Producto> listarTodos() {
        return repositorioProducto.buscarTodos();
    }

    public List<Producto> listarActivos() {
        return repositorioProducto.buscarProductosActivos();
    }

    public Producto buscarPorId(Long idProducto) throws ProductoNoEncontradoException {
        return repositorioProducto.buscarPorId(idProducto)
            .orElseThrow(() -> new ProductoNoEncontradoException(
                "Producto no encontrado: " + idProducto));
    }

    // ==================== VALIDACIONES PRIVADAS ====================

    private void validarDatosObligatorios(String nombre, Double precio, Integer stock)
            throws DatosProductoInvalidosException {

        if (nombre == null || nombre.isBlank()) {
            throw new DatosProductoInvalidosException("El nombre del producto es obligatorio");
        }

        if (precio == null) {
            throw new DatosProductoInvalidosException("El precio es obligatorio");
        }

        if (stock != null && stock < 0) {
            throw new DatosProductoInvalidosException("El stock no puede ser negativo");
        }
    }

    private void validarPrecio(Double precio) throws PrecioInvalidoException {
        if (precio <= 0) {
            throw new PrecioInvalidoException("El precio debe ser mayor a 0");
        }
    }

    private void validarStock(Integer stock) throws StockNegativoException {
        if (stock < 0) {
            throw new StockNegativoException("El stock no puede ser negativo");
        }
    }

    private void validarNombreUnico(String nombre, Long idProductoExcluir)
            throws ProductoDuplicadoException {

        List<Producto> productosConNombre = repositorioProducto
            .buscarPorNombreContiene(nombre);

        for (Producto p : productosConNombre) {
            if (p.getNombreProducto().equalsIgnoreCase(nombre)) {
                if (idProductoExcluir == null || !p.getIdProducto().equals(idProductoExcluir)) {
                    throw new ProductoDuplicadoException(
                        "Ya existe un producto con el nombre: " + nombre);
                }
            }
        }
    }
}
