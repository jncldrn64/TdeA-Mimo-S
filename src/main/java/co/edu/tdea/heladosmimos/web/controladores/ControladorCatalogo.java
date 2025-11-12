package co.edu.tdea.heladosmimos.web.controladores;

import co.edu.tdea.heladosmimos.web.entidades.Producto;
import co.edu.tdea.heladosmimos.web.entidades.Usuario;
import co.edu.tdea.heladosmimos.web.casosdeuso.CasoDeUsoConsultarProductos;
import co.edu.tdea.heladosmimos.web.servicios.requisitos.funcionales.ServicioCarritoCompras;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// Controlador de catálogo de productos (server-side rendering con Thymeleaf)
@Controller
public class ControladorCatalogo {

    @Autowired
    private CasoDeUsoConsultarProductos casoDeUsoConsultar;

    @Autowired
    private ServicioCarritoCompras servicioCarritoCompras;

    @GetMapping("/catalogo")
    public String mostrarCatalogo(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        List<Producto> productos = casoDeUsoConsultar.ejecutarListarActivos();
        Integer cantidadItems = servicioCarritoCompras.contarTotalDeProductos();

        model.addAttribute("usuario", usuario);
        model.addAttribute("productos", productos);
        model.addAttribute("cantidadItems", cantidadItems);

        return "catalogo";
    }
}
