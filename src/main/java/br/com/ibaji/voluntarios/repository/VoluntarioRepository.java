package br.com.ibaji.voluntarios.repository;

import br.com.ibaji.voluntarios.model.Voluntario;
import br.com.ibaji.voluntarios.model.dto.RelatorioMinisterioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface VoluntarioRepository extends JpaRepository<Voluntario, Long> {

    // 1. Relatório de Quantidade (LEFT JOIN para trazer quem tem 0 também)
    @Query(value = """
        SELECT m.id as id, m.nome as nome, COUNT(vm.voluntario_id) as quantidade 
        FROM ministerios m 
        LEFT JOIN voluntario_ministerios vm ON m.id = vm.ministerio_id 
        GROUP BY m.id, m.nome 
        ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<RelatorioMinisterioDTO> contarVoluntariosPorMinisterio();

    // 2. Buscar quem vai vencer nos próximos X dias (ou já venceu)
    // Traz quem vence entre HOJE e DataLimite, ou quem já está atrasado (menor que hoje)
    List<Voluntario> findByProximaRenovacaoLessThanEqual(LocalDate dataLimite);

    List<Voluntario> findByMinisteriosId(Long ministerioId);

    // Busca por nome ignorando maiúsculas/minúsculas, com paginação
    Page<Voluntario> findByNomeCompletoContainingIgnoreCase(String nome, Pageable pageable);

    // ... imports existentes

    // 1. Contar voluntários sem atestado (antecedentes is null)
    long countByAntecedentesIsNull();

    // 2. Contar manuais pendentes
    long countByManualEntregueFalse();

    // 3. Contar novos no mês atual (Postgres/H2 compatível)
    @Query("SELECT COUNT(v) FROM Voluntario v WHERE MONTH(v.dataCriacao) = :mes AND YEAR(v.dataCriacao) = :ano")
    long contarNovosNoMes(int mes, int ano);

    // 4. Buscar aniversariantes do mês atual
    @Query("SELECT v FROM Voluntario v WHERE MONTH(v.dataNascimento) = :mes ORDER BY DAY(v.dataNascimento) ASC")
    List<Voluntario> findAniversariantesDoMes(int mes);


    @Query(value = """
        SELECT m.id as id, m.nome as nome, COUNT(vm.voluntario_id) as quantidade 
        FROM ministerios m 
        LEFT JOIN voluntario_ministerios vm ON m.id = vm.ministerio_id 
        GROUP BY m.id, m.nome 
        ORDER BY quantidade DESC
    """,
            countQuery = "SELECT count(*) FROM ministerios", // Ensina o Spring a contar o total
            nativeQuery = true)
    Page<RelatorioMinisterioDTO> contarVoluntariosPorMinisterio(Pageable pageable);

    long countByDataIntegracaoIsNull();

    // 2. Agrupar voluntários por BASE do ministério
    // (Essa query é mais complexa pois cruza Voluntario -> Ministerio)
    @Query(value = """
        SELECT b.nome as nome, COUNT(DISTINCT vm.voluntario_id) as quantidade 
        FROM ministerios m 
        JOIN bases b ON m.base_id = b.id  -- JOIN COM A NOVA TABELA
        JOIN voluntario_ministerios vm ON m.id = vm.ministerio_id 
        GROUP BY b.id, b.nome 
        ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<RelatorioMinisterioDTO> contarVoluntariosPorBase();

    // Conta quantos foram criados em um mês/ano específico
    @Query("SELECT COUNT(v) FROM Voluntario v WHERE MONTH(v.dataCriacao) = :mes AND YEAR(v.dataCriacao) = :ano")
    long countByMesAno(int mes, int ano);

    @Query("SELECT v.dataNascimento FROM Voluntario v WHERE v.dataNascimento IS NOT NULL")
    List<LocalDate> findAllDataNascimento();
}