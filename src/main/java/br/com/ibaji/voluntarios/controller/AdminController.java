package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.Voluntario;
import br.com.ibaji.voluntarios.service.VoluntarioService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final VoluntarioService voluntarioService;

    public AdminController(VoluntarioService voluntarioService) {
        this.voluntarioService = voluntarioService;
    }

    @GetMapping
    public String dashboard(
            @RequestParam(value = "busca", required = false) String busca,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        // Define 10 itens por página
        int tamanhoPagina = 10;

        // Chama o serviço paginado
        Page<Voluntario> paginaVoluntarios = voluntarioService.listarPaginado(busca, page, tamanhoPagina);

        model.addAttribute("voluntariosPage", paginaVoluntarios);
        model.addAttribute("busca", busca); // Retorna o termo de busca para manter no input

        return "admin-dashboard";
    }
}