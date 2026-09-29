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
    public boolean criarEventoESeries(Evento evento, Espaco espacoBase, LocalDateTime dataInicioBase, LocalDateTime dataFimBase) {
        evento = eventoRepository.save(evento);
        
        LocalDateTime start = dataInicioBase;
        LocalDateTime end = dataFimBase;
        LocalDateTime maxDate = dataInicioBase.plusYears(1);
        
        String regra = evento.getRegraRecorrencia() != null ? evento.getRegraRecorrencia().toUpperCase() : "UNICO";
        boolean temConflito = false;
        
        do {
            EventoOcorrencia ocorrencia = new EventoOcorrencia();
            ocorrencia.setEvento(evento);
            ocorrencia.setEspaco(espacoBase);
            ocorrencia.setDataInicio(start);
            ocorrencia.setDataFim(end);
            
            // Verificar conflito de forma permissiva (marca como CONFLITO mas salva)
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
                    start = start.plusMonths(1);
                    end = end.plusMonths(1);
                    break;
                case "UNICO":
                default:
                    start = maxDate.plusDays(1); // Para sair do loop
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
}
