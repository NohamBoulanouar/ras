package altn72.ras.controleur;

import altn72.ras.repository.EquipementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static altn72.ras.controleur.UtilisateursDeTest.agent;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EquipementControleurTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EquipementRepository equipementRepository;

    @Test
    void unVisiteurNonConnecteEstRenvoyeVersLaConnexion() throws Exception {
        mockMvc.perform(get("/stations"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connexion"));
    }

    @Test
    void lAgentVoitLaFicheDUnEquipementEtSesIncidents() throws Exception {
        Long id = idDe("ASC-GC-01");
        mockMvc.perform(get("/equipements/{id}", id).with(agent()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bonjour Thomas (agent)")))
                .andExpect(content().string(containsString("Ascenseur bloqué au 1er étage")));
    }

    @Test
    void uneDeclarationSansDescriptionReafficheLeFormulaireAvecUnMessage() throws Exception {
        Long id = idDe("POR-BE-03");
        mockMvc.perform(post("/equipements/{id}/declarer", id).param("description", "  ").with(agent()).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("formulaire", "description"))
                .andExpect(content().string(containsString("Décrivez la panne constatée.")));
    }

    @Test
    void lAgentDeclareUnIncident() throws Exception {
        Long id = idDe("POR-BE-03");
        mockMvc.perform(post("/equipements/{id}/declarer", id).param("description", "Lecteur de badge HS").with(agent()).with(csrf()))
                .andExpect(redirectedUrlPattern("/incidents/*"))
                .andExpect(flash().attribute("succes", not(containsString("fragile"))));
    }

    @Test
    void laTroisiemeDeclarationEnTrenteJoursSignaleUnEquipementFragile() throws Exception {
        Long id = idDe("ECR-TI-02");
        mockMvc.perform(post("/equipements/{id}/declarer", id).param("description", "Écran à nouveau éteint").with(agent()).with(csrf()))
                .andExpect(flash().attribute("succes", containsString("fragile")));
    }

    @Test
    void laFicheAfficheLeBadgeSeulementPourUnEquipementFragile() throws Exception {
        mockMvc.perform(get("/equipements/{id}", idDe("ASC-GC-01")).with(agent()))
                .andExpect(content().string(containsString("Équipement fragile")));
        mockMvc.perform(get("/equipements/{id}", idDe("POR-BE-03")).with(agent()))
                .andExpect(content().string(not(containsString("Équipement fragile"))));
    }

    @Test
    void unEquipementInexistantAfficheUnePageIntrouvable() throws Exception {
        mockMvc.perform(get("/equipements/{id}", 9999).with(agent()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Équipement introuvable : 9999")));
    }

    @Test
    void lesStationsEtLeursEquipementsSontConsultables() throws Exception {
        mockMvc.perform(get("/stations").with(agent()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Belvédère")));

        Long stationId = equipementRepository.findAll().stream()
                .filter(equipement -> equipement.getReference().equals("ASC-BE-01"))
                .findFirst().orElseThrow().getStation().getId();
        mockMvc.perform(get("/stations/{id}", stationId).with(agent()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ASC-BE-01")))
                .andExpect(content().string(containsString("Hors service")));
    }

    private Long idDe(String reference) {
        return equipementRepository.findAll().stream()
                .filter(equipement -> equipement.getReference().equals(reference))
                .findFirst().orElseThrow().getId();
    }
}
