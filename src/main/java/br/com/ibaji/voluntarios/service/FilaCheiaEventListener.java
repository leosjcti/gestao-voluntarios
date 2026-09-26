package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.event.FilaCheiaEvent;
import br.com.ibaji.voluntarios.repository.VoluntarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FilaCheiaEventListener {

    private static final Logger log = LoggerFactory.getLogger(FilaCheiaEventListener.class);
    private final VoluntarioRepository repository;

    public FilaCheiaEventListener(VoluntarioRepository repository) {
        this.repository = repository;
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleFilaCheiaEvent(FilaCheiaEvent event) {
        Long id = event.getVoluntarioId();
        if (id != null) {
            log.warn("Fila do executor cheia. Evento capturado. Marcando voluntário {} como ERRO_FILA_CHEIA_REVISAO_MANUAL em nova transação", id);
            repository.findById(id).ifPresent(v -> {
                v.setStatusAntecedentes("ERRO_FILA_CHEIA_REVISAO_MANUAL");
                repository.save(v);
            });
        }
    }
}
