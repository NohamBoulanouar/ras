package altn72.ras.service;

import altn72.ras.modele.*;
import altn72.ras.repository.EquipementRepository;
import altn72.ras.repository.StationRepository;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class IncidentServiceTest {

    @Autowired
    private IncidentService incidentService;
    @Autowired private StationRepository stationRepository;
    @Autowired private EquipementRepository equipementRepository;

    private Equipement nouvelEquipement() {
        Station station = stationRepository.save(new Station("Station test"));
        return equipementRepository.save(
                new Equipement("TEST-SVC-01", "Ascenseur test", TypeEquipement.ASCENSEUR, station));
    }

    @Test
    void declarerPasseLEquipementHorsService() {
        Equipement equipement = nouvelEquipement();

        incidentService.declarer(equipement.getId(), "Panne de test");

        assertEquals(EtatFonctionnement.HORS_SERVICE, equipement.getEtat());
    }

    @Test
    void resoudreUnSeulDeDeuxIncidentsLaisseHorsService() {
        Equipement equipement = nouvelEquipement();
        Incident premier = incidentService.declarer(equipement.getId(), "Panne 1");
        incidentService.declarer(equipement.getId(), "Panne 2");

        incidentService.resoudre(premier.getId());

        assertEquals(EtatFonctionnement.HORS_SERVICE, equipement.getEtat());
    }
}