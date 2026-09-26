package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.repository.VoluntarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatusAntecedentesService {

    private final VoluntarioRepository repository;

    public StatusAntecedentesService(VoluntarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void registrarSucesso(Long id, String status, String chave) {
        repository.findById(id).ifPresent(v -> {
            v.setStatusAntecedentes(status);
            if (chave != null) {
                v.setChaveAutenticacaoDocumento(chave);
            }
            if (v.getAntecedentes() != null) {
                try {
                    v.getAntecedentes().setStatus(br.com.ibaji.voluntarios.model.enums.StatusAntecedentes.valueOf(status));
                } catch (Exception e) {
                    // Ignore se o status não for um enum válido, embora deva ser
                }
            }
            v.setAntecedentesAnalisados(true);
        });
    }
}
