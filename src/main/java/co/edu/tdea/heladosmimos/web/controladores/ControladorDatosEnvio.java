package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioUsuario;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador para gestión de datos de envío del usuario.
 * Permite completar/actualizar información necesaria para procesar pedidos.
 */
@Controller
@RequestMapping("/datos-envio")
public class ControladorDatosEnvio {

    private static final Logger logger = LoggerFactory.getLogger(ControladorDatosEnvio.class);

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @GetMapping
    public String mostrarFormulario(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        // Recargar usuario desde BD para obtener datos más recientes
        Usuario usuarioDB = repositorioUsuario.buscarPorId(usuario.getIdUsuario());
        if (usuarioDB != null) {
            usuario = usuarioDB;
            session.setAttribute("usuario", usuario); // Actualizar sesión
        }

        model.addAttribute("usuario", usuario);
        return "datos-envio";
    }

    @PostMapping("/guardar")
    public String guardarDatosEnvio(
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam String ciudad,
            @RequestParam(required = false) String codigoPostal,
            @RequestParam String estadoProvincia,
            @RequestParam(required = false) String informacionAdicional,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            // Recargar usuario desde BD
            Usuario usuarioDB = repositorioUsuario.buscarPorId(usuario.getIdUsuario());

            if (usuarioDB == null) {
                model.addAttribute("error", "Usuario no encontrado");
                return "datos-envio";
            }

            // Actualizar datos de envío
            usuarioDB.setTelefono(telefono);
            usuarioDB.setDireccion(direccion);
            usuarioDB.setCiudad(ciudad);
            usuarioDB.setCodigoPostal(codigoPostal);
            usuarioDB.setEstadoProvincia(estadoProvincia);
            usuarioDB.setInformacionAdicional(informacionAdicional);

            // Guardar en BD
            repositorioUsuario.guardar(usuarioDB);

            // Actualizar sesión
            session.setAttribute("usuario", usuarioDB);

            logger.info("Datos de envío actualizados para usuario {}", usuarioDB.getIdUsuario());

            // Redirigir al carrito para continuar con checkout
            return "redirect:/carrito";

        } catch (Exception e) {
            logger.error("Error al guardar datos de envío: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al guardar los datos: " + e.getMessage());
            model.addAttribute("usuario", usuario);
            return "datos-envio";
        }
    }
}
