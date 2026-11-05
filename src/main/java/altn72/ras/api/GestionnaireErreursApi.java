package altn72.ras.api;

import altn72.ras.exception.RegleMetierException;
import altn72.ras.exception.RessourceIntrouvableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice(basePackageClasses = IncidentApiControleur.class)
public class GestionnaireErreursApi {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ProblemDetail ressourceIntrouvable(RessourceIntrouvableException exception) {
        return probleme(HttpStatus.NOT_FOUND, "Ressource introuvable", exception.getMessage());
    }

    @ExceptionHandler(RegleMetierException.class)
    public ProblemDetail regleMetier(RegleMetierException exception) {
        return probleme(HttpStatus.CONFLICT, "Opération impossible", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail donneesInvalides(MethodArgumentNotValidException exception) {
        String details = exception.getBindingResult().getFieldErrors().stream()
                .map(erreur -> erreur.getDefaultMessage())
                .collect(Collectors.joining(" "));
        return probleme(HttpStatus.BAD_REQUEST, "Données invalides", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail corpsIllisible() {
        return probleme(HttpStatus.BAD_REQUEST, "Données invalides", "Le corps de la requête n'est pas un JSON valide.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail accesRefuse() {
        return probleme(HttpStatus.FORBIDDEN, "Accès refusé", "Cette opération est réservée au responsable d'exploitation.");
    }

    private ProblemDetail probleme(HttpStatus statut, String titre, String detail) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(statut, detail);
        probleme.setTitle(titre);
        return probleme;
    }
}
