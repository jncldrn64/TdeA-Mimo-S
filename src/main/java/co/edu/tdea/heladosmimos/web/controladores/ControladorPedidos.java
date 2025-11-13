package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.Pedido;
import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioPedido;
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
 * Controlador para seguimiento de pedidos del usuario.
 */
@Controller
@RequestMapping("/pedidos")
public class ControladorPedidos {

    private static final Logger logger = LoggerFactory.getLogger(ControladorPedidos.class);

    @Autowired
    private RepositorioPedido repositorioPedido;

    /**
     * Muestra la lista de pedidos del usuario autenticado.
     */
    @GetMapping
    public String mostrarPedidos(HttpSession sesion, Model modelo) {
        // Obtener usuario de la sesión
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        if (usuario == null) {
            logger.warn("Intento de acceso a pedidos sin autenticación");
            return "redirect:/login";
        }

        // Buscar todos los pedidos del usuario
        List<Pedido> pedidos = repositorioPedido.buscarPorUsuario(usuario.getIdUsuario());

        logger.info("Usuario {} consultó sus pedidos. Total: {}",
            usuario.getIdUsuario(), pedidos.size());

        modelo.addAttribute("usuario", usuario);
        modelo.addAttribute("pedidos", pedidos);

        return "seguimiento-pedidos";
    }
}
