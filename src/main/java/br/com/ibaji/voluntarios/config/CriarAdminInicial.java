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
        // Verifica se o usuário 'admin' JÁ existe especificamente
        if (repository.findByLogin("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setLogin("admin");

            // Defina a senha que você quer aqui
            admin.setSenha(passwordEncoder.encode("Admin123"));

            admin.setRole("SUPER_ADMIN");
            repository.save(admin);
            System.out.println("--- USUÁRIO ADMIN RECRIADO COM SUCESSO ---");
        }
    }
}
