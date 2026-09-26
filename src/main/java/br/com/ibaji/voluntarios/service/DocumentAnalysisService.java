package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.dto.ResultadoAnalise;
import br.com.ibaji.voluntarios.repository.VoluntarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;

@Service
public class DocumentAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(DocumentAnalysisService.class);

    private final DocumentoExtractorService extractorService;
    private final RegraAntecedentesService regraService;
    private final VoluntarioRepository repository;
    private final ErroAnaliseService erroAnaliseService;
    private final StatusAntecedentesService statusService;

    public DocumentAnalysisService(DocumentoExtractorService extractorService, 
                                   RegraAntecedentesService regraService, 
                                   VoluntarioRepository repository,
                                   ErroAnaliseService erroAnaliseService,
                                   StatusAntecedentesService statusService) {
        this.extractorService = extractorService;
        this.regraService = regraService;
        this.repository = repository;
        this.erroAnaliseService = erroAnaliseService;
        this.statusService = statusService;
    }

    @Async("documentAnalysisExecutor")
    public void processarDocumentoAssincrono(Long voluntarioId, File arquivoTemporario) {
        try {
            // Usa a query leve para economizar RAM
            String nomeVoluntario = repository.findNomeById(voluntarioId).orElse(null);
            
            if (nomeVoluntario == null || nomeVoluntario.trim().isEmpty()) {
                log.error("Voluntário ID {} não encontrado ou com nome inválido. Abortando.", voluntarioId);
                erroAnaliseService.registrarErro(voluntarioId, "ERRO_DADOS_INVALIDOS");
                return;
            }

            String texto = extractorService.extrairTexto(arquivoTemporario); 
            if (texto == null || texto.trim().isEmpty()) {
                log.warn("Extrator retornou texto vazio para voluntario {}. Abortando.", voluntarioId);
                erroAnaliseService.registrarErro(voluntarioId, "ERRO_ANALISE");
                return;
            }

            ResultadoAnalise resultado = regraService.analisar(texto, nomeVoluntario);
            statusService.registrarSucesso(voluntarioId, resultado.status(), resultado.chaveAutenticacao());

        } catch (Exception e) {
            log.error("Erro no processamento do documento do voluntário {}", voluntarioId, e);
            erroAnaliseService.registrarErro(voluntarioId, "ERRO_ANALISE");
        } finally {
            if (arquivoTemporario != null) {
                try {
                    Files.deleteIfExists(arquivoTemporario.toPath());
                } catch (java.io.IOException e) {
                    log.error("Storage Leak: Falha ao deletar arquivo temporário {}", arquivoTemporario.getAbsolutePath(), e);
                }
            }
        }
    }
}
