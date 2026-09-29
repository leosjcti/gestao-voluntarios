package br.com.ibaji.voluntarios.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "espacos")
public class Espaco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private Integer capacidade;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    public Espaco() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Espaco espaco = (Espaco) o;
        return Objects.equals(id, espaco.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
