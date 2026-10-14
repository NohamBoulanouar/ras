package altn72.ras.service;

import altn72.ras.modele.*;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateurNiveauAttentionTest {

    // On fige "maintenant" à une date fixe
    private final Clock horlogeFixe = Clock.fixed(Instant.parse("2026-10-14T12:00:00Z"), ZoneOffset.UTC);
    private final LocalDateTime maintenant = LocalDateTime.now(horlogeFixe);
    private final CalculateurNiveauAttention calculateur = new CalculateurNiveauAttention(horlogeFixe);

    private Incident incidentOuvertIlYa(long heures) {
        Equipement equipement = new Equipement("TEST-01", "Test", TypeEquipement.ASCENSEUR, new Station("Test"));
        return new Incident(equipement, "Panne de test", maintenant.minusHours(heures));
    }

    @Test
    void ouvertDepuis47hEstVert() {
        assertEquals(NiveauAttention.VERT, calculateur.calculer(incidentOuvertIlYa(47)));
    }

    @Test
    void ouvertDepuis48hEstOrange() {
        assertEquals(NiveauAttention.ORANGE, calculateur.calculer(incidentOuvertIlYa(48)));
    }

    @Test
    void ouvertDepuis71hEstOrange() {
        assertEquals(NiveauAttention.ORANGE, calculateur.calculer(incidentOuvertIlYa(71)));
    }

    @Test
    void ouvertDepuis72hEstRouge() {
        assertEquals(NiveauAttention.ROUGE, calculateur.calculer(incidentOuvertIlYa(72)));
    }

    @Test
    void incidentAffecteEstPrisEnCharge() {
        // 100 h donnerait ROUGE : on vérifie que "pris en charge" passe avant la couleur
        Incident incident = incidentOuvertIlYa(100);
        incident.affecter(new Prestataire("Élévatec", "contact@elevatec.fr", "0102030405"));

        assertEquals(NiveauAttention.PRIS_EN_CHARGE, calculateur.calculer(incident));
    }
}
