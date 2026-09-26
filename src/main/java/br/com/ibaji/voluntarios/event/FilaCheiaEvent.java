package br.com.ibaji.voluntarios.event;

public class FilaCheiaEvent {
    private final Long voluntarioId;

    public FilaCheiaEvent(Long voluntarioId) {
        this.voluntarioId = voluntarioId;
    }

    public Long getVoluntarioId() {
        return voluntarioId;
    }
}
