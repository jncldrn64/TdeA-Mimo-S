package co.edu.tdea.heladosmimos.web.seguridad.casosdeuso;

import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.servicios.ServicioAutenticacion;
import co.edu.tdea.heladosmimos.web.excepciones.CredencialesInvalidasException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Iniciar sesión.
 * Valida credenciales y retorna el usuario autenticado.
 */
@Service
public class CasoDeUsoLogin {

    @Autowired
    private ServicioAutenticacion servicioAutenticacion;

    public Usuario ejecutar(String correo, String contrasena)
        throws CredencialesInvalidasException {

        return servicioAutenticacion.validarCredenciales(correo, contrasena);
    }
}
