package br.com.ibaji.voluntarios.model.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class VoluntarioFormDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nomeCompleto;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotNull(message = "Data de nascimento é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd") // Formato do HTML5 input date
    private LocalDate dataNascimento;

    @NotNull(message = "Selecione pelo menos um ministério")
    private List<Long> idsMinisterios;

    @NotNull(message = "O aceite dos termos é obrigatório")
    @AssertTrue(message = "Você deve aceitar os termos")
    private Boolean termosAceitos;

    // --- MENOR DE IDADE ---
    private boolean menorIdade;
    private String nomeResponsavel;
    private String cpfResponsavel;
    private String emailResponsavel;
    private String telefoneResponsavel;

    private boolean membroIbaji;

    // Getters e Setters
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public List<Long> getIdsMinisterios() {
        return idsMinisterios;
    }

    public void setIdsMinisterios(List<Long> idsMinisterios) {
        this.idsMinisterios = idsMinisterios;
    }

    public Boolean getTermosAceitos() {
        return termosAceitos;
    }

    public void setTermosAceitos(Boolean termosAceitos) {
        this.termosAceitos = termosAceitos;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    // Getters e Setters
    public boolean isMenorIdade() {
        return menorIdade;
    }

    public void setMenorIdade(boolean menorIdade) {
        this.menorIdade = menorIdade;
    }

    public boolean isMembroIbaji() {
        return membroIbaji;
    }

    public void setMembroIbaji(boolean membroIbaji) {
        this.membroIbaji = membroIbaji;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public String getCpfResponsavel() {
        return cpfResponsavel;
    }

    public void setCpfResponsavel(String cpfResponsavel) {
        this.cpfResponsavel = cpfResponsavel;
    }

    public String getEmailResponsavel() {
        return emailResponsavel;
    }

    public void setEmailResponsavel(String emailResponsavel) {
        this.emailResponsavel = emailResponsavel;
    }

    public String getTelefoneResponsavel() {
        return telefoneResponsavel;
    }

    public void setTelefoneResponsavel(String telefoneResponsavel) {
        this.telefoneResponsavel = telefoneResponsavel;
    }
}
