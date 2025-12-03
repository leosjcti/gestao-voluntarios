package br.com.ibaji.voluntarios.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "bases")
public class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String lider; // Campo solicitado

    // EAGER para carregar os ministérios junto e facilitar o Thymeleaf
    @OneToMany(mappedBy = "base", fetch = FetchType.EAGER)
    private List<Ministerio> ministerios;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getLider() { return lider; }
    public void setLider(String lider) { this.lider = lider; }
    public List<Ministerio> getMinisterios() { return ministerios; }
    public void setMinisterios(List<Ministerio> ministerios) { this.ministerios = ministerios; }
}
