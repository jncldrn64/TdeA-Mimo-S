package co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales;

import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.entidades.enums.RolUsuario;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioUsuario;
import co.edu.tdea.heladosmimos.web.excepciones.CorreoYaRegistradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * Servicio de registro de nuevos usuarios.
 * Valida disponibilidad de correo y encripta contraseñas.
 */
@Service
public class ServicioRegistro {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    private BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    /**
     * Verifica si un correo ya está registrado.
     */
    public void verificarCorreoDisponible(String correo)
        throws CorreoYaRegistradoException {

        if (repositorioUsuario.existePorCorreoElectronico(correo)) {
            throw new CorreoYaRegistradoException(
                "Este correo ya está registrado");
        }
    }

    /**
     * Crea un nuevo usuario con rol CLIENTE por defecto.
     * La contraseña se encripta con BCrypt.
     */
    public Usuario crearUsuario(String nombre, String apellido,
                               String correo, String contrasena,
                               String telefono, String direccion,
                               String nit) {

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreoElectronico(correo);
        usuario.setContrasenaEncriptada(encriptador.encode(contrasena));
        usuario.setRol(RolUsuario.CLIENTE);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setEstaActivo(true);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        usuario.setNit(nit);

        return repositorioUsuario.guardar(usuario);
    }
}
