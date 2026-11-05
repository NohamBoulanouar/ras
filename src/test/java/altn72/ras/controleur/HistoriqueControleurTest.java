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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HistoriqueControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void lAgentConsulteLHistoriqueSansBoutonDExport() throws Exception {
        mockMvc.perform(get("/historique").with(agent()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Écran tactile du distributeur fissuré")))
                .andExpect(content().string(not(containsString("Exporter en CSV"))));
    }

    @Test
    void leResponsableExporteLHistoriqueEnCsv() throws Exception {
        mockMvc.perform(get("/historique/export.csv").with(responsable()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("historique-incidents.csv")))
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(content().string(containsString("Incident;Station;Équipement")))
                .andExpect(content().string(containsString("Portisys;4")));
    }

    @Test
    void lAgentNePeutPasExporter() throws Exception {
        mockMvc.perform(get("/historique/export.csv").with(agent()))
                .andExpect(status().isForbidden());
    }
}
