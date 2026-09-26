package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.model.Voluntario;
import br.com.ibaji.voluntarios.event.FilaCheiaEvent;
import br.com.ibaji.voluntarios.repository.VoluntarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FilaCheiaEventListenerTest {

    @Test
    void deveSalvarErroFilaCheiaRevisaoManualQuandoEventoCapturado() {
        VoluntarioRepository repository = mock(VoluntarioRepository.class);
        FilaCheiaEventListener listener = new FilaCheiaEventListener(repository);

        Voluntario v = new Voluntario();
        v.setId(99L);
        when(repository.findById(99L)).thenReturn(Optional.of(v));

        // Dispara evento
        listener.handleFilaCheiaEvent(new FilaCheiaEvent(99L));

        ArgumentCaptor<Voluntario> captor = ArgumentCaptor.forClass(Voluntario.class);
        verify(repository).save(captor.capture());

        assertEquals("ERRO_FILA_CHEIA_REVISAO_MANUAL", captor.getValue().getStatusAntecedentes());
    }
}
