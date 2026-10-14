package altn72.ras.service;

import altn72.ras.modele.Incident;
import altn72.ras.modele.NiveauAttention;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

import static altn72.ras.modele.NiveauAttention.PRIS_EN_CHARGE;
import static altn72.ras.modele.NiveauAttention.ROUGE;
import static altn72.ras.modele.NiveauAttention.ORANGE;
import static altn72.ras.modele.NiveauAttention.VERT;

@Component
public class CalculateurNiveauAttention {

    // Seuils de RG3, définis une seule fois (§7.3 : pas de nombres magiques)
    static final Duration SEUIL_ORANGE = Duration.ofHours(48);
    static final Duration SEUIL_ROUGE = Duration.ofHours(72);

    private final Clock horloge;

    public CalculateurNiveauAttention(Clock horloge) {
        this.horloge = horloge;
    }

    public NiveauAttention calculer(Incident incident) {
        if (incident.estAffecte()){
            return PRIS_EN_CHARGE;
        }
        Duration age = Duration.between(incident.getDateOuverture(), LocalDateTime.now(horloge));
        if (age.compareTo(SEUIL_ROUGE) >= 0) {
            return ROUGE;
        }
        if (age.compareTo(SEUIL_ORANGE) >= 0){
            return ORANGE;
        }
        return VERT;
    }
}