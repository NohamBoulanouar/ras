package altn72.ras.repository;

import altn72.ras.modele.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {

    List<Station> findAllByOrderByNomAsc();

}