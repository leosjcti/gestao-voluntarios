package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.Usuario;
import br.com.ibaji.voluntarios.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", service.listarTodos());
        return "usuarios-lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", service.buscarPorId(id));
        return "usuarios-form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario, RedirectAttributes attr) {
        service.salvar(usuario);
        attr.addFlashAttribute("sucesso", "Usuário salvo com sucesso!");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes attr) {
        // Proteção para não deletar o próprio admin principal se quiser
        if (id == 1L) {
            attr.addFlashAttribute("erro", "Não é possível excluir o administrador raiz.");
        } else {
            service.deletar(id);
            attr.addFlashAttribute("sucesso", "Usuário removido.");
        }
        return "redirect:/admin/usuarios";
    }
}
