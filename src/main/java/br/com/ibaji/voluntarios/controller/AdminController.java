package br.com.ibaji.voluntarios.controller;

import br.com.ibaji.voluntarios.model.dto.ArquivoDTO;
import br.com.ibaji.voluntarios.model.Voluntario;
import br.com.ibaji.voluntarios.service.VoluntarioService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final VoluntarioService voluntarioService;

    public AdminController(VoluntarioService voluntarioService) {
        this.voluntarioService = voluntarioService;
    }

    @GetMapping
    public String dashboard(
            @RequestParam(value = "busca", required = false) String busca,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        int tamanhoPagina = 10;
        Page<Voluntario> paginaVoluntarios = voluntarioService.listarPaginado(busca, page, tamanhoPagina);

        model.addAttribute("voluntariosPage", paginaVoluntarios);
        model.addAttribute("busca", busca);

        return "admin-dashboard";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadAntecedentes(@PathVariable Long id) {
        try {
            // Chama o serviço (Camada correta)
            ArquivoDTO arquivo = voluntarioService.baixarAntecedentes(id);

            // Retorna o arquivo para o navegador
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + arquivo.getNome() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(arquivo.getStream()));

        } catch (Exception e) {
            // Se der erro (arquivo não encontrado no S3, ou ID inválido), retorna texto amigável
            return ResponseEntity.status(404)
                    .body("Erro ao baixar arquivo: " + e.getMessage() + ". Verifique se o arquivo existe no armazenamento.");
        }
    }
}