package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.model.dto.RelatorioMinisterioDTO;
import br.com.ibaji.voluntarios.model.dto.RelatorioVencimentoDTO;
import br.com.ibaji.voluntarios.model.Ministerio;
import br.com.ibaji.voluntarios.model.Voluntario;
import br.com.ibaji.voluntarios.repository.MinisterioRepository;
import br.com.ibaji.voluntarios.repository.VoluntarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public RelatorioService(VoluntarioRepository voluntarioRepository,
                            MinisterioRepository ministerioRepository) {
        this.voluntarioRepository = voluntarioRepository;
        this.ministerioRepository = ministerioRepository;
    }

    public Page<RelatorioMinisterioDTO> getContagemMinisterios(int pagina) {
        // Página X, Tamanho 6
        return voluntarioRepository.contarVoluntariosPorMinisterio(PageRequest.of(pagina, 6));
    }

    public List<RelatorioVencimentoDTO> getTermosAVencer() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(30); // Próximos 30 dias

        // Busca todos que vencem até a data limite (inclui atrasados)
        List<Voluntario> voluntarios = voluntarioRepository.findByProximaRenovacaoLessThanEqual(limite);
        List<RelatorioVencimentoDTO> relatorio = new ArrayList<>();

        for (Voluntario v : voluntarios) {
            // Calcula dias: Se negativo, já venceu. Se positivo, falta vencer.
            long dias = ChronoUnit.DAYS.between(hoje, v.getProximaRenovacao());

            // Formata nomes dos ministérios (Ex: "Louvor, Kids")
            String ministerios = v.getMinisterios().stream()
                    .map(Ministerio::getNome)
                    .collect(Collectors.joining(", "));

            relatorio.add(new RelatorioVencimentoDTO(
                    v.getNomeCompleto(),
                    ministerios,
                    v.getProximaRenovacao(),
                    dias
            ));
        }

        // Ordena pelos que estão mais atrasados primeiro
        relatorio.sort((a, b) -> Long.compare(a.getDiasRestantes(), b.getDiasRestantes()));

        return relatorio;
    }

    public List<Voluntario> listarPorMinisterio(Long id) { return voluntarioRepository.findByMinisteriosId(id); }

    public String buscarNomeMinisterio(Long id) {
        return ministerioRepository.findById(id)
                .map(Ministerio::getNome) // Se achar, pega o nome
                .orElse("Ministério não encontrado"); // Se não achar (ex: ID inválido)
    }

    // ... dentro da classe RelatorioService

    public Map<String, Object> getResumoGeral() {
        LocalDate hoje = LocalDate.now();

        Map<String, Object> resumo = new HashMap<>();
        resumo.put("total", voluntarioRepository.count());
        resumo.put("novosMes", voluntarioRepository.contarNovosNoMes(hoje.getMonthValue(), hoje.getYear()));
        resumo.put("semAtestado", voluntarioRepository.countByAntecedentesIsNull());
        resumo.put("manualPendente", voluntarioRepository.countByManualEntregueFalse());

        return resumo;
    }

    public List<Voluntario> getAniversariantesMes() {
        return voluntarioRepository.findAniversariantesDoMes(LocalDate.now().getMonthValue());
    }
}