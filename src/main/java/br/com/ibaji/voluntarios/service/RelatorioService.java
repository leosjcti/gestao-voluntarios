package br.com.ibaji.voluntarios.service;

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
}