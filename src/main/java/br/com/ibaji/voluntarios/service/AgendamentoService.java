package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.model.Espaco;
import br.com.ibaji.voluntarios.model.Evento;
import br.com.ibaji.voluntarios.model.EventoOcorrencia;
import br.com.ibaji.voluntarios.model.enums.StatusOcorrencia;
import br.com.ibaji.voluntarios.repository.EventoOcorrenciaRepository;
import br.com.ibaji.voluntarios.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {

    private final EventoRepository eventoRepository;
    private final EventoOcorrenciaRepository eventoOcorrenciaRepository;

    public AgendamentoService(EventoRepository eventoRepository, EventoOcorrenciaRepository eventoOcorrenciaRepository) {
        this.eventoRepository = eventoRepository;
        this.eventoOcorrenciaRepository = eventoOcorrenciaRepository;
    }

    @Transactional
    public boolean criarEventoESeries(Evento evento, Espaco espacoBase, LocalDateTime dataInicioBase, LocalDateTime dataFimBase, Boolean excJan, Boolean excJul, Boolean excDez) {
        evento = eventoRepository.save(evento);
        
        LocalDateTime start = dataInicioBase;
        LocalDateTime end = dataFimBase;
        LocalDateTime maxDate = dataInicioBase.plusYears(1);
        
        String regra = evento.getRegraRecorrencia() != null ? evento.getRegraRecorrencia().toUpperCase() : "UNICO";
        boolean temConflito = false;
        
        java.time.DayOfWeek startDow = dataInicioBase.getDayOfWeek();
        int ordinalDow = (dataInicioBase.getDayOfMonth() - 1) / 7 + 1;

        do {
            boolean pular = false;
            int mesAtual = start.getMonthValue();
            if (Boolean.TRUE.equals(excJan) && mesAtual == 1) pular = true;
            if (Boolean.TRUE.equals(excJul) && mesAtual == 7) pular = true;
            if (Boolean.TRUE.equals(excDez) && mesAtual == 12) pular = true;

            if (!pular) {
                EventoOcorrencia ocorrencia = new EventoOcorrencia();
                ocorrencia.setEvento(evento);
                ocorrencia.setEspaco(espacoBase);
                ocorrencia.setDataInicio(start);
                ocorrencia.setDataFim(end);
                
                if (espacoBase != null) {
                    List<EventoOcorrencia> conflitos = eventoOcorrenciaRepository.findConflitos(espacoBase.getId(), start, end);
                    if (!conflitos.isEmpty()) {
                        ocorrencia.setStatus(StatusOcorrencia.CONFLITO);
                        temConflito = true;
                    } else {
                        ocorrencia.setStatus(StatusOcorrencia.APROVADO);
                    }
                } else {
                    ocorrencia.setStatus(StatusOcorrencia.PENDENTE);
                }
                
                eventoOcorrenciaRepository.save(ocorrencia);
            }
            
            switch (regra) {
                case "DIARIO":
                    start = start.plusDays(1);
                    end = end.plusDays(1);
                    break;
                case "SEMANAL":
                    start = start.plusWeeks(1);
                    end = end.plusWeeks(1);
                    break;
                case "MENSAL":
                    java.time.LocalDate nextMonthDate = start.toLocalDate().plusMonths(1);
                    java.time.LocalDate nextOcorrenciaDate = nextMonthDate.with(java.time.temporal.TemporalAdjusters.dayOfWeekInMonth(ordinalDow, startDow));
                    if (nextOcorrenciaDate.getMonth() != nextMonthDate.getMonth()) {
                        nextOcorrenciaDate = nextMonthDate.with(java.time.temporal.TemporalAdjusters.lastInMonth(startDow));
                    }
                    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(start.toLocalDate(), nextOcorrenciaDate);
                    start = start.plusDays(daysBetween);
                    end = end.plusDays(daysBetween);
                    break;
                case "UNICO":
                default:
                    start = maxDate.plusDays(1);
                    break;
            }
        } while (start.isBefore(maxDate));
        
        return temConflito;
    }

    @Transactional
    public void excluirOcorrencia(Long id) {
        eventoOcorrenciaRepository.deleteById(id);
    }

    @Transactional
    public void excluirEventoTotal(Long eventoId) {
        List<EventoOcorrencia> ocorrencias = eventoOcorrenciaRepository.findByEventoId(eventoId);
        eventoOcorrenciaRepository.deleteAll(ocorrencias);
        eventoRepository.deleteById(eventoId);
    }

    public Evento buscarEventoPorId(Long eventoId) {
        return eventoRepository.findById(eventoId).orElse(null);
    }
}
