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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    // Controle
    private Boolean manualEntregue;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataIntegracao;

    private String liderIntegracao;

    private StatusTermo statusTermo;

    private List<Long> idsMinisterios;

    // Arquivo (Opcional para o admin)
    private MultipartFile arquivoAntecedentes;

    // Getters e Setters (Gere todos)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public Boolean getManualEntregue() { return manualEntregue; }
    public void setManualEntregue(Boolean manualEntregue) { this.manualEntregue = manualEntregue; }
    public LocalDate getDataIntegracao() { return dataIntegracao; }
    public void setDataIntegracao(LocalDate dataIntegracao) { this.dataIntegracao = dataIntegracao; }
    public String getLiderIntegracao() { return liderIntegracao; }
    public void setLiderIntegracao(String liderIntegracao) { this.liderIntegracao = liderIntegracao; }
    public StatusTermo getStatusTermo() { return statusTermo; }
    public void setStatusTermo(StatusTermo statusTermo) { this.statusTermo = statusTermo; }
    public List<Long> getIdsMinisterios() { return idsMinisterios; }
    public void setIdsMinisterios(List<Long> idsMinisterios) { this.idsMinisterios = idsMinisterios; }
    public MultipartFile getArquivoAntecedentes() { return arquivoAntecedentes; }
    public void setArquivoAntecedentes(MultipartFile arquivoAntecedentes) { this.arquivoAntecedentes = arquivoAntecedentes; }
}