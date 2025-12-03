package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.Ministerio;
import br.com.ibaji.voluntarios.repository.BaseRepository; // <--- Importante
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
    private final BaseRepository baseRepository; // <--- INJEÇÃO NOVA

    // Construtor atualizado
    public MinisterioController(MinisterioService service, BaseRepository baseRepository) {
        this.service = service;
        this.baseRepository = baseRepository;
    }

    // 1. Lista Paginada
    @GetMapping
    public String listar(
            @RequestParam(value = "busca", required = false) String busca,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        Page<Ministerio> paginaMinisterios = service.listarPaginado(busca, page, 10);
        model.addAttribute("ministeriosPage", paginaMinisterios);
        model.addAttribute("busca", busca);

        return "ministerios-lista";
    }

    // 2. Novo Cadastro
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("ministerio", new Ministerio());

        // AQUI ESTAVA FALTANDO: Mandar a lista de bases para o select
        model.addAttribute("bases", baseRepository.findAll());

        return "ministerios-form";
    }

    // 3. Edição
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var ministerio = service.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        model.addAttribute("ministerio", ministerio);

        // AQUI TAMBÉM: Mandar a lista de bases ao editar
        model.addAttribute("bases", baseRepository.findAll());

        return "ministerios-form";
    }

    // 4. Salvar
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Ministerio ministerio, RedirectAttributes attr) {
        service.salvar(ministerio);
        attr.addFlashAttribute("sucesso", "Ministério salvo com sucesso!");
        return "redirect:/admin/ministerios";
    }

    // 5. Deletar
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