package altn72.ras.repository;

import altn72.ras.modele.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrestataireRepository extends JpaRepository<Prestataire, Long>{
    List<Prestataire> findAllByOrderByNomAsc();
}
