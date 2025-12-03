package br.com.ibaji.voluntarios.model.dto;

import java.io.InputStream;

public class ArquivoDTO {
    private String nome;
    private InputStream stream;

    public ArquivoDTO(String nome, InputStream stream) {
        this.nome = nome;
        this.stream = stream;
    }

    public String getNome() { return nome; }
    public InputStream getStream() { return stream; }
}