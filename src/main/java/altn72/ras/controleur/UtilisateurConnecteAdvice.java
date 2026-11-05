package altn72.ras.controleur;

import altn72.ras.modele.Utilisateur;
import altn72.ras.repository.UtilisateurRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class UtilisateurConnecteAdvice {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurConnecteAdvice(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @ModelAttribute("utilisateurConnecte")
    public Utilisateur utilisateurConnecte(Principal connecte) {
        if (connecte == null) {
            return null;
        }
        return utilisateurRepository.findByLogin(connecte.getName()).orElse(null);
    }
}
