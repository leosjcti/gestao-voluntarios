package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.model.Ministerio;
import br.com.ibaji.voluntarios.repository.MinisterioRepository;
import br.com.ibaji.voluntarios.util.FormatadorTexto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MinisterioService {

    private final MinisterioRepository repository;

    public MinisterioService(MinisterioRepository repository) {
        this.repository = repository;
    }

    public List<Ministerio> listarTodos() {
        return repository.findAll();
    }

    // --- NOVO MÉTODO PAGINADO ---
    public Page<Ministerio> listarPaginado(String busca, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("nome").ascending());

        if (busca != null && !busca.isBlank()) {
            return repository.findByNomeContainingIgnoreCase(busca, pageable);
        }

        return repository.findAll(pageable);
    }

    public Optional<Ministerio> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void salvar(Ministerio ministerio) {
        ministerio.setNome(FormatadorTexto.padronizarNome(ministerio.getNome()));
        ministerio.setLider(FormatadorTexto.padronizarNome(ministerio.getLider()));

        repository.save(ministerio);
    }

    public void deletar(Long id) throws Exception {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new Exception("Não é possível excluir este ministério pois existem voluntários vinculados a ele.");
        }
    }
}