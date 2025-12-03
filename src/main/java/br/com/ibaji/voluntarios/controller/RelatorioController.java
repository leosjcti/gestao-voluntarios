package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.service.RelatorioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Controller
@RequestMapping("/admin/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping
    public String exibirRelatorios(
            @RequestParam(value = "minPage", defaultValue = "0") int minPage,   // Pág. Ministérios
            @RequestParam(value = "anivPage", defaultValue = "0") int anivPage, // Pág. Aniversariantes
            @RequestParam(value = "basePage", defaultValue = "0") int basePage, // Pág. Bases
            @RequestParam(value = "termoPage", defaultValue = "0") int termoPage, // Pág. Termos
            Model model) {

        // Define o tamanho fixo de 5 itens por card
        final int PAGE_SIZE = 5;

        // 1. Carrega os dados paginados (Chamando os métodos novos do Service)
        model.addAttribute("contagemPage", service.getContagemMinisterios(minPage));
        model.addAttribute("aniversariantesPage", service.getAniversariantesMesPaginado(anivPage, PAGE_SIZE));
        model.addAttribute("basesPage", service.getContagemPorBasePaginado(basePage, PAGE_SIZE));
        model.addAttribute("vencimentosPage", service.getTermosAVencerPaginado(termoPage, PAGE_SIZE));
        model.addAttribute("graficoCrescimento", service.getGraficoCrescimento());
        model.addAttribute("graficoEtario", service.getPerfilEtario());

        // 2. Carrega os dados de resumo (KPIs)
        model.addAttribute("resumo", service.getResumoGeral());

        // 3. Formata o mês atual para o título (Ex: "Novembro")
        String mesAtual = LocalDate.now().getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
        model.addAttribute("mesAtual", mesAtual);

        return "admin-relatorios";
    }

    // Rota para o Drill-down (Detalhe do Ministério)
    @GetMapping("/detalhe/{id}")
    public String detalheMinisterio(@PathVariable Long id, Model model) {
        model.addAttribute("voluntarios", service.listarPorMinisterio(id));
        model.addAttribute("nomeMinisterio", service.buscarNomeMinisterio(id));
        return "admin-relatorios-detalhe";
    }
}