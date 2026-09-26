package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.model.Voluntario;
import br.com.ibaji.voluntarios.dto.ResultadoAnalise;
import br.com.ibaji.voluntarios.repository.VoluntarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentAnalysisServiceTest {

    @Mock
    private DocumentoExtractorService extractorService;

    @Mock
    private RegraAntecedentesService regraService;

    @Mock
    private VoluntarioRepository repository;

    @Mock
    private ErroAnaliseService erroAnaliseService;

    @Mock
    private StatusAntecedentesService statusService;

    private DocumentAnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new DocumentAnalysisService(extractorService, regraService, repository, erroAnaliseService, statusService);
    }

    @Test
    void deveProcessarEAtualizarStatusComSucesso() {
        Long voluntarioId = 3L;
        File fakeFile = new File("fake.pdf");

        String textoExtraido = "Certidão de antecedentes: NADA CONSTA. Chave: 12345";
        ResultadoAnalise resultadoMock = new ResultadoAnalise("APROVADO_PRELIMINAR", "Sucesso", "12345");

        when(repository.findNomeById(voluntarioId)).thenReturn(Optional.of("João Silva"));
        when(extractorService.extrairTexto(any())).thenReturn(textoExtraido);
        when(regraService.analisar(textoExtraido, "João Silva")).thenReturn(resultadoMock);

        analysisService.processarDocumentoAssincrono(voluntarioId, fakeFile);

        verify(erroAnaliseService, never()).registrarErro(anyLong(), anyString());
        verify(repository, times(1)).findNomeById(voluntarioId);
        verify(statusService).registrarSucesso(voluntarioId, "APROVADO_PRELIMINAR", "12345");
    }

    @Test
    void deveAcionarErroAnaliseQuandoPdfCorrompidoOuFalhaGeral() {
        Long voluntarioId = 1L;
        File fakeFile = new File("fake.pdf");

        when(repository.findNomeById(voluntarioId)).thenReturn(Optional.of("João Silva"));
        when(extractorService.extrairTexto(any())).thenThrow(new RuntimeException("PDF Corrompido"));

        analysisService.processarDocumentoAssincrono(voluntarioId, fakeFile);

        verify(erroAnaliseService).registrarErro(voluntarioId, "ERRO_ANALISE");
        verify(statusService, never()).registrarSucesso(any(), any(), any());
    }

    @Test
    void deveAcionarErroAnaliseQuandoExtratorRetornaVazio() {
        Long voluntarioId = 2L;
        File fakeFile = new File("fake.pdf");

        when(repository.findNomeById(voluntarioId)).thenReturn(Optional.of("João Silva"));
        when(extractorService.extrairTexto(any())).thenReturn("   "); // Retorna texto vazio

        analysisService.processarDocumentoAssincrono(voluntarioId, fakeFile);

        verify(erroAnaliseService).registrarErro(voluntarioId, "ERRO_ANALISE");
        verify(regraService, never()).analisar(any(), any());
        verify(statusService, never()).registrarSucesso(any(), any(), any());
    }

    @Test
    void deveAcionarErroAnaliseQuandoFalharAoSalvarStatusDeSucesso() {
        Long voluntarioId = 4L;
        File fakeFile = new File("fake.pdf");

        when(repository.findNomeById(voluntarioId)).thenReturn(Optional.of("João"));
        when(extractorService.extrairTexto(any())).thenReturn("Texto Válido");
        when(regraService.analisar(anyString(), anyString()))
            .thenReturn(new ResultadoAnalise("APROVADO_PRELIMINAR", "Ok", "123"));
        
        // Simula uma queda de banco no momento exato do commit
        doThrow(new RuntimeException("DB Offline")).when(statusService).registrarSucesso(anyLong(), anyString(), anyString());

        analysisService.processarDocumentoAssincrono(voluntarioId, fakeFile);

        // O catch master deve segurar a bomba e garantir o log de erro
        verify(erroAnaliseService).registrarErro(voluntarioId, "ERRO_ANALISE");
    }

    @Test
    void deveAbortarEGravarErroDeDadosQuandoVoluntarioNaoExistirOuNomeForNulo() {
        Long voluntarioId = 99L;
        File fakeFile = new File("fake.pdf");

        when(repository.findNomeById(voluntarioId)).thenReturn(Optional.empty());

        analysisService.processarDocumentoAssincrono(voluntarioId, fakeFile);

        verify(extractorService, never()).extrairTexto(any());
        verify(erroAnaliseService).registrarErro(voluntarioId, "ERRO_DADOS_INVALIDOS");
    }
}
