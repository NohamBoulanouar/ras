package altn72.ras.service;

import altn72.ras.exception.RessourceIntrouvableException;
import altn72.ras.modele.Station;
import altn72.ras.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> lister() {
        return stationRepository.findAllByOrderByNomAsc();
    }

    public Station trouver(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Station introuvable : " + id));
    }
}
