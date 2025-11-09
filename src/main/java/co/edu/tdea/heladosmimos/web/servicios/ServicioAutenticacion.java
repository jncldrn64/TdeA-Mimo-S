package co.edu.tdea.heladosmimos.web.servicios;

import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioUsuario;
import co.edu.tdea.heladosmimos.web.excepciones.CredencialesInvalidasException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación de usuarios.
 * Valida credenciales usando BCrypt para contraseñas.
 */
@Service
public class ServicioAutenticacion {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    private BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    /**
     * Valida credenciales de usuario.
     * Lanza excepción genérica por seguridad (no especifica si es correo o contraseña).
     */
    public Usuario validarCredenciales(String correo, String contrasena)
        throws CredencialesInvalidasException {

        Usuario usuario = repositorioUsuario.buscarPorCorreoElectronico(correo);

        if (usuario == null) {
            throw new CredencialesInvalidasException(
                "Correo o contraseña incorrectos");
        }

        if (!usuario.getEstaActivo()) {
            throw new CredencialesInvalidasException(
                "Correo o contraseña incorrectos");
        }

        if (!encriptador.matches(contrasena, usuario.getContrasenaEncriptada())) {
            throw new CredencialesInvalidasException(
                "Correo o contraseña incorrectos");
        }

        return usuario;
    }
}
