package altn72.ras.controleur;

import altn72.ras.controleur.formulaire.DeclarationIncidentFormulaire;
import altn72.ras.modele.Incident;
import altn72.ras.service.EquipementService;
import altn72.ras.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EquipementControleur {

    private final EquipementService equipementService;
    private final IncidentService incidentService;

    public EquipementControleur(EquipementService equipementService, IncidentService incidentService) {
        this.equipementService = equipementService;
        this.incidentService = incidentService;
    }

    @GetMapping("/equipements/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        model.addAttribute("equipement", equipementService.trouver(id));
        model.addAttribute("incidents", incidentService.listerParEquipement(id));
        model.addAttribute("fragile", equipementService.estFragile(id));
        return "equipement";
    }

    @GetMapping("/equipements/{id}/declarer")
    public String formulaireDeclaration(@PathVariable Long id, Model model) {
        model.addAttribute("equipement", equipementService.trouver(id));
        model.addAttribute("formulaire", new DeclarationIncidentFormulaire());
        return "declarer";
    }

    @PostMapping("/equipements/{id}/declarer")
    public String declarer(@PathVariable Long id,
                           @Valid @ModelAttribute("formulaire") DeclarationIncidentFormulaire formulaire,
                           BindingResult erreurs,
                           Model model,
                           RedirectAttributes redirection) {
        if (erreurs.hasErrors()) {
            model.addAttribute("equipement", equipementService.trouver(id));
            return "declarer";
        }
        Incident incident = incidentService.declarer(id, formulaire.getDescription().strip());
        redirection.addFlashAttribute("succes", messageDeDeclaration(id));
        return "redirect:/incidents/" + incident.getId();
    }

    private String messageDeDeclaration(Long equipementId) {
        String message = "Incident déclaré : l'équipement est passé hors service.";
        if (equipementService.estFragile(equipementId)) {
            message += " Attention : cet équipement est désormais fragile (au moins 3 incidents en 30 jours).";
        }
        return message;
    }
}
