package altn72.ras.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GestionnaireErreurs {

    @ExceptionHandler(RessourceIntrouvableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String ressourceIntrouvable(RessourceIntrouvableException exception, Model model) {
        model.addAttribute("titre", "Élément introuvable");
        model.addAttribute("message", exception.getMessage());
        return "erreur";
    }

    @ExceptionHandler(RegleMetierException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String regleMetier(RegleMetierException exception, Model model) {
        model.addAttribute("titre", "Action impossible");
        model.addAttribute("message", exception.getMessage());
        return "erreur";
    }
}
