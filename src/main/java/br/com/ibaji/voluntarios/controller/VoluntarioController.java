package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.dto.VoluntarioFormDTO;
import br.com.ibaji.voluntarios.service.VoluntarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/voluntarios")
public class VoluntarioController {

    private final VoluntarioService servico;

    public VoluntarioController(VoluntarioService servico) {
        this.servico = servico;
    }

    @GetMapping("/novo")
    public String exibirFormulario(Model modelo) {
        modelo.addAttribute("formDto", new VoluntarioFormDTO());
        modelo.addAttribute("listaMinisterios", servico.listarTodosMinisterios());
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


        if (arquivo == null || arquivo.isEmpty()) {
            erros.rejectValue("termosAceitos", "erro.arquivo", "O arquivo de antecedentes é obrigatório.");
        }

        if (erros.hasErrors()) {
            modelo.addAttribute("listaMinisterios", servico.listarTodosMinisterios());
            return "formulario-voluntario";
        }

        try {
            servico.registrarVoluntario(formDto, arquivo);
            redirect.addFlashAttribute("mensagemSucesso", "Inscrição realizada com glória!");
            return "redirect:/voluntarios/sucesso";
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
