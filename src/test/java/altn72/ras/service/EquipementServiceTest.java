package altn72.ras.service;

import altn72.ras.exception.RegleMetierException;
import altn72.ras.modele.Equipement;
import altn72.ras.modele.Station;
import altn72.ras.modele.TypeEquipement;
import altn72.ras.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@WithMockUser(roles = "RESPONSABLE")
class EquipementServiceTest {

    @Autowired
    private EquipementService equipementService;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private IncidentService incidentService;

    @Test
    void referenceEnDoubleRefusee() {
        Station station = stationRepository.save(new Station("station teste"));
        equipementService.creer("TEST-EQ-01", "Ascenseur", TypeEquipement.ASCENSEUR, station.getId());
        assertThrows(RegleMetierException.class,
                () -> equipementService.creer("TEST-EQ-01", "Ascenseur", TypeEquipement.ASCENSEUR, station.getId()));
    }

    @Test
    void lEquipementVoisinNEstPasContamine() {
        Station station = stationRepository.save(new Station("Station test"));
        Equipement fragile = equipementService.creer("TEST-FR-01", "Ascenseur", TypeEquipement.ASCENSEUR, station.getId());
        Equipement voisin = equipementService.creer("TEST-FR-02", "Escalier", TypeEquipement.ESCALIER_MECANIQUE, station.getId());

        incidentService.declarer(fragile.getId(), "Panne 1");
        incidentService.declarer(fragile.getId(), "Panne 2");
        incidentService.declarer(fragile.getId(), "Panne 3");
        incidentService.declarer(voisin.getId(), "Panne unique");

        assertTrue(equipementService.estFragile(fragile.getId()));
        assertFalse(equipementService.estFragile(voisin.getId()));
    }
}
