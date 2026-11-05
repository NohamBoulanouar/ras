package altn72.ras.controleur;

import altn72.ras.controleur.formulaire.DescriptionFormulaire;
import altn72.ras.controleur.formulaire.EvaluationFormulaire;
import altn72.ras.controleur.vue.FormatDuree;
import altn72.ras.modele.Incident;
import altn72.ras.service.CalculateurNiveauAttention;
import altn72.ras.service.EvaluationService;
import altn72.ras.service.IncidentService;
import altn72.ras.service.PrestataireService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/incidents/{id}")
public class IncidentControleur {

    private final IncidentService incidentService;
    private final EvaluationService evaluationService;
    private final PrestataireService prestataireService;
    private final CalculateurNiveauAttention calculateurNiveauAttention;
    private final FormatDuree formatDuree;

    public IncidentControleur(IncidentService incidentService,
                              EvaluationService evaluationService,
                              PrestataireService prestataireService,
                              CalculateurNiveauAttention calculateurNiveauAttention,
                              FormatDuree formatDuree) {
        this.incidentService = incidentService;
        this.evaluationService = evaluationService;
        this.prestataireService = prestataireService;
        this.calculateurNiveauAttention = calculateurNiveauAttention;
        this.formatDuree = formatDuree;
    }

    @GetMapping
    public String detail(@PathVariable Long id, Model model) {
        Incident incident = incidentService.trouver(id);
        if (!model.containsAttribute("descriptionFormulaire")) {
            model.addAttribute("descriptionFormulaire", new DescriptionFormulaire(incident.getDescription()));
        }
        if (!model.containsAttribute("evaluationFormulaire")) {
            model.addAttribute("evaluationFormulaire", new EvaluationFormulaire());
        }
        remplirDetail(incident, model);
        return "incident";
    }

    @PostMapping("/description")
    public String modifierDescription(@PathVariable Long id,
                                      @Valid @ModelAttribute("descriptionFormulaire") DescriptionFormulaire formulaire,
                                      BindingResult erreurs,
                                      Model model,
                                      RedirectAttributes redirection) {
        if (erreurs.hasErrors()) {
            model.addAttribute("evaluationFormulaire", new EvaluationFormulaire());
            remplirDetail(incidentService.trouver(id), model);
            return "incident";
        }
        incidentService.modifierDescription(id, formulaire.getDescription().strip());
        redirection.addFlashAttribute("succes", "Description modifiée.");
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/affecter")
    public String affecter(@PathVariable Long id, @RequestParam Long prestataireId, RedirectAttributes redirection) {
        incidentService.affecter(id, prestataireId);
        redirection.addFlashAttribute("succes", "Prestataire affecté.");
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/resoudre")
    public String resoudre(@PathVariable Long id, RedirectAttributes redirection) {
        incidentService.resoudre(id);
        redirection.addFlashAttribute("succes", "Incident résolu.");
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/evaluer")
    public String evaluer(@PathVariable Long id,
                          @Valid @ModelAttribute("evaluationFormulaire") EvaluationFormulaire formulaire,
                          BindingResult erreurs,
                          Model model,
                          RedirectAttributes redirection) {
        if (erreurs.hasErrors()) {
            Incident incident = incidentService.trouver(id);
            model.addAttribute("descriptionFormulaire", new DescriptionFormulaire(incident.getDescription()));
            remplirDetail(incident, model);
            return "incident";
        }
        evaluationService.evaluer(id, formulaire.getNote(), formulaire.getCommentaire());
        redirection.addFlashAttribute("succes", "Évaluation enregistrée.");
        return "redirect:/incidents/" + id;
    }

    private void remplirDetail(Incident incident, Model model) {
        model.addAttribute("incident", incident);
        model.addAttribute("dureeOuverture", formatDuree.enTexte(incidentService.dureeOuverture(incident)));
        model.addAttribute("niveau", incident.estResolu() ? null : calculateurNiveauAttention.calculer(incident));
        model.addAttribute("evaluation", evaluationService.trouverParIncident(incident.getId()).orElse(null));
        model.addAttribute("prestataires", prestataireService.lister());
    }
}
