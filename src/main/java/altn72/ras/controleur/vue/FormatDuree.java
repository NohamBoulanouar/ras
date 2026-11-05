package altn72.ras.controleur.vue;

import java.time.Duration;

public final class FormatDuree {

    private FormatDuree() {
    }

    public static String enTexte(Duration duree) {
        long jours = duree.toDays();
        long heures = duree.toHoursPart();
        if (jours > 0) {
            return jours + " j " + heures + " h";
        }
        long minutes = duree.toMinutesPart();
        return heures + " h " + String.format("%02d", minutes);
    }
}
