package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.model.dto.GraficoDTO;
import br.com.ibaji.voluntarios.model.dto.RelatorioMinisterioDTO;
import br.com.ibaji.voluntarios.model.dto.RelatorioVencimentoDTO;
import br.com.ibaji.voluntarios.model.Ministerio;
import br.com.ibaji.voluntarios.model.Voluntario;
import br.com.ibaji.voluntarios.repository.MinisterioRepository;
import br.com.ibaji.voluntarios.repository.VoluntarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final VoluntarioRepository voluntarioRepository;
    private final MinisterioRepository ministerioRepository;

    // Tamanho fixo da página para os cards do dashboard (5 itens)
    private static final int CARD_PAGE_SIZE = 5;

    public RelatorioService(VoluntarioRepository voluntarioRepository,
                            MinisterioRepository ministerioRepository) {
        this.voluntarioRepository = voluntarioRepository;
        this.ministerioRepository = ministerioRepository;
    }

    // --- MÉTODOS DE RELATÓRIO PRINCIPAIS ---

    // 1. Contagem de Voluntários por Ministério (Usa Paginação JPA)
    public Page<RelatorioMinisterioDTO> getContagemMinisterios(int pagina) {
        return voluntarioRepository.contarVoluntariosPorMinisterio(PageRequest.of(pagina, 6));
    }

    // 2. Aniversariantes do Mês (Paginado em Memória)
    public Page<Voluntario> getAniversariantesMesPaginado(int pagina, int tamanho) { // RECEBE TAMANHO
        List<Voluntario> listaCompleta = voluntarioRepository.findAniversariantesDoMes(LocalDate.now().getMonthValue());

        return paginateList(listaCompleta, pagina, tamanho);
    }

    // 3. Distribuição por Base (Paginado em Memória)
    public Page<RelatorioMinisterioDTO> getContagemPorBasePaginado(int pagina, int tamanho) { // RECEBE TAMANHO
        List<RelatorioMinisterioDTO> listaCompleta = voluntarioRepository.contarVoluntariosPorBase();

        return paginateList(listaCompleta, pagina, tamanho);
    }

    // 4. Termos a Vencer (Paginado em Memória)
    public Page<RelatorioVencimentoDTO> getTermosAVencerPaginado(int pagina, int tamanho) { // RECEBE TAMANHO
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(30);

        List<Voluntario> voluntarios = voluntarioRepository.findByProximaRenovacaoLessThanEqual(limite);
        List<RelatorioVencimentoDTO> listaCompleta = new ArrayList<>();

        for (Voluntario v : voluntarios) {
            long dias = ChronoUnit.DAYS.between(hoje, v.getProximaRenovacao());
            String ministerios = v.getMinisterios().stream()
                    .map(Ministerio::getNome)
                    .collect(Collectors.joining(", "));

            listaCompleta.add(new RelatorioVencimentoDTO(v.getNomeCompleto(), ministerios, v.getProximaRenovacao(), dias));
        }

        // Ordena pelos que estão mais atrasados primeiro
        listaCompleta.sort((a, b) -> Long.compare(a.getDiasRestantes(), b.getDiasRestantes()));

        return paginateList(listaCompleta, pagina, tamanho);
    }

    // 5. Resumo Geral (KPIs)
    public Map<String, Object> getResumoGeral() {
        LocalDate hoje = LocalDate.now();
        Map<String, Object> resumo = new HashMap<>();

        resumo.put("total", voluntarioRepository.count());
        resumo.put("novosMes", voluntarioRepository.contarNovosNoMes(hoje.getMonthValue(), hoje.getYear()));
        resumo.put("semAtestado", voluntarioRepository.countByAntecedentesIsNull());
        resumo.put("faltaIntegracao", voluntarioRepository.countByDataIntegracaoIsNull());
        resumo.put("manualPendente", voluntarioRepository.countByManualEntregueFalse());

        return resumo;
    }

    // 6. Listagem para Drill-Down (Não paginado)
    public List<Voluntario> listarPorMinisterio(Long id) {
        return voluntarioRepository.findByMinisteriosId(id);
    }

    // 7. Busca o nome do ministério
    public String buscarNomeMinisterio(Long id) {
        return ministerioRepository.findById(id)
                .map(Ministerio::getNome)
                .orElse("Ministério não encontrado");
    }

    // --- MÉTODO AUXILIAR PARA PAGINAÇÃO EM MEMÓRIA (DRY) ---
    private <T> Page<T> paginateList(List<T> list, int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, list.size());

        // Se a página solicitada for inválida (ex: 5 em uma lista de 2 páginas), retorna a primeira página ou vazia.
        if (start > list.size() && list.size() > 0) {
            start = 0;
            end = Math.min(size, list.size());
            page = 0;
        } else if (start > list.size() && list.size() == 0) {
            return new PageImpl<>(List.of(), PageRequest.of(0, size), 0);
        }

        List<T> pageContent = list.subList(start, end);
        Pageable pageable = PageRequest.of(page, size);

        return new PageImpl<>(pageContent, pageable, list.size());
    }

    public List<VoluntarioService.DadoGrafico> getGraficoCrescimento() {
        List<VoluntarioService.DadoGrafico> grafico = new ArrayList<>();
        LocalDate data = LocalDate.now().minusMonths(5); // Começa 5 meses atrás

        // Busca o maior valor para calcular a escala (100%)
        long maxValor = 1;
        Map<String, Long> tempMap = new HashMap<>();

        for (int i = 0; i < 6; i++) {
            long qtd = voluntarioRepository.countByMesAno(data.getMonthValue(), data.getYear());
            if (qtd > maxValor) maxValor = qtd;

            // Ex: "NOV"
            String label = data.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, new java.util.Locale("pt", "BR")).toUpperCase();
            tempMap.put(label, qtd);

            // Avança pro próximo mês do loop
            data = data.plusMonths(1);
        }

        // Reconstrói a lista ordenada e calcula altura
        data = LocalDate.now().minusMonths(5);
        for (int i = 0; i < 6; i++) {
            String label = data.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, new java.util.Locale("pt", "BR")).toUpperCase();
            long qtd = tempMap.get(label);

            // Regra de 3 para altura da barra (mínimo 5% pra não sumir)
            int altura = (int) ((qtd * 100) / maxValor);
            if (altura < 5) altura = 5;

            grafico.add(new VoluntarioService.DadoGrafico(label, qtd, altura));
            data = data.plusMonths(1);
        }

        return grafico;
    }

    public List<GraficoDTO> getPerfilEtario() {
        List<LocalDate> nascimentos = voluntarioRepository.findAllDataNascimento();
        LocalDate hoje = LocalDate.now();

        long total = nascimentos.size();
        if (total == 0) return new ArrayList<>();

        long teens = 0;   // < 18
        long jovens = 0;  // 18 - 29
        long adultos = 0; // 30 - 49
        long senior = 0;  // 50+

        for (LocalDate nasc : nascimentos) {
            int idade = Period.between(nasc, hoje).getYears();
            if (idade < 18) teens++;
            else if (idade < 30) jovens++;
            else if (idade < 50) adultos++;
            else senior++;
        }

        List<GraficoDTO> grafico = new ArrayList<>();
        // Helper para calcular % seguro
        grafico.add(new GraficoDTO("Teens (<18)", teens, calcularPorcentagem(teens, total)));
        grafico.add(new GraficoDTO("Jovens (18-29)", jovens, calcularPorcentagem(jovens, total)));
        grafico.add(new GraficoDTO("Adultos (30-49)", adultos, calcularPorcentagem(adultos, total)));
        grafico.add(new GraficoDTO("Sênior (50+)", senior, calcularPorcentagem(senior, total)));

        return grafico;
    }

    private int calcularPorcentagem(long parte, long total) {
        if (total == 0) return 0;
        return (int) ((parte * 100) / total);
    }
}