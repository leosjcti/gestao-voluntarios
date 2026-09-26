package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.dto.ResultadoAnalise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegraAntecedentesServiceTest {

    private RegraAntecedentesService service;

    @BeforeEach
    void setUp() {
        service = new RegraAntecedentesService();
    }

    @Test
    void deveAprovarQuandoNomeBateEConstaTermoRegex() {
        String textoPdf = "CERTIDÃO DE ANTECEDENTES CRIMINAIS\nNome: JOSÉ MÁRIO DA SILVA\nCertificamos que NADA CONSTA contra o solicitante.\nChave de autenticação: ABC-123.45";
        String nomeBanco = "José Mário da Silva";

        ResultadoAnalise resultado = service.analisar(textoPdf, nomeBanco);

        assertEquals("APROVADO_PRELIMINAR", resultado.status());
        assertEquals("ABC-123.45", resultado.chaveAutenticacao());
    }

    @Test
    void deveRejeitarQuandoNomeDivergente() {
        String textoPdf = "Nome: JOÃO DA SILVA\n NADA CONSTA";
        String nomeBanco = "José Mário da Silva";

        ResultadoAnalise resultado = service.analisar(textoPdf, nomeBanco);

        assertEquals("REVISAO_MANUAL", resultado.status());
        assertTrue(resultado.motivo().contains("Divergência de titularidade"));
        assertNull(resultado.chaveAutenticacao());
    }

    @Test
    void deveRejeitarQuandoFaltaTermoNadaConsta() {
        String textoPdf = "Nome: JOSÉ MÁRIO DA SILVA\nO sistema encontrou 1 registro(s).";
        String nomeBanco = "José Mário da Silva";

        ResultadoAnalise resultado = service.analisar(textoPdf, nomeBanco);

        assertEquals("REVISAO_MANUAL", resultado.status());
        assertTrue(resultado.motivo().contains("Não foi encontrada declaração"));
    }

    @Test
    void deveAprovarComVariacaoNaoConstamRegistros() {
        String textoPdf = "Nome: JOSÉ MÁRIO DA SILVA\nCertificamos que não constam registros contra o indivíduo.\nCódigo de autenticação: ZYX-987";
        String nomeBanco = "JOSÉ MÁRIO DA SILVA";

        ResultadoAnalise resultado = service.analisar(textoPdf, nomeBanco);

        assertEquals("APROVADO_PRELIMINAR", resultado.status());
        assertEquals("ZYX-987", resultado.chaveAutenticacao());
    }
}
