package altn72.ras.service;

import altn72.ras.modele.Incident;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DetecteurEquipementFragile {

    static final int SEUIL_INCIDENTS = 3;
    static final Duration FENETRE = Duration.ofDays(30);

    private final Clock horloge;

    public DetecteurEquipementFragile(Clock horloge) {
        this.horloge = horloge;
    }

    public boolean estFragile(List<Incident> incidentsDeLEquipement) {
        LocalDateTime debutFenetre = LocalDateTime.now(horloge).minus(FENETRE);

        int nombre = 0;
        for (Incident incident : incidentsDeLEquipement) {
            if (!incident.getDateOuverture().isBefore(debutFenetre)) {
                nombre++;
            }
        }
        return nombre >= SEUIL_INCIDENTS;
    }
}
