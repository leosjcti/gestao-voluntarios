package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.EventoOcorrencia;
import br.com.ibaji.voluntarios.model.enums.StatusOcorrencia;
import br.com.ibaji.voluntarios.repository.EventoOcorrenciaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoApiController {

    private final EventoOcorrenciaRepository eventoOcorrenciaRepository;

    public AgendamentoApiController(EventoOcorrenciaRepository eventoOcorrenciaRepository) {
        this.eventoOcorrenciaRepository = eventoOcorrenciaRepository;
    }

    @GetMapping("/ocorrencias")
    public List<Map<String, Object>> listarOcorrencias() {
        List<EventoOcorrencia> ocorrencias = eventoOcorrenciaRepository.findAll();
        List<Map<String, Object>> eventosJson = new ArrayList<>();

        for (EventoOcorrencia o : ocorrencias) {
            if (o.getStatus() == StatusOcorrencia.CANCELADO) {
                continue;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("id", o.getId()); map.put("eventoId", o.getEvento().getId());
            map.put("title", o.getEvento().getTitulo() + (o.getEspaco() != null ? " (" + o.getEspaco().getNome() + ")" : ""));
            map.put("start", o.getDataInicio().toString());
            map.put("end", o.getDataFim().toString());

            String color = "#3788d8"; // Default blue
            if (o.getStatus() == StatusOcorrencia.CONFLITO) {
                color = "#dc3545"; // Red
            } else if (o.getStatus() == StatusOcorrencia.APROVADO) {
                color = "#28a745"; // Green
            } else if (o.getStatus() == StatusOcorrencia.PENDENTE) {
                color = "#ffc107"; // Yellow
            }

            map.put("color", color);
            eventosJson.add(map);
        }

        return eventosJson;
    }
}
