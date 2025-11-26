package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.dto.VoluntarioAdminDTO;
import br.com.ibaji.voluntarios.model.enums.StatusTermo;
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

    public AdminVoluntarioController(VoluntarioService service, MinisterioService ministerioService) {
        this.service = service;
        this.ministerioService = ministerioService;
    }

    // Formulário de Edição
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        VoluntarioAdminDTO dto = service.buscarParaEdicao(id);

        model.addAttribute("voluntario", dto);
        model.addAttribute("ministerios", ministerioService.listarTodos());
        model.addAttribute("statusOpcoes", StatusTermo.values()); // Para o select de status

        return "admin-voluntario-form";
    }

    // Salvar Edição
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute VoluntarioAdminDTO dto, RedirectAttributes attr) {
        try {
            service.atualizarPeloAdmin(dto);
            attr.addFlashAttribute("sucesso", "Voluntário atualizado com sucesso!");
        } catch (Exception e) {
            attr.addFlashAttribute("erro", "Erro ao atualizar: " + e.getMessage());
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
}