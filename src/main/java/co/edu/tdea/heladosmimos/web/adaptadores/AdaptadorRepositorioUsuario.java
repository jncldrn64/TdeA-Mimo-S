package co.edu.tdea.heladosmimos.web.adaptadores;

import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.puertos.RepositorioUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de repositorio para Usuario usando Spring Data JPA.
 * Implementa tanto JpaRepository como el puerto RepositorioUsuario.
 */
@Repository
public interface AdaptadorRepositorioUsuario
    extends JpaRepository<Usuario, Long>, RepositorioUsuario {

    Usuario findByCorreoElectronico(String correo);
    Boolean existsByCorreoElectronico(String correo);

    @Override
    default Usuario buscarPorCorreoElectronico(String correo) {
        return findByCorreoElectronico(correo);
    }

    @Override
    default Boolean existePorCorreoElectronico(String correo) {
        return existsByCorreoElectronico(correo);
    }

    @Override
    default Usuario guardar(Usuario usuario) {
        return save(usuario);
    }

    @Override
    default Usuario buscarPorId(Long id) {
        return findById(id).orElse(null);
    }
}
