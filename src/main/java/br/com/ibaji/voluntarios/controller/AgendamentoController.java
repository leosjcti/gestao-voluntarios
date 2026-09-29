package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.dto.AgendamentoFormDTO;
import br.com.ibaji.voluntarios.model.Espaco;
import br.com.ibaji.voluntarios.model.Evento;
import br.com.ibaji.voluntarios.model.EventoOcorrencia;
import br.com.ibaji.voluntarios.model.Ministerio;
import br.com.ibaji.voluntarios.model.enums.StatusOcorrencia;
import br.com.ibaji.voluntarios.repository.EspacoRepository;
import br.com.ibaji.voluntarios.repository.EventoOcorrenciaRepository;
import br.com.ibaji.voluntarios.repository.MinisterioRepository;
import br.com.ibaji.voluntarios.service.AgendamentoService;
import br.com.ibaji.voluntarios.service.GCalExportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final GCalExportService gCalExportService;
    private final EspacoRepository espacoRepository;
    private final MinisterioRepository ministerioRepository;
    private final EventoOcorrenciaRepository eventoOcorrenciaRepository;

    public AgendamentoController(AgendamentoService agendamentoService,
                                 GCalExportService gCalExportService,
                                 EspacoRepository espacoRepository,
                                 MinisterioRepository ministerioRepository,
                                 EventoOcorrenciaRepository eventoOcorrenciaRepository) {
        this.agendamentoService = agendamentoService;
        this.gCalExportService = gCalExportService;
        this.espacoRepository = espacoRepository;
        this.ministerioRepository = ministerioRepository;
        this.eventoOcorrenciaRepository = eventoOcorrenciaRepository;
    }

    @GetMapping("/calendario")
    public String calendario() {
        return "agendamentos-calendario";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("dto")) {
            model.addAttribute("dto", new AgendamentoFormDTO());
        }
        model.addAttribute("espacos", espacoRepository.findAll());
        model.addAttribute("ministerios", ministerioRepository.findAll());
        return "agendamentos-form";
    }

    @PostMapping("/novo")
    public String salvarNovo(@ModelAttribute AgendamentoFormDTO dto, RedirectAttributes redirectAttributes) {
        Evento evento = new Evento();
        evento.setTitulo(dto.getTitulo());
        evento.setDescricao(dto.getDescricao());
        evento.setRecorrente(dto.getIsRecorrente());
        evento.setRegraRecorrencia(dto.getRegraRecorrencia());
        evento.setApoioNecessario(dto.getApoioNecessario());
        evento.setEstimativaParticipantes(dto.getEstimativaParticipantes());

        if (dto.getMinisterioId() != null) {
            Ministerio ministerio = ministerioRepository.findById(dto.getMinisterioId()).orElse(null);
            evento.setMinisterio(ministerio);
        }

        Espaco espaco = null;
        if (dto.getEspacoId() != null) {
            espaco = espacoRepository.findById(dto.getEspacoId()).orElse(null);
        }

        boolean temConflito = agendamentoService.criarEventoESeries(evento, espaco, dto.getDataInicioBase(), dto.getDataFimBase());
        
        if (temConflito) {
            redirectAttributes.addFlashAttribute("erro", "Agendamento criado, MAS COM CONFLITOS! Existem eventos sobrepostos que precisarão ser avaliados pela gerência.");
        } else {
            redirectAttributes.addFlashAttribute("mensagem", "Agendamento criado com sucesso!");
        }
        
        return "redirect:/admin/agendamentos/calendario";
    }

    @GetMapping("/conflitos")
    public String conflitos(Model model) {
        List<EventoOcorrencia> conflitos = eventoOcorrenciaRepository.findByStatus(StatusOcorrencia.CONFLITO);
        model.addAttribute("conflitos", conflitos);
        model.addAttribute("espacos", espacoRepository.findAll());
        return "agendamentos-conflitos";
    }

    @PostMapping("/resolver-conflito/{id}")
    public String resolverConflito(@PathVariable Long id, 
                                   @RequestParam String acao, 
                                   @RequestParam(required = false) Long novoEspacoId,
                                   RedirectAttributes redirectAttributes) {
        EventoOcorrencia ocorrencia = eventoOcorrenciaRepository.findById(id).orElse(null);
        if (ocorrencia != null) {
            if ("APROVAR".equalsIgnoreCase(acao)) {
                ocorrencia.setStatus(StatusOcorrencia.APROVADO);
                if (novoEspacoId != null) {
                    Espaco espaco = espacoRepository.findById(novoEspacoId).orElse(null);
                    ocorrencia.setEspaco(espaco);
                }
                eventoOcorrenciaRepository.save(ocorrencia);
                redirectAttributes.addFlashAttribute("mensagem", "Conflito resolvido e agendamento aprovado.");
            } else if ("CANCELAR".equalsIgnoreCase(acao)) {
                ocorrencia.setStatus(StatusOcorrencia.CANCELADO);
                eventoOcorrenciaRepository.save(ocorrencia);
                redirectAttributes.addFlashAttribute("mensagem", "Ocorrência cancelada.");
            }
        }
        return "redirect:/admin/agendamentos/conflitos";
    }

    @PostMapping("/ocorrencia/{id}/excluir")
    public String excluirOcorrencia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        agendamentoService.excluirOcorrencia(id);
        redirectAttributes.addFlashAttribute("mensagem", "Ocorrência excluída com sucesso.");
        return "redirect:/admin/agendamentos/calendario";
    }

    @PostMapping("/evento/{eventoId}/excluir")
    public String excluirEventoTotal(@PathVariable Long eventoId, RedirectAttributes redirectAttributes) {
        agendamentoService.excluirEventoTotal(eventoId);
        redirectAttributes.addFlashAttribute("mensagem", "Série de eventos excluída com sucesso.");
        return "redirect:/admin/agendamentos/calendario";
    }

    @GetMapping(value = "/exportar.ics", produces = "text/calendar")
    @ResponseBody
    public String exportarIcs() {
        return gCalExportService.exportarCalendario();
    }
}
