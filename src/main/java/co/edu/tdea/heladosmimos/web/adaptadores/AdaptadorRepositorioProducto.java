package co.edu.tdea.heladosmimos.web.adaptadores;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PROPÓSITO: Implementación JPA del repositorio de productos
 *
 * DECISIÓN TÉCNICA:
 * - Extiende JpaRepository para operaciones CRUD automáticas
 * - Implementa RepositorioProducto (puerto) para cumplir contrato
 * - Spring Data JPA genera todas las queries automáticamente
 *
 * OPERACIONES HEREDADAS AUTOMÁTICAMENTE:
 * - save() → Guardar/actualizar producto
 * - findById() → Buscar por ID
 * - findAll() → Listar todos
 * - delete() → Eliminar
 * - count() → Contar registros
 * - Y más de 20 métodos adicionales
 *
 * DEPENDENCIAS:
 * - Spring Data JPA
 * - Entidad Producto
 * - Puerto RepositorioProducto
 *
 * CONEXIÓN CON OTROS MÓDULOS:
 * - ServicioCarritoCompras: Valida existencia y stock de productos
 * - ServicioCatalogo: Lista productos disponibles
 * - Base de datos SQL Server: Tabla productos
 *
 * GENERADO POR: Claude - 2024-11-08
 */
@Repository
public interface AdaptadorRepositorioProducto 
    extends JpaRepository<Producto, Long>, RepositorioProducto {
    
    // ==================== IMPLEMENTACIÓN DEL PUERTO ====================
    
    /**
     * Implementa buscarPorId del puerto
     * 
     * DELEGACIÓN: Usa findById() heredado de JpaRepository
     * 
     * QUERY GENERADA:
     * SELECT * FROM productos WHERE id_producto = ?
     * 
     * @param idProducto ID del producto a buscar
     * @return Optional con el producto si existe, vacío si no
     */
    @Override
    default Optional<Producto> buscarPorId(Long idProducto) {
        return findById(idProducto);
    }
    
    /**
     * Implementa guardar del puerto
     * 
     * DELEGACIÓN: Usa save() heredado de JpaRepository
     * 
     * COMPORTAMIENTO:
     * - Si producto.idProducto == null → INSERT (nuevo producto)
     * - Si producto.idProducto != null → UPDATE (actualizar existente)
     * 
     * @param producto Producto a guardar
     * @return Producto guardado con ID generado (si es nuevo)
     */
    @Override
    default Producto guardar(Producto producto) {
        return save(producto);
    }
}