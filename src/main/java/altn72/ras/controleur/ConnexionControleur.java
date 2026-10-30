package altn72.ras.controleur;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConnexionControleur {

    @GetMapping("/connexion")
    public String connexion() {
        return "connexion";
    }
}
