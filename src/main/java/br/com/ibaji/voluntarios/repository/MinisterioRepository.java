package br.com.ibaji.voluntarios.repository;

import br.com.ibaji.voluntarios.model.Ministerio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface MinisterioRepository extends JpaRepository<Ministerio, Long> {
    Set<Ministerio> findAllByIdIn(List<Long> ids);

    Page<Ministerio> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}