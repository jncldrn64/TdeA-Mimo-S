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
 * Controlador para la gestión de pedidos del usuario.
 * Permite consultar el historial de pedidos realizados.
 */
@Controller
@RequestMapping("/pedidos")
public class ControladorPedidos {

    private static final Logger logger = LoggerFactory.getLogger(ControladorPedidos.class);

    @Autowired
    private RepositorioPedido repositorioPedido;

    /**
     * Muestra el historial de pedidos del usuario autenticado.
     */
    @GetMapping
    public String mostrarPedidos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            logger.warn("Intento de acceso a pedidos sin autenticación");
            return "redirect:/login";
        }

        List<Pedido> pedidos = repositorioPedido.buscarPorUsuario(usuario.getIdUsuario());

        logger.info("Usuario {} consultó sus pedidos. Total: {}",
            usuario.getIdUsuario(), pedidos.size());

        model.addAttribute("usuario", usuario);
        model.addAttribute("pedidos", pedidos);

        return "seguimiento-pedidos";
    }
}
