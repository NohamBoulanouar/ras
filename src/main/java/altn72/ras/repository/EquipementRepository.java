package altn72.ras.repository;

import altn72.ras.modele.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipementRepository extends JpaRepository<Equipement, Long>{

    boolean existsByReference(String reference);
    List<Equipement> findByStationIdOrderByReferenceAsc(Long stationId);

}
