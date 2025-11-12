package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoLogin;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoIniciarRegistro;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoCompletarRegistro;
import co.edu.tdea.heladosmimos.web.excepciones.CredencialesInvalidasException;
import co.edu.tdea.heladosmimos.web.excepciones.CorreoYaRegistradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

// API REST para autenticación (RF-03)
@RestController
@RequestMapping("/api/auth")
public class ControladorAutenticacionREST {

    @Autowired
    private CasoDeUsoLogin casoDeUsoLogin;

    @Autowired
    private CasoDeUsoIniciarRegistro casoDeUsoIniciarRegistro;

    @Autowired
    private CasoDeUsoCompletarRegistro casoDeUsoCompletarRegistro;

    @PostMapping("/validar-correo")
    public ResponseEntity<Map<String, Object>> validarCorreo(@RequestParam String correo)
            throws CorreoYaRegistradoException {
        casoDeUsoIniciarRegistro.ejecutar(correo);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Correo disponible");
        respuesta.put("correo", correo);

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrarUsuario(
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam String nit) throws CorreoYaRegistradoException {

        casoDeUsoIniciarRegistro.ejecutar(correo);

        Usuario usuario = casoDeUsoCompletarRegistro.ejecutar(
            nombre, apellido, correo, contrasena, telefono, direccion, nit
        );

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Usuario registrado exitosamente");
        respuesta.put("usuario", Map.of(
            "idUsuario", usuario.getIdUsuario(),
            "correo", usuario.getCorreoElectronico(),
            "nombre", usuario.getNombre() + " " + usuario.getApellido(),
            "rol", usuario.getRol().toString()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String correo,
            @RequestParam String contrasena,
            HttpSession sesion) throws CredencialesInvalidasException {

        Usuario usuario = casoDeUsoLogin.ejecutar(correo, contrasena);
        sesion.setAttribute("usuario", usuario);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Login exitoso");
        respuesta.put("usuario", Map.of(
            "idUsuario", usuario.getIdUsuario(),
            "correo", usuario.getCorreoElectronico(),
            "nombre", usuario.getNombre() + " " + usuario.getApellido(),
            "rol", usuario.getRol().toString()
        ));

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession sesion) {
        sesion.invalidate();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", "Sesión cerrada");

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> obtenerSesion(HttpSession sesion) {
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        Map<String, Object> respuesta = new HashMap<>();

        if (usuario != null) {
            respuesta.put("autenticado", true);
            respuesta.put("usuario", Map.of(
                "idUsuario", usuario.getIdUsuario(),
                "correo", usuario.getCorreoElectronico(),
                "nombre", usuario.getNombre() + " " + usuario.getApellido(),
                "rol", usuario.getRol().toString()
            ));
        } else {
            respuesta.put("autenticado", false);
        }

        return ResponseEntity.ok(respuesta);
    }
}
