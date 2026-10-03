package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.dto.ResultadoAnalise;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegraAntecedentesService {

    private static final Pattern NADA_CONSTA_PATTERN = Pattern.compile("(?i)(nada consta|n[aã]o constam registros|n[aã]o consta)");
    private static final Pattern CHAVE_PATTERN = Pattern.compile("(?i)(?:chave|c[oó]digo) de autentica[cç][aã]o:\\s*([A-Z0-9.-]+)");

    public ResultadoAnalise analisar(String textoPdf, String nomeVoluntario) {
        String chave = null;
        Matcher chaveMatcher = CHAVE_PATTERN.matcher(textoPdf); // usa texto original
        if (chaveMatcher.find()) {
            chave = chaveMatcher.group(1);
        }

        String textoNormalizado = normalizar(textoPdf);
        String nomeNormalizado = normalizar(nomeVoluntario);

        if (!textoNormalizado.contains(nomeNormalizado)) {
            return new ResultadoAnalise("REVISAO_MANUAL", "Divergência de titularidade: Nome do voluntário não encontrado no documento.", null);
        }

        Matcher antecedenteMatcher = NADA_CONSTA_PATTERN.matcher(textoNormalizado);
        if (!antecedenteMatcher.find()) {
            return new ResultadoAnalise("REVISAO_MANUAL", "Não foi encontrada declaração de 'nada consta'.", null);
        }

        return new ResultadoAnalise("APROVADO_PRELIMINAR", "Documento validado com sucesso.", chave);
    }

    private String normalizar(String input) {
        if (input == null) return "";
        String semAcento = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toUpperCase().replaceAll("\\s+", " ").trim();
    }
}
