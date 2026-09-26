package br.com.ibaji.voluntarios.config;

public class IdAwareRunnable implements Runnable {
    private final Runnable delegate;
    private final Long id;

    public IdAwareRunnable(Runnable delegate, Long id) {
        this.delegate = delegate;
        this.id = id;
    }

    @Override
    public void run() {
        delegate.run();
    }

    public Long getId() {
        return id;
    }
}
