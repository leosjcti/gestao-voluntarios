package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.repository.VoluntarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ErroAnaliseService {

    private final VoluntarioRepository repository;

    public ErroAnaliseService(VoluntarioRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarErro(Long id, String status) {
        repository.findById(id).ifPresent(v -> {
            v.setStatusAntecedentes(status);
            repository.save(v);
        });
    }
}
