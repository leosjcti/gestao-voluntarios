package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.Base;
import br.com.ibaji.voluntarios.repository.BaseRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/bases")
public class BaseController {

    private final BaseRepository repository;

    public BaseController(BaseRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listar(Model model) {
        // Lista todas as bases ordenadas por nome (pode melhorar com paginação depois se crescer muito)
        model.addAttribute("bases", repository.findAll());
        return "bases-lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("base", new Base());
        return "bases-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Base base = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("base", base);
        return "bases-form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Base base, RedirectAttributes attr) {
        repository.save(base);
        attr.addFlashAttribute("sucesso", "Base salva com sucesso!");
        return "redirect:/admin/bases";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes attr) {
        try {
            repository.deleteById(id);
            attr.addFlashAttribute("sucesso", "Base excluída com sucesso.");
        } catch (DataIntegrityViolationException e) {
            // Captura erro de chave estrangeira (se tiver ministério vinculado)
            attr.addFlashAttribute("erro", "Não é possível excluir: Existem ministérios vinculados a esta Base.");
        } catch (Exception e) {
            attr.addFlashAttribute("erro", "Erro ao excluir: " + e.getMessage());
        }
        return "redirect:/admin/bases";
    }
}