package altn72.ras.controleur;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccueilControleur {

    @GetMapping("/")
    public String accueil(Authentication connecte) {
        boolean responsable = connecte.getAuthorities().stream()
                .anyMatch(droit -> droit.getAuthority().equals("ROLE_RESPONSABLE"));
        return responsable ? "redirect:/tableau-de-bord" : "redirect:/stations";
    }
}
