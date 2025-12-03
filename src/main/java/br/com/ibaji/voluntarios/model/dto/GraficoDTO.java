package br.com.ibaji.voluntarios.model.dto;

public class GraficoDTO {
    private String label;
    private long valor;
    private int percentual;

    public GraficoDTO(String label, long valor, int percentual) {
        this.label = label;
        this.valor = valor;
        this.percentual = percentual;
    }

    // Getters
    public String getLabel() { return label; }
    public long getValor() { return valor; }
    public int getPercentual() { return percentual; }
}