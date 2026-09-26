package br.com.ibaji.voluntarios.config;

import br.com.ibaji.voluntarios.event.FilaCheiaEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AsyncConfigTest {

    @Test
    void devePublicarFilaCheiaEventQuandoRejeitado() {
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        AsyncConfig config = new AsyncConfig();
        
        ThreadPoolTaskExecutor executor = config.documentAnalysisExecutor(publisher);
        RejectedExecutionHandler handler = executor.getThreadPoolExecutor().getRejectedExecutionHandler();

        // Simula Runnable com ID atrelado (IdAwareRunnable)
        Runnable stub = () -> {};
        IdAwareRunnable idAware = new IdAwareRunnable(stub, 42L);

        // Dispara a rejeição manualmente
        handler.rejectedExecution(idAware, mock(ThreadPoolExecutor.class));

        // Valida que o evento foi lançado
        ArgumentCaptor<FilaCheiaEvent> captor = ArgumentCaptor.forClass(FilaCheiaEvent.class);
        verify(publisher).publishEvent(captor.capture());

        FilaCheiaEvent event = captor.getValue();
        assertNotNull(event);
        assertEquals(42L, event.getVoluntarioId());
    }
}
