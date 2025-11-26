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

    @NotNull(message = "Data de nascimento é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd") // Formato do HTML5 input date
    private LocalDate dataNascimento;

    @NotNull(message = "Selecione pelo menos um ministério")
    @Size(min = 1, max = 4, message = "Selecione entre 1 e 4 ministérios") // VALIDAÇÃO DO LIMITE
    private List<Long> idsMinisterios;

    @NotNull(message = "O aceite dos termos é obrigatório")
    @AssertTrue(message = "Você deve aceitar os termos")
    private Boolean termosAceitos;

    // Getters e Setters
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public List<Long> getIdsMinisterios() { return idsMinisterios; }
    public void setIdsMinisterios(List<Long> idsMinisterios) { this.idsMinisterios = idsMinisterios; }
    public Boolean getTermosAceitos() { return termosAceitos; }
    public void setTermosAceitos(Boolean termosAceitos) { this.termosAceitos = termosAceitos; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
}
