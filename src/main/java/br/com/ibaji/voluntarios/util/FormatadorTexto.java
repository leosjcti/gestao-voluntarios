package br.com.ibaji.voluntarios.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FormatadorTexto {

    private static final List<String> PREPOSICOES = Arrays.asList("da", "de", "do", "das", "dos", "e");

    public static String padronizarNome(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        String[] palavras = texto.trim().toLowerCase().split("\\s+");

        return Arrays.stream(palavras)
                .map(palavra -> {
                    if (PREPOSICOES.contains(palavra)) {
                        return palavra; // Mantém minúsculo (ex: "da")
                    }
                    if (palavra.length() > 1) {
                        return Character.toUpperCase(palavra.charAt(0)) + palavra.substring(1);
                    }
                    return palavra.toUpperCase(); // Letras isoladas (ex: D. Pedro)
                })
                .collect(Collectors.joining(" "));
    }
}
