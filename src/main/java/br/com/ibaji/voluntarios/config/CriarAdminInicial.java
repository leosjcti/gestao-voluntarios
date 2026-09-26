package br.com.ibaji.voluntarios.config;

import br.com.ibaji.voluntarios.model.Usuario;
import br.com.ibaji.voluntarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CriarAdminInicial implements CommandLineRunner {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public CriarAdminInicial(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. ADMIN ALTO (Nível 3) - Força atualização se já existir
        Usuario admin = repository.findByLogin("admin").orElseGet(() -> {
            Usuario u = new Usuario();
            u.setLogin("admin");
            return u;
        });
        admin.setSenha(passwordEncoder.encode("Admin123"));
        admin.setRole("ADMIN_ALTO");
        repository.save(admin);
        System.out.println("--- USUÁRIO ADMIN ALTO ATUALIZADO/CRIADO ---");

        // 2. ADMIN MEDIO (Nível 2)
        if (repository.findByLogin("medio").isEmpty()) {
            Usuario medio = new Usuario();
            medio.setLogin("medio");
            medio.setSenha(passwordEncoder.encode("Medio123"));
            medio.setRole("ADMIN_MEDIO");
            repository.save(medio);
            System.out.println("--- USUÁRIO ADMIN MEDIO CRIADO ---");
        }

        // 3. ADMIN BASICO (Nível 1)
        if (repository.findByLogin("basico").isEmpty()) {
            Usuario basico = new Usuario();
            basico.setLogin("basico");
            basico.setSenha(passwordEncoder.encode("Basico123"));
            basico.setRole("ADMIN_BASICO");
            repository.save(basico);
            System.out.println("--- USUÁRIO ADMIN BASICO CRIADO ---");
        }
    }
}
