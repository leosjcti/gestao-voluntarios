package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.dto.VoluntarioAdminDTO;
import br.com.ibaji.voluntarios.model.enums.StatusTermo;
import br.com.ibaji.voluntarios.repository.BaseRepository;
import br.com.ibaji.voluntarios.service.MinisterioService;
import br.com.ibaji.voluntarios.service.VoluntarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/voluntarios")
public class AdminVoluntarioController {

    private final VoluntarioService service;
    private final MinisterioService ministerioService;
    private final BaseRepository baseRepository;

    public AdminVoluntarioController(VoluntarioService service,
                                     MinisterioService ministerioService,
                                     BaseRepository baseRepository) {
        this.service = service;
        this.ministerioService = ministerioService;
        this.baseRepository = baseRepository;
    }

    // Formulário de Edição
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        VoluntarioAdminDTO dto = service.buscarParaEdicao(id);

        model.addAttribute("voluntario", dto);
        model.addAttribute("statusOpcoes", StatusTermo.values());
        model.addAttribute("listaBases", baseRepository.findAll());

        return "admin-voluntario-form";
    }

    // Salvar Edição
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute VoluntarioAdminDTO dto, RedirectAttributes attr) {
        try {
            service.salvarPeloAdmin(dto);
            attr.addFlashAttribute("sucesso", "Voluntário salvo com sucesso!");
        } catch (Exception e) {
            attr.addFlashAttribute("erro", "Erro ao salvar: " + e.getMessage());
            // Se der erro, volta pro form (idealmente teria que repopular as bases aqui também,
            // mas vamos simplificar o redirect)
            return "redirect:/admin/voluntarios/novo";
        }
        return "redirect:/admin";
    }

    // Deletar Voluntário
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes attr) {
        try {
            service.deletarVoluntario(id);
            attr.addFlashAttribute("sucesso", "Voluntário removido.");
        } catch (Exception e) {
            attr.addFlashAttribute("erro", "Erro ao remover.");
        }
        return "redirect:/admin";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("voluntario", new VoluntarioAdminDTO()); // DTO Vazio
        //model.addAttribute("ministerios", ministerioService.listarTodos());
        model.addAttribute("statusOpcoes", StatusTermo.values());
        model.addAttribute("listaBases", baseRepository.findAll());
        return "admin-voluntario-form"; // Reusa o mesmo HTML
    }
}