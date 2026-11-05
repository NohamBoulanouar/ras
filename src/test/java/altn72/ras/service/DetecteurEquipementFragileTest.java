package altn72.ras.service;

import altn72.ras.modele.Equipement;
import altn72.ras.modele.Incident;
import altn72.ras.modele.Station;
import altn72.ras.modele.TypeEquipement;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DetecteurEquipementFragileTest {

    private final Clock horlogeFixe = Clock.fixed(Instant.parse("2026-11-05T12:00:00Z"), ZoneOffset.UTC);
    private final LocalDateTime maintenant = LocalDateTime.now(horlogeFixe);
    private final DetecteurEquipementFragile detecteur = new DetecteurEquipementFragile(horlogeFixe);
    private final Equipement equipement =
            new Equipement("TEST-01", "Test", TypeEquipement.ASCENSEUR, new Station("Test"));

    private Incident ouvertIlYa(Duration anciennete) {
        return new Incident(equipement, "Panne", maintenant.minus(anciennete));
    }

    @Test
    void deuxIncidentsDansLaFenetreNeSuffisentPas() {
        List<Incident> incidents = List.of(ouvertIlYa(Duration.ofDays(1)), ouvertIlYa(Duration.ofDays(2)));
        assertFalse(detecteur.estFragile(incidents));
    }

    @Test
    void troisIncidentsDansLaFenetreRendentFragile() {
        List<Incident> incidents = List.of(ouvertIlYa(Duration.ofDays(1)), ouvertIlYa(Duration.ofDays(2)),
                ouvertIlYa(Duration.ofDays(3)));
        assertTrue(detecteur.estFragile(incidents));
    }

    @Test
    void unIncidentResoluCompteQuandMeme() {
        Incident resolu = ouvertIlYa(Duration.ofDays(5));
        resolu.resoudre(maintenant);
        List<Incident> incidents = List.of(ouvertIlYa(Duration.ofDays(1)), ouvertIlYa(Duration.ofDays(2)), resolu);
        assertTrue(detecteur.estFragile(incidents));
    }

    @Test
    void unIncidentOuvertIlYa30JoursEt1MinuteNeComptePlus() {
        List<Incident> incidents = List.of(ouvertIlYa(Duration.ofDays(1)), ouvertIlYa(Duration.ofDays(2)),
                ouvertIlYa(Duration.ofDays(30).plusMinutes(1)));
        assertFalse(detecteur.estFragile(incidents));
    }

    @Test
    void unIncidentOuvertIlYa29Jours23h59Compte() {
        List<Incident> incidents = List.of(ouvertIlYa(Duration.ofDays(1)), ouvertIlYa(Duration.ofDays(2)),
                ouvertIlYa(Duration.ofDays(29).plusHours(23).plusMinutes(59)));
        assertTrue(detecteur.estFragile(incidents));
    }

    @Test
    void lesIncidentsAnciensNeComptentPas() {
        Duration ilYa45Jours = Duration.ofDays(45);
        List<Incident> incidents = List.of(ouvertIlYa(Duration.ofDays(1)), ouvertIlYa(Duration.ofDays(2)),
                ouvertIlYa(ilYa45Jours), ouvertIlYa(ilYa45Jours), ouvertIlYa(ilYa45Jours),
                ouvertIlYa(ilYa45Jours), ouvertIlYa(ilYa45Jours));
        assertFalse(detecteur.estFragile(incidents));
    }
}
