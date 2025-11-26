package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.service.RelatorioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping
    public String exibirRelatorios(
            @RequestParam(value = "minPage", defaultValue = "0") int minPage, // Novo Parametro
            Model model) {

        // Passa a página solicitada para o serviço
        model.addAttribute("contagemPage", service.getContagemMinisterios(minPage));

        // ... (o resto continua igual: vencimentos, resumo, aniversariantes) ...
        model.addAttribute("vencimentos", service.getTermosAVencer());
        model.addAttribute("resumo", service.getResumoGeral());
        model.addAttribute("aniversariantes", service.getAniversariantesMes());
        model.addAttribute("mesAtual", java.time.LocalDate.now().getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("pt", "BR")));

        return "admin-relatorios";
    }

    @GetMapping("/detalhe/{id}")
    public String detalheMinisterio(@PathVariable Long id, Model model) {
        model.addAttribute("voluntarios", service.listarPorMinisterio(id));
        model.addAttribute("nomeMinisterio", service.buscarNomeMinisterio(id)); // Pequeno helper pra pegar o nome

        return "admin-relatorios-detalhe";
    }
}
