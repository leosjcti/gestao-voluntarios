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
import java.time.LocalDateTime;
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
        this.pdfEmailService = pdfEmailService;
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
        voluntario.setMembroIbaji(dto.isMembroIbaji());

        // Lógica de datas (mantém igual)
        LocalDate hoje = LocalDate.now();
        voluntario.setDataTermo(hoje);
        voluntario.setProximaRenovacao(hoje.plusYears(1));
        voluntario.setStatusTermo(StatusTermo.ATIVO);

        // Ministérios (mantém igual)
        Set<Ministerio> ministerios = ministerioRepository.findAllByIdIn(dto.getIdsMinisterios());
        voluntario.setMinisterios(ministerios);

        // --- NOVO: MAPEAMENTO DO MENOR ---
        voluntario.setMenorIdade(dto.isMenorIdade());
        if (dto.isMenorIdade()) {
            voluntario.setNomeResponsavel(dto.getNomeResponsavel());
            voluntario.setCpfResponsavel(dto.getCpfResponsavel());
            voluntario.setEmailResponsavel(dto.getEmailResponsavel());
            voluntario.setTelefoneResponsavel(dto.getTelefoneResponsavel());
        }

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
        dto.setCpf(v.getCpf());
        dto.setStatusTermo(v.getStatusTermo());
        dto.setAntecedentesAnalisados(v.getAntecedentesAnalisados());
        dto.setMembroIbaji(v.getMembroIbaji() != null && v.getMembroIbaji());

        // Mapeia os IDs dos ministérios
        List<Long> ids = v.getMinisterios().stream().map(Ministerio::getId).toList();
        dto.setIdsMinisterios(ids);

        return dto;
    }

    @Transactional
    public void salvarPeloAdmin(VoluntarioAdminDTO dto) {
        Voluntario v;
        boolean isNovoCadastro = false;

        if (dto.getId() != null) {
            // EDIÇÃO: Busca o existente
            v = voluntarioRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Voluntário não encontrado"));
        } else {
            // CRIAÇÃO: Instancia um novo
            v = new Voluntario();
            v.setDataCriacao(LocalDateTime.now()); // Data de hoje
            v.setTermosAceitos(true); // Se o admin está cadastrando, assume-se que aceitou (ex: papel)
            isNovoCadastro = true;
        }

        v.setNomeCompleto(FormatadorTexto.padronizarNome(dto.getNomeCompleto()));
        v.setEmail(dto.getEmail().toLowerCase());
        v.setTelefone(dto.getTelefone());
        v.setDataNascimento(dto.getDataNascimento());
        v.setCpf(dto.getCpf()); // Não esqueça do CPF se adicionou no DTO
        v.setStatusTermo(dto.getStatusTermo());
        v.setAntecedentesAnalisados(dto.getAntecedentesAnalisados());
        v.setMembroIbaji(dto.isMembroIbaji());

        // Ministérios
        if (dto.getIdsMinisterios() != null) {
            v.setMinisterios(ministerioRepository.findAllByIdIn(dto.getIdsMinisterios()));
        }

        // --- NOVO: MAPEAMENTO DO MENOR ---
        v.setMenorIdade(dto.isMenorIdade());
        if (dto.isMenorIdade()) {
            v.setNomeResponsavel(dto.getNomeResponsavel());
            v.setCpfResponsavel(dto.getCpfResponsavel());
            v.setEmailResponsavel(dto.getEmailResponsavel());
            v.setTelefoneResponsavel(dto.getTelefoneResponsavel());
        }

        // Cálculos de Datas automáticas (Apenas na criação)
        if (dto.getId() == null) {
            LocalDate hoje = LocalDate.now();
            if (v.getDataTermo() == null)
                v.setDataTermo(hoje);
            if (v.getProximaRenovacao() == null)
                v.setProximaRenovacao(hoje.plusYears(1));
            if (v.getStatusTermo() == null)
                v.setStatusTermo(StatusTermo.ATIVO);
        }

        // Salva para gerar o ID (necessário para o S3)
        Voluntario salvo = voluntarioRepository.save(v);

        if (isNovoCadastro) {
            // O envio é assíncrono, não trava a tela do admin
            pdfEmailService.gerarEEnviarTermo(salvo);
        }

        // Lógica de Arquivo (Upload)
        if (dto.getArquivoAntecedentes() != null && !dto.getArquivoAntecedentes().isEmpty()) {
            String key = s3Service.enviarArquivo(dto.getArquivoAntecedentes(), salvo.getId());

            if (salvo.getAntecedentes() == null) {
                AntecedentesCriminais ant = new AntecedentesCriminais();
                ant.setVoluntario(salvo);
                ant.setStatus(StatusAntecedentes.APROVADO);
                salvo.setAntecedentes(ant);
            }
            salvo.getAntecedentes().setCaminhoArquivoS3(key);
            salvo.getAntecedentes().setNomeOriginalArquivo(dto.getArquivoAntecedentes().getOriginalFilename());

            antecedentesRepository.save(salvo.getAntecedentes());
        }
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
    public record DadoGrafico(String label, long valor, int alturaPercentual) {
    }

}