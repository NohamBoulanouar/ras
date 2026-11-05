package altn72.ras.repository;

import altn72.ras.modele.Equipement;
import altn72.ras.modele.Evaluation;
import altn72.ras.modele.Incident;
import altn72.ras.modele.Prestataire;
import altn72.ras.modele.Station;
import altn72.ras.modele.TypeEquipement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class IncidentRepositoryTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 5, 12, 0);

    @Autowired
    private IncidentRepository incidentRepository;
    @Autowired
    private EvaluationRepository evaluationRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private EquipementRepository equipementRepository;
    @Autowired
    private PrestataireRepository prestataireRepository;

    private Equipement equipement;
    private Prestataire prestataire;
    private Incident actif;
    private Incident resoluHier;
    private Incident resoluAvantHier;

    @BeforeEach
    void preparerLesDonnees() {
        Station station = stationRepository.save(new Station("Station test"));
        equipement = equipementRepository.save(new Equipement("REP-01", "Portique", TypeEquipement.PORTIQUE, station));
        prestataire = prestataireRepository.save(new Prestataire("Prestataire test", "p@test.fr", "01"));

        actif = incidentRepository.save(new Incident(equipement, "En cours", MAINTENANT.minusHours(5)));
        resoluAvantHier = incidentRepository.save(resolu("Ancien", 100, 48));
        resoluHier = incidentRepository.save(resolu("Récent", 30, 24));
    }

    @Test
    void lesIncidentsActifsExcluentLesResolus() {
        assertThat(incidentRepository.trouverIncidentsActifs()).containsExactly(actif);
    }

    @Test
    void laRequeteNativeCompteLesMemesIncidentsActifsQueLaRequeteJpql() {
        assertThat(incidentRepository.compterIncidentsActifs())
                .isEqualTo(incidentRepository.trouverIncidentsActifs().size());
    }

    @Test
    void lHistoriqueEstTrieParDateDeResolutionDecroissante() {
        assertThat(incidentRepository.trouverHistoriqueResolus()).containsExactly(resoluHier, resoluAvantHier);
    }

    @Test
    void lesIncidentsDUnEquipementSontTriesParDateDOuvertureDecroissante() {
        assertThat(incidentRepository.findByEquipementIdOrderByDateOuvertureDesc(equipement.getId()))
                .containsExactly(actif, resoluHier, resoluAvantHier);
    }

    @Test
    void laNoteMoyenneDUnPrestataireEstLaMoyenneDeSesEvaluations() {
        evaluationRepository.save(new Evaluation(resoluHier, 5, null, MAINTENANT));
        evaluationRepository.save(new Evaluation(resoluAvantHier, 2, null, MAINTENANT));

        assertThat(evaluationRepository.calculerNoteMoyenne(prestataire.getId())).isEqualTo(3.5);
    }

    @Test
    void unPrestataireSansEvaluationNAPasDeNoteMoyenne() {
        assertThat(evaluationRepository.calculerNoteMoyenne(prestataire.getId())).isNull();
    }

    private Incident resolu(String description, long ouvertIlYaHeures, long resoluIlYaHeures) {
        Incident incident = new Incident(equipement, description, MAINTENANT.minusHours(ouvertIlYaHeures));
        incident.affecter(prestataire);
        incident.resoudre(MAINTENANT.minusHours(resoluIlYaHeures));
        return incident;
    }
}
