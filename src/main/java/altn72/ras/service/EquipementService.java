package altn72.ras.service;


import altn72.ras.exception.RegleMetierException;
import altn72.ras.exception.RessourceIntrouvableException;
import altn72.ras.modele.Equipement;
import altn72.ras.modele.Station;
import altn72.ras.modele.TypeEquipement;
import altn72.ras.repository.EquipementRepository;
import altn72.ras.repository.StationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipementService {

    private final EquipementRepository equipementRepository;
    private final StationRepository stationRepository;

    public EquipementService(EquipementRepository equipementRepository,
                             StationRepository stationRepository) {
        this.equipementRepository = equipementRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    @PreAuthorize("hasRole('RESPONSABLE')")
    public Equipement creer(String reference, String libelle, TypeEquipement type, Long stationId) {
        if (equipementRepository.existsByReference(reference)) {
            throw new RegleMetierException("Un équipement avec la référence " + reference + " existe déjà");
        }

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RessourceIntrouvableException("Station introuvable : " + stationId));

        Equipement equipement = new Equipement(reference, libelle, type, station);
        return equipementRepository.save(equipement);
    }

    public Equipement trouver(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Équipement introuvable : " + id));
    }

    public List<Equipement> listerParStation(Long stationId) {
        return equipementRepository.findByStationIdOrderByReferenceAsc(stationId);
    }
}
