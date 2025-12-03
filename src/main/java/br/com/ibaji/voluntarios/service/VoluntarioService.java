package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.model.AntecedentesCriminais;
import br.com.ibaji.voluntarios.model.Ministerio;
import br.com.ibaji.voluntarios.model.Voluntario;
import br.com.ibaji.voluntarios.model.dto.ArquivoDTO;
import br.com.ibaji.voluntarios.model.dto.MinisterioDTO;
import br.com.ibaji.voluntarios.model.dto.VoluntarioAdminDTO;
import br.com.ibaji.voluntarios.model.dto.VoluntarioFormDTO;
import br.com.ibaji.voluntarios.model.enums.StatusAntecedentes;
import br.com.ibaji.voluntarios.model.enums.StatusTermo;
import br.com.ibaji.voluntarios.repository.AntecedentesCriminaisRepository;
import br.com.ibaji.voluntarios.repository.MinisterioRepository;
import br.com.ibaji.voluntarios.repository.VoluntarioRepository;
import br.com.ibaji.voluntarios.util.FormatadorTexto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VoluntarioService {

    private final VoluntarioRepository voluntarioRepository;
    private final MinisterioRepository ministerioRepository;
    private final AntecedentesCriminaisRepository antecedentesRepository;
    private final S3Service s3Service;
    private final PdfEmailService pdfEmailService;

    public VoluntarioService(VoluntarioRepository voluntarioRepo, MinisterioRepository ministerioRepo,
                             AntecedentesCriminaisRepository antecedentesRepo, S3Service s3Service, PdfEmailService pdfEmailService) {
        this.voluntarioRepository = voluntarioRepo;
        this.ministerioRepository = ministerioRepo;
        this.antecedentesRepository = antecedentesRepo;
        this.s3Service = s3Service;
        this.pdfEmailService =  pdfEmailService;
    }

    public List<MinisterioDTO> listarTodosMinisterios() {
        return ministerioRepository.findAll().stream()
                .map(m -> new MinisterioDTO(m.getId(), m.getNome()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void registrarVoluntario(VoluntarioFormDTO dto, MultipartFile arquivo) {
        Voluntario voluntario = new Voluntario();
        // ... (sets dos dados pessoais permanecem iguais) ...
        voluntario.setNomeCompleto(FormatadorTexto.padronizarNome(dto.getNomeCompleto()));
        voluntario.setEmail(dto.getEmail().toLowerCase());
        voluntario.setTelefone(dto.getTelefone());
        voluntario.setCpf(dto.getCpf());
        voluntario.setDataNascimento(dto.getDataNascimento());
        voluntario.setTermosAceitos(dto.getTermosAceitos());

        // Lógica de datas (mantém igual)
        LocalDate hoje = LocalDate.now();
        voluntario.setDataTermo(hoje);
        voluntario.setProximaRenovacao(hoje.plusYears(1));
        voluntario.setStatusTermo(StatusTermo.ATIVO);
        voluntario.setManualEntregue(false);

        // Ministérios (mantém igual)
        Set<Ministerio> ministerios = ministerioRepository.findAllByIdIn(dto.getIdsMinisterios());
        voluntario.setMinisterios(ministerios);

        // Salva o voluntário primeiro
        Voluntario salvo = voluntarioRepository.save(voluntario);

        // --- ALTERAÇÃO AQUI: Só faz upload se tiver arquivo ---
        if (arquivo != null && !arquivo.isEmpty()) {
            String caminhoS3 = s3Service.enviarArquivo(arquivo, salvo.getId());

            AntecedentesCriminais antecedentes = new AntecedentesCriminais();
            antecedentes.setVoluntario(salvo);
            antecedentes.setCaminhoArquivoS3(caminhoS3);
            antecedentes.setNomeOriginalArquivo(arquivo.getOriginalFilename());
            antecedentes.setStatus(StatusAntecedentes.PENDENTE_ANALISE);

            antecedentesRepository.save(antecedentes);

            pdfEmailService.gerarEEnviarTermo(salvo);
        }
        // Se não tiver arquivo, o voluntário é salvo sem registro de antecedentes
        // e no dashboard aparecerá como "Pendente"
    }

    public VoluntarioAdminDTO buscarParaEdicao(Long id) {
        Voluntario v = voluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voluntário não encontrado"));

        VoluntarioAdminDTO dto = new VoluntarioAdminDTO();
        dto.setId(v.getId());
        dto.setNomeCompleto(v.getNomeCompleto());
        dto.setEmail(v.getEmail());
        dto.setTelefone(v.getTelefone());
        dto.setDataNascimento(v.getDataNascimento());
        dto.setManualEntregue(v.getManualEntregue());
        dto.setDataIntegracao(v.getDataIntegracao());
        dto.setLiderIntegracao(v.getLiderIntegracao());
        dto.setStatusTermo(v.getStatusTermo());

        // Mapeia os IDs dos ministérios
        List<Long> ids = v.getMinisterios().stream().map(Ministerio::getId).toList();
        dto.setIdsMinisterios(ids);

        return dto;
    }

    @Transactional
    public void atualizarPeloAdmin(VoluntarioAdminDTO dto) {
        Voluntario v = voluntarioRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Voluntário não encontrado"));

        // Atualiza dados básicos
        v.setNomeCompleto(FormatadorTexto.padronizarNome(dto.getNomeCompleto()));
        v.setEmail(dto.getEmail().toLowerCase());
        v.setTelefone(dto.getTelefone());
        v.setDataNascimento(dto.getDataNascimento());

        // Atualiza dados administrativos
        v.setManualEntregue(dto.getManualEntregue());
        v.setDataIntegracao(dto.getDataIntegracao());
        v.setLiderIntegracao(FormatadorTexto.padronizarNome(dto.getLiderIntegracao()));
        v.setStatusTermo(dto.getStatusTermo());

        // Atualiza Ministérios
        if (dto.getIdsMinisterios() != null) {
            v.setMinisterios(ministerioRepository.findAllByIdIn(dto.getIdsMinisterios()));
        }

        // LÓGICA DO ARQUIVO (O Pulo do Gato)
        if (dto.getArquivoAntecedentes() != null && !dto.getArquivoAntecedentes().isEmpty()) {
            // Faz o upload
            String key = s3Service.enviarArquivo(dto.getArquivoAntecedentes(), v.getId());

            // Verifica se já existe o objeto Antecedentes, se não, cria
            if (v.getAntecedentes() == null) {
                AntecedentesCriminais ant = new AntecedentesCriminais();
                ant.setVoluntario(v);
                ant.setStatus(StatusAntecedentes.APROVADO); // Se o admin subiu, já tá aprovado
                v.setAntecedentes(ant);
            }

            // Atualiza o link do arquivo
            v.getAntecedentes().setCaminhoArquivoS3(key);
            v.getAntecedentes().setNomeOriginalArquivo(dto.getArquivoAntecedentes().getOriginalFilename());
            antecedentesRepository.save(v.getAntecedentes());
        }

        voluntarioRepository.save(v);
    }

    public void deletarVoluntario(Long id) {
        voluntarioRepository.deleteById(id);
    }

    public Page<Voluntario> listarPaginado(String busca, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("nomeCompleto").ascending());

        if (busca != null && !busca.isBlank()) {
            return voluntarioRepository.findByNomeCompletoContainingIgnoreCase(busca, pageable);
        }

        return voluntarioRepository.findAll(pageable);
    }

    public ArquivoDTO baixarAntecedentes(Long id) throws FileNotFoundException {
        // 1. Busca o voluntário
        Voluntario voluntario = voluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voluntário não encontrado"));

        // 2. Valida se tem antecedentes vinculados
        if (voluntario.getAntecedentes() == null || voluntario.getAntecedentes().getCaminhoArquivoS3() == null) {
            throw new FileNotFoundException("Nenhum arquivo de antecedentes anexado para este voluntário.");
        }

        // 3. Busca os metadados
        String s3Key = voluntario.getAntecedentes().getCaminhoArquivoS3();
        String nomeOriginal = voluntario.getAntecedentes().getNomeOriginalArquivo();

        // 4. Chama o serviço de S3 para pegar o Stream real
        var s3Stream = s3Service.baixarArquivo(s3Key);

        return new ArquivoDTO(nomeOriginal, s3Stream);
    }

    // DTO Interno para o Gráfico (pode colocar no pacote DTO se preferir)
    public record DadoGrafico(String label, long valor, int alturaPercentual) {}


}