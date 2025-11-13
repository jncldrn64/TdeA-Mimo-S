package co.edu.tdea.heladosmimos.web.configuracion;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Inicializador que corrige productos existentes con campo version NULL.
 * Esto evita NullPointerException en optimistic locking cuando se actualiza stock.
 */
@Configuration
public class InicializadorVersionProductos {

    private static final Logger logger = LoggerFactory.getLogger(InicializadorVersionProductos.class);

    @Bean
    public CommandLineRunner inicializarVersionProductos(RepositorioProducto repositorioProducto) {
        return args -> {
            try {
                // Buscar todos los productos
                List<Producto> productos = repositorioProducto.buscarTodos();

                int productosActualizados = 0;
                for (Producto producto : productos) {
                    // Si version es null, inicializar a 0
                    if (producto.getVersion() == null) {
                        producto.setVersion(0L);
                        repositorioProducto.guardar(producto);
                        productosActualizados++;
                        logger.info("Producto ID {} - version inicializada a 0", producto.getIdProducto());
                    }
                }

                if (productosActualizados > 0) {
                    logger.info("✓ Se inicializaron {} productos con version NULL a 0", productosActualizados);
                } else {
                    logger.debug("✓ Todos los productos ya tienen version inicializada");
                }

            } catch (Exception e) {
                logger.error("Error al inicializar versiones de productos: {}", e.getMessage(), e);
            }
        };
    }
}
