package br.com.ibaji.voluntarios.config;

import br.com.ibaji.voluntarios.event.FilaCheiaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean(name = "documentAnalysisExecutor")
    public ThreadPoolTaskExecutor documentAnalysisExecutor(ApplicationEventPublisher eventPublisher) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("DocAnalysis-");

        executor.setTaskDecorator(new TaskDecorator() {
            @Override
            public Runnable decorate(Runnable runnable) {
                Long id = VoluntarioContextHolder.getId();
                return new IdAwareRunnable(runnable, id);
            }
        });

        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                if (r instanceof IdAwareRunnable) {
                    Long id = ((IdAwareRunnable) r).getId();
                    if (id != null) {
                        eventPublisher.publishEvent(new FilaCheiaEvent(id));
                    }
                } else {
                    log.error("Fila do executor cheia e tarefa rejeitada não possui ID.");
                    throw new java.util.concurrent.RejectedExecutionException("Task rejeitada e wrapper não reconhecido");
                }
            }
        });

        executor.initialize();
        return executor;
    }
}
