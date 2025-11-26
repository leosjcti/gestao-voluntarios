package br.com.ibaji.voluntarios.model;

import br.com.ibaji.voluntarios.model.enums.StatusAntecedentes;
import jakarta.persistence.*;

@Entity
@Table(name = "antecedentes_criminais")
public class AntecedentesCriminais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "voluntario_id", nullable = false)
    private Voluntario voluntario;

    @Enumerated(EnumType.STRING)
    private StatusAntecedentes status;

    private String caminhoArquivoS3;
    private String nomeOriginalArquivo;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Voluntario getVoluntario() { return voluntario; }
    public void setVoluntario(Voluntario voluntario) { this.voluntario = voluntario; }
    public StatusAntecedentes getStatus() { return status; }
    public void setStatus(StatusAntecedentes status) { this.status = status; }
    public String getCaminhoArquivoS3() { return caminhoArquivoS3; }
    public void setCaminhoArquivoS3(String caminhoArquivoS3) { this.caminhoArquivoS3 = caminhoArquivoS3; }
    public String getNomeOriginalArquivo() { return nomeOriginalArquivo; }
    public void setNomeOriginalArquivo(String nomeOriginalArquivo) { this.nomeOriginalArquivo = nomeOriginalArquivo; }
}