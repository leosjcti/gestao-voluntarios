package br.com.ibaji.voluntarios.model.dto;

import br.com.ibaji.voluntarios.model.enums.StatusTermo;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public class VoluntarioAdminDTO {

    private Long id;
    private String nomeCompleto;
    private String email;
    private String telefone;
    private String cpf;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    private StatusTermo statusTermo;

    private List<Long> idsMinisterios;

    private MultipartFile arquivoAntecedentes;

    private Boolean antecedentesAnalisados;

    // --- MENOR DE IDADE ---
    private boolean menorIdade;
    private String nomeResponsavel;
    private String cpfResponsavel;
    private String emailResponsavel;
    private String telefoneResponsavel;

    private boolean membroIbaji;

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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public StatusTermo getStatusTermo() {
        return statusTermo;
    }

    public void setStatusTermo(StatusTermo statusTermo) {
        this.statusTermo = statusTermo;
    }

    public List<Long> getIdsMinisterios() {
        return idsMinisterios;
    }

    public void setIdsMinisterios(List<Long> idsMinisterios) {
        this.idsMinisterios = idsMinisterios;
    }

    public MultipartFile getArquivoAntecedentes() {
        return arquivoAntecedentes;
    }

    public void setArquivoAntecedentes(MultipartFile arquivoAntecedentes) {
        this.arquivoAntecedentes = arquivoAntecedentes;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Boolean getAntecedentesAnalisados() {
        return antecedentesAnalisados;
    }

    public void setAntecedentesAnalisados(Boolean antecedentesAnalisados) {
        this.antecedentesAnalisados = antecedentesAnalisados;
    }

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