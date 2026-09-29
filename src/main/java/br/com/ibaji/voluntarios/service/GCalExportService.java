package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.model.EventoOcorrencia;
import br.com.ibaji.voluntarios.model.enums.StatusOcorrencia;
import br.com.ibaji.voluntarios.repository.EventoOcorrenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GCalExportService {

    private final EventoOcorrenciaRepository eventoOcorrenciaRepository;

    public GCalExportService(EventoOcorrenciaRepository eventoOcorrenciaRepository) {
        this.eventoOcorrenciaRepository = eventoOcorrenciaRepository;
    }

    @Transactional(readOnly = true)
    public String exportarCalendario() {
        List<EventoOcorrencia> ocorrencias = eventoOcorrenciaRepository.findByStatus(StatusOcorrencia.APROVADO);
        
        StringBuilder ical = new StringBuilder();
        ical.append("BEGIN:VCALENDAR\n");
        ical.append("VERSION:2.0\n");
        ical.append("PRODID:-//Ibaji//Voluntarios//PT\n");
        ical.append("CALSCALE:GREGORIAN\n");
        ical.append("METHOD:PUBLISH\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        ZoneId zone = ZoneId.systemDefault();

        for (EventoOcorrencia o : ocorrencias) {
            ical.append("BEGIN:VEVENT\n");
            ical.append("UID:").append(o.getId()).append("@ibaji.org\n");
            
            // Formatando em UTC para o iCal
            String dtStart = o.getDataInicio().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).format(formatter);
            String dtEnd = o.getDataFim().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).format(formatter);
            
            ical.append("DTSTART:").append(dtStart).append("\n");
            ical.append("DTEND:").append(dtEnd).append("\n");
            ical.append("SUMMARY:").append(o.getEvento().getTitulo()).append("\n");
            
            if (o.getEspaco() != null) {
                ical.append("LOCATION:").append(o.getEspaco().getNome()).append("\n");
            }
            if (o.getEvento().getDescricao() != null) {
                ical.append("DESCRIPTION:").append(o.getEvento().getDescricao().replace("\n", "\\n")).append("\n");
            }
            
            ical.append("STATUS:CONFIRMED\n");
            ical.append("END:VEVENT\n");
        }

        ical.append("END:VCALENDAR\n");
        return ical.toString();
    }
}
