package altn72.ras.controleur.vue;

import org.springframework.stereotype.Component;

import java.time.Duration;

@Component("formatDuree")
public class FormatDuree {

    public String enTexte(Duration duree) {
        long jours = duree.toDays();
        long heures = duree.toHoursPart();
        if (jours > 0) {
            return jours + " j " + heures + " h";
        }
        return heures + " h " + String.format("%02d", duree.toMinutesPart());
    }
}
