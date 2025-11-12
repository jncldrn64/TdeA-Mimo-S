package co.edu.tdea.heladosmimos.web.casosdeuso;

import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioRegistro;
import co.edu.tdea.heladosmimos.web.excepciones.CorreoYaRegistradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Iniciar proceso de registro (Paso 1).
 * Valida que el correo esté disponible.
 */
@Service
public class CasoDeUsoIniciarRegistro {

    @Autowired
    private ServicioRegistro servicioRegistro;

    public void ejecutar(String correo) throws CorreoYaRegistradoException {
        servicioRegistro.verificarCorreoDisponible(correo);
    }
}
