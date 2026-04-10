package br.com.ibaji.voluntarios.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String redirecionarParaCadastro() {
        return "redirect:/cadastro";
    }
}