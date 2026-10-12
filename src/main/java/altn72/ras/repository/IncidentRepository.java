package altn72.ras.repository;

import altn72.ras.modele.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface IncidentRepository extends JpaRepository<Incident,Long> {

    List<Incident> findByEquipementIdOrderByDateOuvertureDesc(Long equipementId);

    @Query("SELECT i FROM Incident i WHERE i.dateResolution IS NULL")
    List<Incident> trouverIncidentsActifs();

    @Query("SELECT i FROM Incident i WHERE i.dateResolution IS NOT NULL ORDER BY i.dateResolution DESC")
    List<Incident> trouverHistoriqueResolus();

    @Query(value = "SELECT COUNT(*) FROM incident WHERE date_resolution IS NULL", nativeQuery = true)
    long compterIncidentsActifs();


}
