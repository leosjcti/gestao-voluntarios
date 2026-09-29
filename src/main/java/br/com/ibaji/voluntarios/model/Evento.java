package br.com.ibaji.voluntarios.model;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministerio_id")
    private Ministerio ministerio;

    @Column(name = "is_recorrente")
    private Boolean isRecorrente = false;

    @Column(name = "regra_recorrencia")
    private String regraRecorrencia;

    @Column(name = "apoio_necessario", columnDefinition = "TEXT")
    private String apoioNecessario;

    @Column(name = "estimativa_participantes")
    private Integer estimativaParticipantes;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoOcorrencia> ocorrencias = new ArrayList<>();

    public Evento() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Ministerio getMinisterio() {
        return ministerio;
    }

    public void setMinisterio(Ministerio ministerio) {
        this.ministerio = ministerio;
    }

    public Boolean getRecorrente() {
        return isRecorrente;
    }

    public void setRecorrente(Boolean recorrente) {
        isRecorrente = recorrente;
    }

    public String getRegraRecorrencia() {
        return regraRecorrencia;
    }

    public void setRegraRecorrencia(String regraRecorrencia) {
        this.regraRecorrencia = regraRecorrencia;
    }

    public String getApoioNecessario() {
        return apoioNecessario;
    }

    public void setApoioNecessario(String apoioNecessario) {
        this.apoioNecessario = apoioNecessario;
    }

    public Integer getEstimativaParticipantes() {
        return estimativaParticipantes;
    }

    public void setEstimativaParticipantes(Integer estimativaParticipantes) {
        this.estimativaParticipantes = estimativaParticipantes;
    }

    public List<EventoOcorrencia> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(List<EventoOcorrencia> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return Objects.equals(id, evento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
