package altn72.ras.controleur;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static altn72.ras.controleur.UtilisateursDeTest.agent;
import static altn72.ras.controleur.UtilisateursDeTest.responsable;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TableauDeBordControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void leResponsableArriveSurLeTableauDeBord() throws Exception {
        mockMvc.perform(get("/").with(responsable()))
                .andExpect(redirectedUrl("/tableau-de-bord"));
    }

    @Test
    void lAgentArriveSurLaListeDesStations() throws Exception {
        mockMvc.perform(get("/").with(agent()))
                .andExpect(redirectedUrl("/stations"));
    }

    @Test
    void leTableauDeBordAfficheLesIndicateursEtLesIncidentsActifs() throws Exception {
        mockMvc.perform(get("/tableau-de-bord").with(responsable()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("incidents actifs")))
                .andExpect(content().string(containsString("Escalier mécanique à l&#39;arrêt")))
                .andExpect(content().string(not(containsString("Écran tactile du distributeur fissuré"))));
    }

    @Test
    void leFiltreParNiveauSeRetrouveDansLAdresse() throws Exception {
        mockMvc.perform(get("/tableau-de-bord").param("niveau", "VERT").with(responsable()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ascenseur bloqué au 1er étage")))
                .andExpect(content().string(not(containsString("Escalier mécanique à l&#39;arrêt"))));
    }

    @Test
    void leTableauDeBordSignaleLesIncidentsDUnEquipementFragile() throws Exception {
        mockMvc.perform(get("/tableau-de-bord").param("niveau", "VERT").with(responsable()))
                .andExpect(content().string(containsString("ASC-GC-01")))
                .andExpect(content().string(containsString("Équipement fragile")));
        mockMvc.perform(get("/tableau-de-bord").param("niveau", "ROUGE").with(responsable()))
                .andExpect(content().string(not(containsString("Équipement fragile"))));
    }

    @Test
    void lAgentNAccedePasAuTableauDeBord() throws Exception {
        mockMvc.perform(get("/tableau-de-bord").with(agent()))
                .andExpect(status().isForbidden());
    }
}
