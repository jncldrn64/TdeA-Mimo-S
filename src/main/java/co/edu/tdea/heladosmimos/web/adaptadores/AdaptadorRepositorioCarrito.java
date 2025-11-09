package co.edu.tdea.heladosmimos.web.adaptadores;

import co.edu.tdea.heladosmimos.web.entidades.Carrito;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PROPÓSITO: Implementación JPA del repositorio de carritos
 *
 * DECISIÓN TÉCNICA:
 * - Extiende JpaRepository para heredar operaciones CRUD automáticas
 * - Implementa RepositorioCarrito para cumplir el contrato del puerto
 * - Spring Data JPA genera automáticamente las queries SQL
 *
 * OPERACIONES AUTOMÁTICAS DE JPA:
 * - save() → Implementa guardar()
 * - findById() → Implementa buscarPorId()
 * - deleteById() → Implementa eliminar()
 * - Y más de 20 métodos adicionales sin escribir código
 *
 * DEPENDENCIAS:
 * - Spring Data JPA
 * - Entidad Carrito
 * - Puerto RepositorioCarrito
 *
 * CONEXIÓN CON OTROS MÓDULOS:
 * - ServicioCarritoCompras: Inyecta este repositorio
 * - Base de datos SQL Server: Ejecuta queries generadas por JPA
 *
 * GENERADO POR: Claude - 2024-11-08
 */
@Repository
public interface AdaptadorRepositorioCarrito 
    extends JpaRepository<Carrito, Long>, RepositorioCarrito {
    
    /**
     * Busca un carrito por ID de usuario
     * 
     * QUERY GENERADA POR JPA:
     * SELECT * FROM carritos WHERE id_usuario = ?
     * 
     * NAMING CONVENTION:
     * - findBy → SELECT
     * - IdUsuario → Mapea al campo idUsuario de la entidad
     * 
     * @param idUsuario ID del usuario
     * @return Optional con el carrito si existe
     */
    Optional<Carrito> findByIdUsuario(Long idUsuario);
    
    /**
     * Verifica si existe un carrito para un usuario
     * 
     * QUERY GENERADA:
     * SELECT COUNT(*) > 0 FROM carritos WHERE id_usuario = ?
     * 
     * @param idUsuario ID del usuario
     * @return true si existe, false si no
     */
    boolean existsByIdUsuario(Long idUsuario);
    
    // ==================== IMPLEMENTACIÓN DEL PUERTO ====================
    
    /**
     * Implementa buscarPorId del puerto
     * 
     * DELEGACIÓN: Usa findById() heredado de JpaRepository
     */
    @Override
    default Optional<Carrito> buscarPorId(Long idCarrito) {
        return findById(idCarrito);
    }
    
    /**
     * Implementa buscarPorIdUsuario del puerto
     * 
     * DELEGACIÓN: Usa findByIdUsuario() definido arriba
     */
    @Override
    default Optional<Carrito> buscarPorIdUsuario(Long idUsuario) {
        return findByIdUsuario(idUsuario);
    }
    
    /**
     * Implementa guardar del puerto
     * 
     * DELEGACIÓN: Usa save() heredado de JpaRepository
     * - Si el carrito tiene ID → UPDATE
     * - Si no tiene ID → INSERT
     */
    @Override
    default Carrito guardar(Carrito carrito) {
        return save(carrito);
    }
    
    /**
     * Implementa eliminar del puerto
     * 
     * DELEGACIÓN: Usa deleteById() heredado de JpaRepository
     */
    @Override
    default void eliminar(Long idCarrito) {
        deleteById(idCarrito);
    }
    
    /**
     * Implementa existePorIdUsuario del puerto
     * 
     * DELEGACIÓN: Usa existsByIdUsuario() definido arriba
     */
    @Override
    default boolean existePorIdUsuario(Long idUsuario) {
        return existsByIdUsuario(idUsuario);
    }
}
