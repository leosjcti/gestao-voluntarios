package br.com.ibaji.voluntarios.repository;

import br.com.ibaji.voluntarios.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
}
