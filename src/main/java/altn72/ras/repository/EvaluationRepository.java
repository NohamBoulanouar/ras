package altn72.ras.repository;

import altn72.ras.modele.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;





public interface EvaluationRepository extends JpaRepository<Evaluation, Long>{

    boolean existsByIncidentId(Long incidentId);

    Optional<Evaluation> findByIncidentId(Long incidentId);

    @Query("SELECT AVG(e.note) FROM Evaluation e WHERE e.incident.prestataire.id = :idPrestataire")
    Double calculerNoteMoyenne(@Param("idPrestataire") Long idPrestataire);
}
