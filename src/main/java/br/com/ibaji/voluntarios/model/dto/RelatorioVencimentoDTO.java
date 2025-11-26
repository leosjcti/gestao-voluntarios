package br.com.ibaji.voluntarios.model.dto;

import java.time.LocalDate;

public class RelatorioVencimentoDTO {
    private String nomeVoluntario;
    private String nomeMinisterio; // Pega o primeiro ou lista todos
    private LocalDate dataVencimento;
    private long diasRestantes;

    public RelatorioVencimentoDTO(String nomeVoluntario, String nomeMinisterio, LocalDate dataVencimento, long diasRestantes) {
        this.nomeVoluntario = nomeVoluntario;
        this.nomeMinisterio = nomeMinisterio;
        this.dataVencimento = dataVencimento;
        this.diasRestantes = diasRestantes;
    }

    // Getters
    public String getNomeVoluntario() { return nomeVoluntario; }
    public String getNomeMinisterio() { return nomeMinisterio; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public long getDiasRestantes() { return diasRestantes; }
}