package br.com.ibaji.voluntarios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ministerios")
public class Ministerio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private String lider;
    private String pastorResponsavel;
    private String contatoLider;
    private String base;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getLider() { return lider; }
    public void setLider(String lider) { this.lider = lider; }
    public String getPastorResponsavel() { return pastorResponsavel; }
    public void setPastorResponsavel(String pastorResponsavel) { this.pastorResponsavel = pastorResponsavel; }
    public String getContatoLider() { return contatoLider; }
    public void setContatoLider(String contatoLider) { this.contatoLider = contatoLider; }
    public String getBase() { return base; }
    public void setBase(String base) { this.base = base; }
}
