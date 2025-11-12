package co.edu.tdea.heladosmimos.web.casosdeuso;

import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioRegistro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Completar registro con datos completos (Paso 2).
 * Crea el usuario en la base de datos.
 */
@Service
public class CasoDeUsoCompletarRegistro {

    @Autowired
    private ServicioRegistro servicioRegistro;

    public Usuario ejecutar(String nombre, String apellido,
                          String correo, String contrasena,
                          String telefono, String direccion,
                          String nit) {

        return servicioRegistro.crearUsuario(nombre, apellido, correo,
                                           contrasena, telefono,
                                           direccion, nit);
    }
}
