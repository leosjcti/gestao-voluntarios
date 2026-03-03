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
    private String cpf;
    private Boolean termosAceitos;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    private LocalDate dataTermo;      // Data que aceitou
    private LocalDate proximaRenovacao; // Ex: +1 ano

    @Enumerated(EnumType.STRING)
    private StatusTermo statusTermo;

    @CreationTimestamp
    private LocalDateTime dataCriacao;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "voluntario_ministerios",
            joinColumns = @JoinColumn(name = "voluntario_id"),
            inverseJoinColumns = @JoinColumn(name = "ministerio_id")
    )
    private Set<Ministerio> ministerios = new HashSet<>();

    @OneToOne(mappedBy = "voluntario", cascade = CascadeType.ALL)
    private AntecedentesCriminais antecedentes;

    private Boolean antecedentesAnalisados = false;


    // --- MENOR DE IDADE ---
    private boolean menorIdade;
    private String nomeResponsavel;
    private String cpfResponsavel;
    private String emailResponsavel;
    private String telefoneResponsavel;


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

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

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


    public Boolean getAntecedentesAnalisados() { return antecedentesAnalisados; }
    public void setAntecedentesAnalisados(Boolean antecedentesAnalisados) { this.antecedentesAnalisados = antecedentesAnalisados; }

    // Menor de idade
    public boolean isMenorIdade() { return menorIdade; }
    public void setMenorIdade(boolean menorIdade) { this.menorIdade = menorIdade; }
    public String getNomeResponsavel() { return nomeResponsavel; }
    public void setNomeResponsavel(String nomeResponsavel) { this.nomeResponsavel = nomeResponsavel; }
    public String getCpfResponsavel() { return cpfResponsavel; }
    public void setCpfResponsavel(String cpfResponsavel) { this.cpfResponsavel = cpfResponsavel; }
    public String getEmailResponsavel() { return emailResponsavel; }
    public void setEmailResponsavel(String emailResponsavel) { this.emailResponsavel = emailResponsavel; }
    public String getTelefoneResponsavel() { return telefoneResponsavel; }
    public void setTelefoneResponsavel(String telefoneResponsavel) { this.telefoneResponsavel = telefoneResponsavel; }
}