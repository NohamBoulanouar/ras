package altn72.ras.controleur;

import altn72.ras.repository.UtilisateurRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class AccueilControleur {

    private final UtilisateurRepository utilisateurRepository;

    public AccueilControleur(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping("/")
    public String accueil(Principal connecte, Model model) {
        utilisateurRepository.findByLogin(connecte.getName()).ifPresent(utilisateur -> {
            model.addAttribute("prenom", utilisateur.getPrenom());
            model.addAttribute("profil", utilisateur.getProfil());
        });
        return "accueil";
    }
}
