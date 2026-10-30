package altn72.ras.service;


import altn72.ras.exception.RegleMetierException;
import altn72.ras.modele.*;
import altn72.ras.repository.EquipementRepository;
import altn72.ras.repository.PrestataireRepository;
import altn72.ras.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class EvaluationServiceTest {

    @Autowired private EvaluationService evaluationService;
    @Autowired private IncidentService incidentService;
    @Autowired private StationRepository stationRepository;
    @Autowired private EquipementRepository equipementRepository;
    @Autowired private PrestataireRepository prestataireRepository;

    private Incident incidentResoluAvecPrestataire() {
        Station station = stationRepository.save(new Station("Station test"));
        Equipement equipement = equipementRepository.save(
                new Equipement("TEST-EVAL-01", "Ascenseur test", TypeEquipement.ASCENSEUR, station));
        Prestataire prestataire = prestataireRepository.save(
                new Prestataire("Élévatec test", "test@elevatec.fr", "0102030405"));

        Incident incident = incidentService.declarer(equipement.getId(), "Panne de test");
        incidentService.affecter(incident.getId(), prestataire.getId());
        incidentService.resoudre(incident.getId());
        return incident;
    }

    @Test
    void onNePeutPasEvaluerDeuxFois() {
        Incident incident = incidentResoluAvecPrestataire();
        evaluationService.evaluer(incident.getId(), 4, "Intervention rapide");

        assertThrows(RegleMetierException.class,
                () -> evaluationService.evaluer(incident.getId(), 5, "Encore"));
    }
}