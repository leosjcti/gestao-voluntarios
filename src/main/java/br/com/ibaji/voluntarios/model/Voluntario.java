package br.com.ibaji.voluntarios.model;

import br.com.ibaji.voluntarios.model.enums.StatusTermo;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "voluntarios")
public class Voluntario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCompleto;
    private String email;
    private String telefone;
    private Boolean termosAceitos;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    // === NOVOS CAMPOS DE CONTROLE (SISTEMA) ===
    private LocalDate dataTermo;      // Data que aceitou
    private LocalDate proximaRenovacao; // Ex: +1 ano

    @Enumerated(EnumType.STRING)
    private StatusTermo statusTermo;

    @CreationTimestamp
    private LocalDateTime dataCriacao;

    private Boolean manualEntregue = false;
    private LocalDate dataIntegracao;
    private String liderIntegracao;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "voluntario_ministerios",
            joinColumns = @JoinColumn(name = "voluntario_id"),
            inverseJoinColumns = @JoinColumn(name = "ministerio_id")
    )
    private Set<Ministerio> ministerios = new HashSet<>();

    @OneToOne(mappedBy = "voluntario", cascade = CascadeType.ALL)
    private AntecedentesCriminais antecedentes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getTermosAceitos() {
        return termosAceitos;
    }

    public void setTermosAceitos(Boolean termosAceitos) {
        this.termosAceitos = termosAceitos;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Set<Ministerio> getMinisterios() {
        return ministerios;
    }

    public void setMinisterios(Set<Ministerio> ministerios) {
        this.ministerios = ministerios;
    }

    public AntecedentesCriminais getAntecedentes() {
        return antecedentes;
    }

    public void setAntecedentes(AntecedentesCriminais antecedentes) {
        this.antecedentes = antecedentes;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDate getDataTermo() {
        return dataTermo;
    }

    public void setDataTermo(LocalDate dataTermo) {
        this.dataTermo = dataTermo;
    }

    public LocalDate getProximaRenovacao() {
        return proximaRenovacao;
    }

    public void setProximaRenovacao(LocalDate proximaRenovacao) {
        this.proximaRenovacao = proximaRenovacao;
    }

    public StatusTermo getStatusTermo() {
        return statusTermo;
    }

    public void setStatusTermo(StatusTermo statusTermo) {
        this.statusTermo = statusTermo;
    }

    public Boolean getManualEntregue() {
        return manualEntregue;
    }

    public void setManualEntregue(Boolean manualEntregue) {
        this.manualEntregue = manualEntregue;
    }

    public LocalDate getDataIntegracao() {
        return dataIntegracao;
    }

    public void setDataIntegracao(LocalDate dataIntegracao) {
        this.dataIntegracao = dataIntegracao;
    }

    public String getLiderIntegracao() {
        return liderIntegracao;
    }

    public void setLiderIntegracao(String liderIntegracao) {
        this.liderIntegracao = liderIntegracao;
    }
}