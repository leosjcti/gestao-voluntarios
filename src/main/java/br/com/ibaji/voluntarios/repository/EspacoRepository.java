package br.com.ibaji.voluntarios.repository;

import br.com.ibaji.voluntarios.model.Espaco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspacoRepository extends JpaRepository<Espaco, Long> {
}
