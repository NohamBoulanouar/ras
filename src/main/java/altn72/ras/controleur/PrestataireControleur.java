package altn72.ras.controleur;

import altn72.ras.modele.Prestataire;
import altn72.ras.service.IncidentService;
import altn72.ras.service.PrestataireService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class PrestataireControleur {

    private final PrestataireService prestataireService;
    private final IncidentService incidentService;

    public PrestataireControleur(PrestataireService prestataireService, IncidentService incidentService) {
        this.prestataireService = prestataireService;
        this.incidentService = incidentService;
    }

    @GetMapping("/prestataires")
    public String lister(Model model) {
        Map<Prestataire, Double> notesMoyennes = new LinkedHashMap<>();
        for (Prestataire prestataire : prestataireService.lister()) {
            notesMoyennes.put(prestataire, prestataireService.noteMoyenne(prestataire.getId()).orElse(null));
        }
        model.addAttribute("notesMoyennes", notesMoyennes);
        return "prestataires";
    }

    @GetMapping("/prestataires/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        model.addAttribute("prestataire", prestataireService.trouver(id));
        model.addAttribute("noteMoyenne", prestataireService.noteMoyenne(id).orElse(null));
        model.addAttribute("incidents", incidentService.listerParPrestataire(id));
        return "prestataire";
    }
}
