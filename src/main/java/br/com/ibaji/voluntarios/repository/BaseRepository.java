package br.com.ibaji.voluntarios.repository;

import br.com.ibaji.voluntarios.model.Base;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseRepository extends JpaRepository<Base, Long> {
}
