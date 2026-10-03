package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.dto.VoluntarioFormDTO;
import br.com.ibaji.voluntarios.repository.BaseRepository;
import br.com.ibaji.voluntarios.service.VoluntarioService;
import br.com.ibaji.voluntarios.service.TurnstileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
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
    private final TurnstileService turnstileService;

    @Value("${cloudflare.turnstile.site-key}")
    private String siteKey;

    public VoluntarioController(VoluntarioService servico, BaseRepository baseRepository, TurnstileService turnstileService) {
        this.servico = servico;
        this.baseRepository = baseRepository;
        this.turnstileService = turnstileService;
    }

    @GetMapping("/cadastro")
    public String exibirFormulario(Model modelo, HttpServletRequest request) {
        System.out.println("Sitekey enviada ao template: '" + siteKey + "'");

        request.getSession(true);

        modelo.addAttribute("formDto", new VoluntarioFormDTO());
        modelo.addAttribute("listaBases", baseRepository.findAll());
        modelo.addAttribute("turnstileSiteKey", siteKey);
        return "formulario-voluntario";
    }

    @PostMapping("/salvar")
    public String salvarVoluntario(
            @Valid @ModelAttribute("formDto") VoluntarioFormDTO formDto,
            BindingResult erros,
            @RequestParam(value = "arquivoAntecedentes", required = false) MultipartFile arquivo,
            @RequestParam(value = "cf-turnstile-response", required = false) String turnstileToken,
            HttpServletRequest request,
            Model modelo,
            RedirectAttributes redirect) {

        // Forçar a criação de sessão para evitar erro de commit do Thymeleaf/CSRF
        request.getSession(true);

        // Obter IP do cliente tratando proxies/Cloudflare
        String clientIp = request.getHeader("CF-Connecting-IP");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp != null && clientIp.contains(",")) {
                clientIp = clientIp.split(",")[0].trim();
            }
        }
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        // Validar Token Turnstile
        boolean captchaValido = turnstileService.verificarToken(turnstileToken, clientIp);
        if (!captchaValido) {
            erros.reject("erro.captcha", "A verificação de segurança anti-robô falhou. Por favor, marque a caixa do Turnstile.");
        }

        boolean isMaiorIdade = false;
        if (formDto.getDataNascimento() != null) {
            isMaiorIdade = java.time.Period.between(formDto.getDataNascimento(), java.time.LocalDate.now()).getYears() >= 18;
        }

        if (isMaiorIdade && (arquivo == null || arquivo.isEmpty())) {
            erros.rejectValue("termosAceitos", "erro.arquivo", "O arquivo de antecedentes é obrigatório para maiores de 18 anos.");
        }

        if (servico.existePorCpf(formDto.getCpf())) {
            erros.rejectValue("cpf", "erro.cpf", "Já existe um voluntário cadastrado com este CPF.");
        }
        
        if (servico.existePorNome(formDto.getNomeCompleto())) {
            erros.rejectValue("nomeCompleto", "erro.nomeCompleto", "Já existe um voluntário cadastrado com este nome.");
        }

        if (erros.hasErrors()) {
            System.out.println("Sitekey enviada no erro: '" + siteKey + "'");
            modelo.addAttribute("listaBases", baseRepository.findAll());
            modelo.addAttribute("turnstileSiteKey", siteKey);
            
            // Repassa o erro geral de captcha se houver
            if (!captchaValido) {
                modelo.addAttribute("mensagemErro", "A verificação de segurança falhou. Por favor, complete o Turnstile.");
            }
            return "formulario-voluntario";
        }

        try {
            servico.registrarVoluntario(formDto, arquivo);
            redirect.addFlashAttribute("mensagemSucesso", "Inscrição realizada com glória!");
            return "redirect:/sucesso";
        } catch (Exception e) {
            modelo.addAttribute("mensagemErro", "Erro no sistema: " + e.getMessage());
            modelo.addAttribute("listaBases", baseRepository.findAll());
            modelo.addAttribute("turnstileSiteKey", siteKey);
            return "formulario-voluntario";
        }
    }

    @GetMapping("/sucesso")
    public String paginaSucesso() {
        return "sucesso";
    }

    @GetMapping("/api/validar-cpf")
    @ResponseBody
    public boolean validarCpf(@RequestParam String cpf) {
        return !servico.existePorCpf(cpf);
    }

    @GetMapping("/api/validar-nome")
    @ResponseBody
    public boolean validarNome(@RequestParam String nome) {
        return !servico.existePorNome(nome);
    }
}
