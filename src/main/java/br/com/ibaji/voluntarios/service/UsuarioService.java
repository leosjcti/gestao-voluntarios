package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.model.Usuario;
import br.com.ibaji.voluntarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public void salvar(Usuario usuario) {
        // Se estiver editando e a senha vier vazia, mantemos a antiga (lógica simples)
        // Aqui assumimos criação ou reset total de senha para simplificar
        if (usuario.getId() == null || !usuario.getSenha().isEmpty()) {
            usuario.setSenha(encoder.encode(usuario.getSenha()));
        } else {
            // Lógica para manter senha antiga se o campo vier vazio na edição
            Usuario antigo = repository.findById(usuario.getId()).orElseThrow();
            usuario.setSenha(antigo.getSenha());
        }
        repository.save(usuario);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
