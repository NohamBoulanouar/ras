package altn72.ras.controleur;

import altn72.ras.modele.Incident;
import altn72.ras.repository.IncidentRepository;
import altn72.ras.repository.PrestataireRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Predicate;

import static altn72.ras.controleur.UtilisateursDeTest.agent;
import static altn72.ras.controleur.UtilisateursDeTest.responsable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IncidentControleurTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IncidentRepository incidentRepository;
    @Autowired
    private PrestataireRepository prestataireRepository;

    @Test
    void leDetailAfficheEquipementStationDureeEtNiveau() throws Exception {
        Incident incident = incidentOuvertSur("POR-GC-03");
        mockMvc.perform(get("/incidents/{id}", incident.getId()).with(agent()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Gare Centrale")))
                .andExpect(content().string(containsString("2 j 0 h")))
                .andExpect(content().string(containsString("48 h à 72 h")))
                .andExpect(content().string(containsString("Seul le responsable d'exploitation")))
                .andExpect(content().string(not(containsString("Affecter un prestataire"))));
    }

    @Test
    void leResponsableVoitLesActionsSurUnIncidentEnCours() throws Exception {
        Incident incident = incidentOuvertSur("POR-GC-03");
        mockMvc.perform(get("/incidents/{id}", incident.getId()).with(responsable()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Affecter un prestataire")))
                .andExpect(content().string(containsString("Déclarer l'incident résolu")));
    }

    @Test
    void unIncidentInexistantAfficheUnePageIntrouvable() throws Exception {
        mockMvc.perform(get("/incidents/{id}", 9999).with(agent()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Incident introuvable : 9999")));
    }

    @Test
    void leResponsableAffecteUnPrestataire() throws Exception {
        Incident incident = incidentOuvertSur("POR-BE-03");
        Long prestataireId = prestataireRepository.findAllByOrderByNomAsc().getFirst().getId();

        mockMvc.perform(post("/incidents/{id}/affecter", incident.getId())
                        .param("prestataireId", prestataireId.toString()).with(responsable()).with(csrf()))
                .andExpect(redirectedUrl("/incidents/" + incident.getId()));

        assertThat(incidentRepository.findById(incident.getId()).orElseThrow().estAffecte()).isTrue();
    }

    @Test
    void lAgentNePeutPasResoudreMemeEnEnvoyantLaRequeteALaMain() throws Exception {
        Incident incident = incidentOuvertSur("POR-BE-03");

        mockMvc.perform(post("/incidents/{id}/resoudre", incident.getId()).with(agent()).with(csrf()))
                .andExpect(status().isForbidden());

        assertThat(incidentRepository.findById(incident.getId()).orElseThrow().estResolu()).isFalse();
    }

    @Test
    void uneNoteHorsLimitesReafficheLeDetailAvecUnMessage() throws Exception {
        Incident incident = incidentResoluAvecPrestataireSansEvaluation();

        mockMvc.perform(post("/incidents/{id}/evaluer", incident.getId())
                        .param("note", "6").with(responsable()).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("evaluationFormulaire", "note"))
                .andExpect(content().string(containsString("La note doit être comprise entre 1 et 5.")));
    }

    @Test
    void uneDeuxiemeEvaluationEstRefuseeAvecUnMessageClair() throws Exception {
        Incident incident = incidentEvalue();

        mockMvc.perform(post("/incidents/{id}/evaluer", incident.getId())
                        .param("note", "5").with(responsable()).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Cet incident a déjà été évalué")));
    }

    @Test
    void laListeDesPrestatairesAfficheLaNoteMoyenne() throws Exception {
        mockMvc.perform(get("/prestataires").with(agent()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("4,0 / 5")));
    }

    @Test
    void leResponsableModifieLaDescriptionDUnIncidentEnCours() throws Exception {
        Incident incident = incidentOuvertSur("POR-BE-03");

        mockMvc.perform(post("/incidents/{id}/description", incident.getId())
                        .param("description", "Lecteur de titres hors service").with(responsable()).with(csrf()))
                .andExpect(redirectedUrl("/incidents/" + incident.getId()));

        assertThat(incidentRepository.findById(incident.getId()).orElseThrow().getDescription())
                .isEqualTo("Lecteur de titres hors service");
    }

    @Test
    void uneDescriptionVideEstRefuseeAvecUnMessage() throws Exception {
        Incident incident = incidentOuvertSur("POR-BE-03");

        mockMvc.perform(post("/incidents/{id}/description", incident.getId())
                        .param("description", "").with(responsable()).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("La description ne peut pas être vide.")));
    }

    @Test
    void laFicheDUnPrestataireListeSesInterventions() throws Exception {
        Long prestataireId = prestataireRepository.findAllByOrderByNomAsc().stream()
                .filter(prestataire -> prestataire.getNom().equals("Portisys"))
                .findFirst().orElseThrow().getId();

        mockMvc.perform(get("/prestataires/{id}", prestataireId).with(agent()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Écran tactile du distributeur fissuré")))
                .andExpect(content().string(containsString("4,0 / 5")));
    }

    private Incident incidentOuvertSur(String reference) {
        return premierIncident(incident -> !incident.estResolu()
                && incident.getEquipement().getReference().equals(reference));
    }

    private Incident incidentResoluAvecPrestataireSansEvaluation() {
        return premierIncident(incident -> incident.estResolu()
                && incident.getEquipement().getReference().equals("ASC-GC-01"));
    }

    private Incident incidentEvalue() {
        return premierIncident(incident -> incident.estResolu()
                && incident.getEquipement().getReference().equals("DIS-TI-01"));
    }

    private Incident premierIncident(Predicate<Incident> critere) {
        return incidentRepository.findAll().stream().filter(critere).findFirst().orElseThrow();
    }
}
