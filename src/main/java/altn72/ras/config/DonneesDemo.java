package altn72.ras.config;

import altn72.ras.modele.*;
import altn72.ras.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class DonneesDemo implements CommandLineRunner {

    private final StationRepository stationRepository;
    private final EquipementRepository equipementRepository;
    private final PrestataireRepository prestataireRepository;
    private final IncidentRepository incidentRepository;
    private final EvaluationRepository evaluationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder encodeurMotDePasse;
    private final Clock horloge;

    // Injection par constructeur : Spring fournit tous ces objets automatiquement
    public DonneesDemo(StationRepository stationRepository, EquipementRepository equipementRepository,
                       PrestataireRepository prestataireRepository, IncidentRepository incidentRepository,
                       EvaluationRepository evaluationRepository, UtilisateurRepository utilisateurRepository,
                       PasswordEncoder encodeurMotDePasse, Clock horloge) {
        this.stationRepository = stationRepository;
        this.equipementRepository = equipementRepository;
        this.prestataireRepository = prestataireRepository;
        this.incidentRepository = incidentRepository;
        this.evaluationRepository = evaluationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.encodeurMotDePasse = encodeurMotDePasse;
        this.horloge = horloge;
    }

    @Override
    public void run(String... args) {
        // 1. Stations
        Station gareCentrale = stationRepository.save(new Station("Gare Centrale"));
        Station lesTilleuls = stationRepository.save(new Station("Les Tilleuls"));
        Station belvedere = stationRepository.save(new Station("Belvédère"));

        // 2. Prestataires
        Prestataire elevatec = prestataireRepository.save(
                new Prestataire("Élévatec", "contact@elevatec.example", "01 23 45 67 89"));
        Prestataire portisys = prestataireRepository.save(
                new Prestataire("Portisys", "contact@portisys.example", "01 42 53 26 47"));
        Prestataire viviotech = prestataireRepository.save(
                new Prestataire("Visiotech", "contact@visiotech.example", "01 25 53 54 06"));

        // 3. Équipements
        Equipement ascGc01 = equipementRepository.save(
                new Equipement("ASC-GC-01", "Ascenseur quai A", TypeEquipement.ASCENSEUR, gareCentrale));
        Equipement escGc02 = equipementRepository.save(
                new Equipement("ESC-GC-02", "Escalier sortie nord", TypeEquipement.ESCALIER_MECANIQUE, gareCentrale));
        Equipement porGc03 = equipementRepository.save(
                new Equipement("POR-GC-03", "Portique ligne 1", TypeEquipement.PORTIQUE, gareCentrale));
        Equipement disTi01 = equipementRepository.save(
                new Equipement("DIS-TI-01", "Distributeur du hall", TypeEquipement.DISTRIBUTEUR_TITRES, lesTilleuls));
        Equipement ecrTi02 = equipementRepository.save(
                new Equipement("ECR-TI-02", "Écran quai 2", TypeEquipement.ECRAN_INFORMATION, lesTilleuls));
        Equipement ascBe01 = equipementRepository.save(
                new Equipement("ASC-BE-01", "Ascenseur accès PMR", TypeEquipement.ASCENSEUR, belvedere));
        Equipement escBe02 = equipementRepository.save(
                new Equipement("ESC-BE-02", "Escalier du quai", TypeEquipement.ESCALIER_MECANIQUE, belvedere));
        Equipement porBe03 = equipementRepository.save(
                new Equipement("POR-BE-03", "Portique de sortie", TypeEquipement.PORTIQUE, belvedere));

        // 4. Comptes de test (mot de passe haché, jamais en clair en base)
        utilisateurRepository.save(new Utilisateur(
                "c.martin", encodeurMotDePasse.encode("Resp2026!"), "Claire", Profil.RESPONSABLE));
        utilisateurRepository.save(new Utilisateur(
                "t.durand", encodeurMotDePasse.encode("Agent2026!"), "Thomas", Profil.AGENT));

        // 5. Incidents non affectés : couvrent les seuils de RG3 (47 h vert, 48 h orange, 71 h orange, 72 h rouge)
        ouvrirIncident(ascGc01, "Ascenseur bloqué au 1er étage", 2);
        ouvrirIncident(disTi01, "Distributeur hors service, paiement par carte refusé", 47);
        ouvrirIncident(porGc03, "Portique de la gare centrale bloqué en position fermée", 48);
        ouvrirIncident(ecrTi02, "Écran du quai 2 éteint", 71);
        ouvrirIncident(escBe02, "Escalier mécanique à l'arrêt", 72);
        ouvrirIncident(porBe03, "Portique de sortie qui ne lit plus les titres", 120);

        // 6. Incidents affectés : jamais vert, orange ou rouge, même anciens (RG4)
        Incident escalierNord = ouvrirIncident(escGc02, "Bruit anormal et arrêts répétés", 80);
        escalierNord.setPrestataire(elevatec);
        incidentRepository.save(escalierNord);

        Incident ascenseurPmr = ouvrirIncident(ascBe01, "Porte de l'ascenseur qui ne se ferme plus", 10);
        ascenseurPmr.setPrestataire(elevatec);
        incidentRepository.save(ascenseurPmr);

        // 7. Incidents résolus : visibles dans l'historique et sur la fiche équipement
        Incident distributeurResolu = new Incident(disTi01, "Écran tactile du distributeur fissuré", ilYa(240));
        distributeurResolu.setPrestataire(portisys);
        distributeurResolu.setDateResolution(ilYa(192));
        incidentRepository.save(distributeurResolu);
        evaluationRepository.save(new Evaluation(
                distributeurResolu, 4, "Intervention rapide, pièce changée en une journée", ilYa(180)));

        Incident ascenseurResolu = new Incident(ascGc01, "Bouton d'appel du quai A inopérant", ilYa(144));
        ascenseurResolu.setPrestataire(elevatec);
        ascenseurResolu.setDateResolution(ilYa(120));
        incidentRepository.save(ascenseurResolu);

        Incident ecranResolu = new Incident(ecrTi02, "Affichage figé, redémarré par l'équipe de station", ilYa(72));
        ecranResolu.setDateResolution(ilYa(48));
        incidentRepository.save(ecranResolu);
    }

    // Déclare un incident actif : l'équipement concerné passe hors service
    private Incident ouvrirIncident(Equipement equipement, String description, long ilYaHeures) {
        equipement.setEtat(EtatFonctionnement.HORS_SERVICE);
        equipementRepository.save(equipement);
        return incidentRepository.save(new Incident(equipement, description, ilYa(ilYaHeures)));
    }

    private LocalDateTime ilYa(long heures) {
        return LocalDateTime.now(horloge).minusHours(heures);
    }
}