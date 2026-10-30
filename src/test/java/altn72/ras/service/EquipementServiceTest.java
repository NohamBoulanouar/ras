package altn72.ras.service;


import altn72.ras.exception.RegleMetierException;
import altn72.ras.modele.Station;
import altn72.ras.modele.TypeEquipement;
import altn72.ras.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@WithMockUser(roles = "RESPONSABLE")
class EquipementServiceTest {

    @Autowired
    private EquipementService equipementService;
    @Autowired
    private StationRepository stationRepository;

    @Test
    void referenceEnDoubleRefusee() {
        Station station = stationRepository.save(new Station("station teste"));
        equipementService.creer("TEST-EQ-01", "Ascenseur", TypeEquipement.ASCENSEUR, station.getId());
        assertThrows(RegleMetierException.class,
                () -> equipementService.creer("TEST-EQ-01", "Ascenseur", TypeEquipement.ASCENSEUR, station.getId()));
    }
}
