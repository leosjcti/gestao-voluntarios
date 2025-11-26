package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.Ministerio;
import br.com.ibaji.voluntarios.service.MinisterioService;
import br.com.ibaji.voluntarios.service.MinisterioService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/ministerios")
public class MinisterioController {

    private final MinisterioService service;

    public MinisterioController(MinisterioService service) {
        this.service = service;
    }

    // 1. Lista todos
    @GetMapping
    public String listar(
            @RequestParam(value = "busca", required = false) String busca,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        // Define 10 itens por página
        Page<Ministerio> paginaMinisterios = service.listarPaginado(busca, page, 10);

        model.addAttribute("ministeriosPage", paginaMinisterios);
        model.addAttribute("busca", busca);

        return "ministerios-lista";
    }

    // 2. Abre formulário de NOVO cadastro
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("ministerio", new Ministerio());
        return "ministerios-form"; // Nome do HTML do form
    }

    // 3. Abre formulário de EDIÇÃO
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var ministerio = service.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        model.addAttribute("ministerio", ministerio);
        return "ministerios-form";
    }

    // 4. Salva (Tanto novo quanto edição)
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Ministerio ministerio, RedirectAttributes attr) {
        service.salvar(ministerio);
        attr.addFlashAttribute("sucesso", "Ministério salvo com sucesso!");
        return "redirect:/admin/ministerios";
    }

    // 5. Deleta
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes attr) {
        try {
            service.deletar(id);
            attr.addFlashAttribute("sucesso", "Ministério excluído.");
        } catch (Exception e) {
            attr.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/ministerios";
    }
}