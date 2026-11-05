package altn72.ras.api;

import altn72.ras.modele.Incident;
import altn72.ras.repository.EquipementRepository;
import altn72.ras.repository.IncidentRepository;
import altn72.ras.repository.PrestataireRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IncidentApiControleurTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IncidentRepository incidentRepository;
    @Autowired
    private EquipementRepository equipementRepository;
    @Autowired
    private PrestataireRepository prestataireRepository;

    @Test
    void unAppelSansAuthentificationRecoitUn401() throws Exception {
        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void lesIncidentsActifsSontListesAvecLeurNiveau() throws Exception {
        mockMvc.perform(get("/api/incidents").with(agent()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)))
                .andExpect(jsonPath("$[?(@.equipement == 'ESC-BE-02')].niveauAttention").value("ROUGE"));
    }

    @Test
    void laDemandeDUnIncidentInexistantRenvoieUn404Explicite() throws Exception {
        mockMvc.perform(get("/api/incidents/{id}", 9999).with(agent()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Ressource introuvable"))
                .andExpect(jsonPath("$.detail").value("Incident introuvable : 9999"));
    }

    @Test
    void lAgentDeclareUnIncidentEtRecoitUn201AvecSonAdresse() throws Exception {
        Long equipementId = equipementRepository.findAll().getFirst().getId();
        mockMvc.perform(post("/api/incidents").with(agent())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipementId\": " + equipementId + ", \"description\": \"Panne signalée par l'API\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("http://localhost/api/incidents/")))
                .andExpect(jsonPath("$.statut").value("NON_AFFECTE"));
    }

    @Test
    void uneDeclarationSansDescriptionRenvoieUn400() throws Exception {
        mockMvc.perform(post("/api/incidents").with(agent())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipementId\": 1, \"description\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("La description est obligatoire."));
    }

    @Test
    void lAgentNePeutPasAffecterUnPrestataire() throws Exception {
        mockMvc.perform(put("/api/incidents/{id}/prestataire", incidentActif().getId()).with(agent())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prestataireId\": " + prestataireId() + "}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void leResponsableAffecteUnPrestataire() throws Exception {
        mockMvc.perform(put("/api/incidents/{id}/prestataire", incidentActif().getId()).with(responsable())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prestataireId\": " + prestataireId() + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("AFFECTE"));
    }

    @Test
    void resoudreUnIncidentDejaResoluRenvoieUn409() throws Exception {
        Long id = incidentActif().getId();
        mockMvc.perform(post("/api/incidents/{id}/resolution", id).with(responsable()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("RESOLU"));
        mockMvc.perform(post("/api/incidents/{id}/resolution", id).with(responsable()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Opération impossible"));
    }

    @Test
    void laDocumentationOpenApiDecritLesEndpoints() throws Exception {
        mockMvc.perform(get("/v3/api-docs").with(user("c.martin").roles("RESPONSABLE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/incidents/{id}']").exists());
    }

    private Incident incidentActif() {
        return incidentRepository.trouverIncidentsActifs().getFirst();
    }

    private Long prestataireId() {
        return prestataireRepository.findAll().getFirst().getId();
    }

    private static RequestPostProcessor agent() {
        return httpBasic("t.durand", "Agent2026!");
    }

    private static RequestPostProcessor responsable() {
        return httpBasic("c.martin", "Resp2026!");
    }
}
