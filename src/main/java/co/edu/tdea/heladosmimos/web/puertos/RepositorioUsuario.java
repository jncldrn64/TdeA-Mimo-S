package co.edu.tdea.heladosmimos.web.puertos;

import co.edu.tdea.heladosmimos.web.entidades.Usuario;

/**
 * Puerto (interfaz) para operaciones de repositorio de usuarios.
 * Define el contrato que deben cumplir las implementaciones.
 */
public interface RepositorioUsuario {
    Usuario buscarPorCorreoElectronico(String correo);
    Boolean existePorCorreoElectronico(String correo);
    Usuario guardar(Usuario usuario);
    Usuario buscarPorId(Long id);
}
