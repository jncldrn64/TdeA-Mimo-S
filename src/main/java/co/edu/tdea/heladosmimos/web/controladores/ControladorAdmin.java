package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.entidades.enums.RolUsuario;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioProducto;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controlador para el panel de administración.
 * Solo accesible para usuarios con rol ADMINISTRADOR_VENTAS.
 */
@Controller
@RequestMapping("/admin")
public class ControladorAdmin {

    private static final Logger logger = LoggerFactory.getLogger(ControladorAdmin.class);

    @Autowired
    private RepositorioProducto repositorioProducto;

    /**
     * Muestra el panel de administración de productos.
     * Solo accesible para administradores.
     */
    @GetMapping("/productos")
    public String mostrarPanelProductos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Validar autenticación
        if (usuario == null) {
            logger.warn("Intento de acceso a panel admin sin autenticación");
            return "redirect:/login";
        }

        // Validar rol de administrador
        if (usuario.getRol() != RolUsuario.ADMINISTRADOR_VENTAS) {
            logger.warn("Usuario {} (rol: {}) intentó acceder al panel admin",
                usuario.getIdUsuario(), usuario.getRol());
            return "redirect:/catalogo";
        }

        // Obtener todos los productos
        List<Producto> productos = repositorioProducto.buscarTodos();

        logger.info("Administrador {} accedió al panel de productos. Total: {}",
            usuario.getIdUsuario(), productos.size());

        model.addAttribute("usuario", usuario);
        model.addAttribute("productos", productos);

        return "admin-productos";
    }
}
