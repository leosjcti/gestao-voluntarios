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
        if (repository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setLogin("admin");
            admin.setSenha(passwordEncoder.encode("admin123")); // Criptografa
            admin.setRole("ADMIN");
            repository.save(admin);
            System.out.println("--- USUÁRIO ADMIN CRIADO ---");
        }
    }
}
