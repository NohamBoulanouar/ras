package altn72.ras.service;

import altn72.ras.modele.Incident;
import altn72.ras.modele.NiveauAttention;
import altn72.ras.repository.IncidentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@WithMockUser(roles = "RESPONSABLE")
class TableauDeBordServiceTest {

    @Autowired
    private TableauDeBordService tableauDeBordService;
    @Autowired
    private IncidentService incidentService;
    @Autowired
    private IncidentRepository incidentRepository;

    @Test
    void unIncidentResoluNApparaitPlusParmiLesIncidentsActifs() {
        Incident incident = incidentRepository.trouverIncidentsActifs().getFirst();

        incidentService.resoudre(incident.getId());

        assertThat(tableauDeBordService.incidentsActifs(null, null))
                .extracting(ligne -> ligne.incident().getId())
                .doesNotContain(incident.getId());
        assertThat(incidentService.historique()).extracting(Incident::getId).contains(incident.getId());
    }

    @Test
    void lesIncidentsLesPlusUrgentsSontAfficheEnPremier() {
        List<NiveauAttention> niveaux = tableauDeBordService.incidentsActifs(null, null).stream()
                .map(LigneTableauDeBord::niveau).toList();

        assertThat(niveaux).first().isEqualTo(NiveauAttention.ROUGE);
        assertThat(niveaux).last().isEqualTo(NiveauAttention.PRIS_EN_CHARGE);
        assertThat(niveaux).containsSubsequence(NiveauAttention.ROUGE, NiveauAttention.ORANGE,
                NiveauAttention.VERT, NiveauAttention.PRIS_EN_CHARGE);
    }

    @Test
    void leFiltreParNiveauNeGardeQueCeNiveau() {
        assertThat(tableauDeBordService.incidentsActifs(null, NiveauAttention.ROUGE))
                .isNotEmpty()
                .allMatch(ligne -> ligne.niveau() == NiveauAttention.ROUGE);
    }

    @Test
    void lesIndicateursComptentActifsAttenteEtRouges() {
        IndicateursTableauDeBord indicateurs = tableauDeBordService.indicateurs();

        assertThat(indicateurs.incidentsActifs()).isEqualTo(8);
        assertThat(indicateurs.enAttenteDAffectation()).isEqualTo(6);
        assertThat(indicateurs.incidentsRouges()).isEqualTo(2);
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void unAgentNAccedePasAuTableauDeBord() {
        assertThatThrownBy(() -> tableauDeBordService.incidentsActifs(null, null))
                .isInstanceOf(AccessDeniedException.class);
    }
}
