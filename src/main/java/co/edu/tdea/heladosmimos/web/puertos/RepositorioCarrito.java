package co.edu.tdea.heladosmimos.web.puertos;

import co.edu.tdea.heladosmimos.web.entidades.Carrito;
import java.util.Optional;

/**
 * PROPÓSITO: Contrato para acceso a datos de carritos de compras
 *
 * DECISIÓN TÉCNICA:
 * - Interface para desacoplar lógica de negocio de persistencia
 * - Permite múltiples implementaciones (JPA, MongoDB, etc.)
 * - Sigue patrón de arquitectura hexagonal
 *
 * DEPENDENCIAS:
 * - Entidad Carrito
 *
 * CONEXIÓN CON OTROS MÓDULOS:
 * - AdaptadorRepositorioCarrito: Implementación concreta con JPA
 * - ServicioCarritoCompras: Consume este contrato
 *
 * GENERADO POR: Claude - 2024-11-08
 */
public interface RepositorioCarrito {
    
    /**
     * Busca un carrito por su ID
     * 
     * @param idCarrito ID del carrito a buscar
     * @return Optional con el carrito si existe, vacío si no
     */
    Optional<Carrito> buscarPorId(Long idCarrito);
    
    /**
     * Busca el carrito activo de un usuario
     * 
     * @param idUsuario ID del usuario
     * @return Optional con el carrito si existe, vacío si no
     */
    Optional<Carrito> buscarPorIdUsuario(Long idUsuario);
    
    /**
     * Guarda o actualiza un carrito en la base de datos
     * 
     * @param carrito Carrito a guardar
     * @return Carrito guardado con ID generado
     */
    Carrito guardar(Carrito carrito);
    
    /**
     * Elimina un carrito de la base de datos
     * 
     * @param idCarrito ID del carrito a eliminar
     */
    void eliminar(Long idCarrito);
    
    /**
     * Verifica si existe un carrito para un usuario
     * 
     * @param idUsuario ID del usuario
     * @return true si existe, false si no
     */
    boolean existePorIdUsuario(Long idUsuario);
}