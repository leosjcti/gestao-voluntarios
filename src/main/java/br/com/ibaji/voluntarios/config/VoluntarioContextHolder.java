package br.com.ibaji.voluntarios.config;

public class VoluntarioContextHolder {
    private static final ThreadLocal<Long> CONTEXT = new ThreadLocal<>();

    public static void setId(Long id) {
        CONTEXT.set(id);
    }

    public static Long getId() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
