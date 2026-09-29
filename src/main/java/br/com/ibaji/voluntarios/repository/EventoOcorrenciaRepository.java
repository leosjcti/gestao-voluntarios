package br.com.ibaji.voluntarios.repository;

import br.com.ibaji.voluntarios.model.EventoOcorrencia;
import br.com.ibaji.voluntarios.model.enums.StatusOcorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoOcorrenciaRepository extends JpaRepository<EventoOcorrencia, Long> {

    @Query("SELECT o FROM EventoOcorrencia o WHERE o.espaco.id = :espacoId " +
           "AND o.status <> br.com.ibaji.voluntarios.model.enums.StatusOcorrencia.CANCELADO " +
           "AND o.dataInicio < :dataFim AND o.dataFim > :dataInicio")
    List<EventoOcorrencia> findConflitos(@Param("espacoId") Long espacoId, 
                                         @Param("dataInicio") LocalDateTime dataInicio, 
                                         @Param("dataFim") LocalDateTime dataFim);
                                         
    List<EventoOcorrencia> findByStatus(StatusOcorrencia status);
    
    List<EventoOcorrencia> findByEventoId(Long eventoId);
}
