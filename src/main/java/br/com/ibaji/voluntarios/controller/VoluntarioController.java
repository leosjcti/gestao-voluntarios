package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.dto.VoluntarioFormDTO;
import br.com.ibaji.voluntarios.repository.BaseRepository;
import br.com.ibaji.voluntarios.service.VoluntarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VoluntarioController {

    private final VoluntarioService servico;
    private final BaseRepository baseRepository;

    public VoluntarioController(VoluntarioService servico, BaseRepository baseRepository) {
        this.servico = servico;
        this.baseRepository = baseRepository;
    }

    @GetMapping("/cadastro")
    public String exibirFormulario(Model modelo, HttpServletRequest request) {

        request.getSession(true);

        modelo.addAttribute("formDto", new VoluntarioFormDTO());
        //modelo.addAttribute("listaMinisterios", servico.listarTodosMinisterios());
        modelo.addAttribute("listaBases", baseRepository.findAll());
        return "formulario-voluntario";
    }

    @PostMapping("/salvar")
    public String salvarVoluntario(
            @Valid @ModelAttribute("formDto") VoluntarioFormDTO formDto,
            BindingResult erros,
            @RequestParam(value = "arquivoAntecedentes", required = false)
            MultipartFile arquivo,
            Model modelo,
            RedirectAttributes redirect) {


        boolean isMaiorIdade = false;
        if (formDto.getDataNascimento() != null) {
            isMaiorIdade = java.time.Period.between(formDto.getDataNascimento(), java.time.LocalDate.now()).getYears() >= 18;
        }

        if (isMaiorIdade && (arquivo == null || arquivo.isEmpty())) {
            erros.rejectValue("termosAceitos", "erro.arquivo", "O arquivo de antecedentes é obrigatório para maiores de 18 anos.");
        }

        if (erros.hasErrors()) {
            modelo.addAttribute("listaBases", baseRepository.findAll());
            return "formulario-voluntario";
        }

        try {
            servico.registrarVoluntario(formDto, arquivo);
            redirect.addFlashAttribute("mensagemSucesso", "Inscrição realizada com glória!");
            return "redirect:/sucesso";
        } catch (Exception e) {
            modelo.addAttribute("mensagemErro", "Erro no sistema: " + e.getMessage());
            modelo.addAttribute("listaMinisterios", servico.listarTodosMinisterios());
            return "formulario-voluntario";
        }
    }

    @GetMapping("/sucesso")
    public String paginaSucesso() {
        return "sucesso";
    }
}
