package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.seguridad.casosdeuso.CasoDeUsoLogin;
import co.edu.tdea.heladosmimos.web.seguridad.casosdeuso.CasoDeUsoIniciarRegistro;
import co.edu.tdea.heladosmimos.web.seguridad.casosdeuso.CasoDeUsoCompletarRegistro;
import co.edu.tdea.heladosmimos.web.excepciones.CredencialesInvalidasException;
import co.edu.tdea.heladosmimos.web.excepciones.CorreoYaRegistradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

/**
 * Controlador de autenticación: login y registro de usuarios.
 * Gestiona el flujo de dos pasos para el registro.
 */
@Controller
public class ControladorAutenticacion {

    @Autowired
    private CasoDeUsoLogin casoDeUsoLogin;

    @Autowired
    private CasoDeUsoIniciarRegistro casoDeUsoIniciarRegistro;

    @Autowired
    private CasoDeUsoCompletarRegistro casoDeUsoCompletarRegistro;

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(required = false) String registro, Model modelo) {
        if ("exitoso".equals(registro)) {
            modelo.addAttribute("exito", "Registro completado. Inicia sesión.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                              @RequestParam String contrasena,
                              HttpSession sesion,
                              Model modelo) {
        try {
            Usuario usuario = casoDeUsoLogin.ejecutar(correo, contrasena);
            sesion.setAttribute("usuario", usuario);
            return "redirect:/catalogo";

        } catch (CredencialesInvalidasException e) {
            modelo.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession sesion) {
        sesion.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistroPaso1() {
        return "registro-paso1";
    }

    @PostMapping("/registro")
    public String procesarRegistroPaso1(@RequestParam String correo,
                                      @RequestParam String contrasena,
                                      HttpSession sesion,
                                      Model modelo) {
        try {
            casoDeUsoIniciarRegistro.ejecutar(correo);
            sesion.setAttribute("correo_registro", correo);
            sesion.setAttribute("contrasena_registro", contrasena);
            return "redirect:/registro/completar";

        } catch (CorreoYaRegistradoException e) {
            modelo.addAttribute("error", e.getMessage());
            return "registro-paso1";
        }
    }

    @GetMapping("/registro/completar")
    public String mostrarRegistroPaso2(HttpSession sesion, Model modelo) {
        String correo = (String) sesion.getAttribute("correo_registro");

        if (correo == null) {
            return "redirect:/registro";
        }

        modelo.addAttribute("correo", correo);
        return "registro-paso2";
    }

    @PostMapping("/registro/completar")
    public String procesarRegistroPaso2(@RequestParam String nombre,
                                      @RequestParam String apellido,
                                      @RequestParam String telefono,
                                      @RequestParam String direccion,
                                      @RequestParam String nit,
                                      @RequestParam String confirmarContrasena,
                                      HttpSession sesion,
                                      Model modelo) {

        String correo = (String) sesion.getAttribute("correo_registro");
        String contrasena = (String) sesion.getAttribute("contrasena_registro");

        if (correo == null || contrasena == null) {
            return "redirect:/registro";
        }

        if (!contrasena.equals(confirmarContrasena)) {
            modelo.addAttribute("error", "Las contraseñas no coinciden");
            modelo.addAttribute("correo", correo);
            return "registro-paso2";
        }

        casoDeUsoCompletarRegistro.ejecutar(
            nombre, apellido, correo, contrasena,
            telefono, direccion, nit
        );

        sesion.removeAttribute("correo_registro");
        sesion.removeAttribute("contrasena_registro");

        return "redirect:/login?registro=exitoso";
    }

    @GetMapping("/registro/cancelar")
    public String cancelarRegistro(HttpSession sesion) {
        sesion.removeAttribute("correo_registro");
        sesion.removeAttribute("contrasena_registro");
        return "redirect:/login";
    }
}
