package altn72.ras.modele;

import altn72.ras.exception.RegleMetierException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class IncidentTest {

    private final LocalDateTime ouverture = LocalDateTime.of(2026, 10, 1, 8, 0);

    private Incident nouvelIncident() {
        Equipement equipement = new Equipement("TEST-01", "Test", TypeEquipement.ASCENSEUR, new Station("Test"));
        return new Incident(equipement, "Panne de test", ouverture);
    }

    @Test
    void onNePeutPasAffecterUnIncidentResolu() {
        Incident incident = nouvelIncident();
        incident.resoudre(ouverture.plusHours(5));

        assertThrows(RegleMetierException.class,
                () -> incident.affecter(new Prestataire("Élévatec", "contact@elevatec.fr", "0102030405")));
    }

    @Test
    void dateDeResolutionAvantOuvertureRefusee() {
        Incident incident = nouvelIncident();

        assertThrows(RegleMetierException.class,
                () -> incident.resoudre(ouverture.minusHours(2)));
    }

    @Test
    void onNePeutPasResoudreDeuxFois() {
        Incident incident = nouvelIncident();
        incident.resoudre(ouverture.plusHours(5));

        assertThrows(RegleMetierException.class,
        () -> incident.resoudre(ouverture.plusHours(5)));
    }

    @Test
    void onPeutReaffecterTantQueNonResolu() {
        Incident incident = nouvelIncident();
        Prestataire premier = new Prestataire("Élévatec", "contact@elevatec.fr", "0102030405");
        Prestataire deuxieme = new Prestataire("Portisys", "contact@portisys.fr", "0607080910");

        incident.affecter(premier);
        incident.affecter(deuxieme);

        assertEquals(deuxieme, incident.getPrestataire());
    }
}
