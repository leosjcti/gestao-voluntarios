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
    public String detalheMinisterio(
            @PathVariable Long id, 
            @RequestParam(value = "status", required = false) br.com.ibaji.voluntarios.model.enums.StatusTermo status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {
        
        int pageSize = 10;
        model.addAttribute("voluntariosPage", service.listarPorMinisterio(id, status, page, pageSize));
        model.addAttribute("nomeMinisterio", service.buscarNomeMinisterio(id));
        model.addAttribute("status", status);
        model.addAttribute("statusOpcoes", br.com.ibaji.voluntarios.model.enums.StatusTermo.values());
        model.addAttribute("ministerioId", id);
        
        return "admin-relatorios-detalhe";
    }

    @GetMapping("/detalhe/{id}/exportar")
    public void exportarCSV(
            @PathVariable Long id,
            @RequestParam(value = "status", required = false) br.com.ibaji.voluntarios.model.enums.StatusTermo status,
            jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        
        String nomeMin = service.buscarNomeMinisterio(id).replaceAll("[^a-zA-Z0-9_-]", "_");
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"voluntarios_" + nomeMin + ".csv\"");

        java.io.PrintWriter writer = response.getWriter();
        writer.println("Nome,Telefone,Email,Status,Proxima Renovacao");
        
        java.util.List<br.com.ibaji.voluntarios.model.Voluntario> voluntarios = service.listarTodosPorMinisterio(id, status);
        
        for (br.com.ibaji.voluntarios.model.Voluntario v : voluntarios) {
            String nome = v.getNomeCompleto() != null ? v.getNomeCompleto() : "";
            String tel = v.getTelefone() != null ? v.getTelefone() : "";
            String email = v.getEmail() != null ? v.getEmail() : "";
            String st = v.getStatusTermo() != null ? v.getStatusTermo().name() : "";
            String prox = v.getProximaRenovacao() != null ? v.getProximaRenovacao().toString() : "";
            
            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n", nome, tel, email, st, prox);
        }
        writer.flush();
    }
}