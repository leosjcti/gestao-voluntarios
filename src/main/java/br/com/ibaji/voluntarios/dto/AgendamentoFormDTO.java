package br.com.ibaji.voluntarios.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

public class AgendamentoFormDTO {
    private String titulo;
    private String descricao;
    private Long ministerioId;
    private Long espacoId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataInicioBase;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataFimBase;

    private Boolean isRecorrente;
    private String regraRecorrencia;
    private String apoioNecessario;
    private Integer estimativaParticipantes;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Long getMinisterioId() { return ministerioId; }
    public void setMinisterioId(Long ministerioId) { this.ministerioId = ministerioId; }

    public Long getEspacoId() { return espacoId; }
    public void setEspacoId(Long espacoId) { this.espacoId = espacoId; }

    public LocalDateTime getDataInicioBase() { return dataInicioBase; }
    public void setDataInicioBase(LocalDateTime dataInicioBase) { this.dataInicioBase = dataInicioBase; }

    public LocalDateTime getDataFimBase() { return dataFimBase; }
    public void setDataFimBase(LocalDateTime dataFimBase) { this.dataFimBase = dataFimBase; }

    public Boolean getIsRecorrente() { return isRecorrente; }
    public void setIsRecorrente(Boolean isRecorrente) { this.isRecorrente = isRecorrente; }

    public String getRegraRecorrencia() { return regraRecorrencia; }
    public void setRegraRecorrencia(String regraRecorrencia) { this.regraRecorrencia = regraRecorrencia; }

    public String getApoioNecessario() { return apoioNecessario; }
    public void setApoioNecessario(String apoioNecessario) { this.apoioNecessario = apoioNecessario; }

    public Integer getEstimativaParticipantes() { return estimativaParticipantes; }
    public void setEstimativaParticipantes(Integer estimativaParticipantes) { this.estimativaParticipantes = estimativaParticipantes; }
}
