package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.Espaco;
import br.com.ibaji.voluntarios.repository.EspacoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/espacos")
public class EspacoController {

    private final EspacoRepository espacoRepository;

    public EspacoController(EspacoRepository espacoRepository) {
        this.espacoRepository = espacoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("espacos", espacoRepository.findAll());
        return "espacos-lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("espaco", new Espaco());
        return "espacos-form";
    }

    @PostMapping("/novo")
    public String salvar(@ModelAttribute Espaco espaco) {
        espacoRepository.save(espaco);
        return "redirect:/admin/espacos";
    }
}
