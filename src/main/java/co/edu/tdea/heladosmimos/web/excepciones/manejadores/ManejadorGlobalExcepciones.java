package co.edu.tdea.heladosmimos.web.excepciones.manejadores;

import co.edu.tdea.heladosmimos.web.excepciones.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones según principios SOLID.
 * Centraliza logging y respuestas de error consistentes.
 */
@ControllerAdvice
public class ManejadorGlobalExcepciones {

    private static final Logger logger = LoggerFactory.getLogger(ManejadorGlobalExcepciones.class);

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarProductoNoEncontrado(ProductoNoEncontradoException ex) {
        logger.warn("Producto no encontrado: {}", ex.getMessage());
        return construirRespuestaError(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductoNoDisponibleException.class)
    public ResponseEntity<Map<String, Object>> manejarProductoNoDisponible(ProductoNoDisponibleException ex) {
        logger.warn("Producto no disponible: {}", ex.getMessage());
        return construirRespuestaError(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> manejarStockInsuficiente(StockInsuficienteException ex) {
        logger.warn("Stock insuficiente: {}", ex.getMessage());
        return construirRespuestaError(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CantidadInvalidaException.class)
    public ResponseEntity<Map<String, Object>> manejarCantidadInvalida(CantidadInvalidaException ex) {
        logger.warn("Cantidad inválida: {}", ex.getMessage());
        return construirRespuestaError(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> manejarCredencialesInvalidas(CredencialesInvalidasException ex) {
        logger.warn("Intento de login fallido: {}", ex.getMessage());
        return construirRespuestaError(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CorreoYaRegistradoException.class)
    public ResponseEntity<Map<String, Object>> manejarCorreoYaRegistrado(CorreoYaRegistradoException ex) {
        logger.warn("Correo duplicado: {}", ex.getMessage());
        return construirRespuestaError(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CarritoVacioException.class)
    public ResponseEntity<Map<String, Object>> manejarCarritoVacio(CarritoVacioException ex) {
        logger.warn("Carrito vacío: {}", ex.getMessage());
        return construirRespuestaError(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SesionInvalidaException.class)
    public ResponseEntity<Map<String, Object>> manejarSesionInvalida(SesionInvalidaException ex) {
        logger.warn("Sesión inválida: {}", ex.getMessage());
        return construirRespuestaError(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ItemNoEncontradoEnCarritoException.class)
    public ResponseEntity<Map<String, Object>> manejarItemNoEnCarrito(ItemNoEncontradoEnCarritoException ex) {
        logger.warn("Item no encontrado en carrito: {}", ex.getMessage());
        return construirRespuestaError(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> manejarErrorPersistencia(DataAccessException ex) {
        logger.error("Error de persistencia: {}", ex.getMessage(), ex);
        return construirRespuestaError(
            new ErrorPersistenciaException("Error en base de datos", ex),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarExcepcionGenerica(Exception ex) {
        logger.error("Error inesperado: {}", ex.getMessage(), ex);

        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Error interno del servidor");
        error.put("tipoExcepcion", ex.getClass().getSimpleName());
        error.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private ResponseEntity<Map<String, Object>> construirRespuestaError(Exception ex, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", ex.getMessage());
        error.put("tipoExcepcion", ex.getClass().getSimpleName());
        error.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(status).body(error);
    }
}
