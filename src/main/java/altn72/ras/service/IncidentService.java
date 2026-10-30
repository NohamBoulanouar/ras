package altn72.ras.service;

import altn72.ras.exception.RegleMetierException;
import altn72.ras.exception.RessourceIntrouvableException;
import altn72.ras.modele.Equipement;
import altn72.ras.modele.EtatFonctionnement;
import altn72.ras.modele.Incident;
import altn72.ras.modele.Prestataire;
import altn72.ras.repository.EquipementRepository;
import altn72.ras.repository.IncidentRepository;
import altn72.ras.repository.PrestataireRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final EquipementRepository equipementRepository;
    private final PrestataireRepository prestataireRepository;
    private final Clock horloge;

    public IncidentService(IncidentRepository incidentRepository,
                           EquipementRepository equipementRepository,
                           PrestataireRepository prestataireRepository,
                           Clock horloge) {
        this.incidentRepository = incidentRepository;
        this.equipementRepository = equipementRepository;
        this.prestataireRepository = prestataireRepository;
        this.horloge = horloge;
    }

    @Transactional
    public Incident declarer(Long equipementId, String description) {
        Equipement equipement = equipementRepository.findById(equipementId)
                .orElseThrow(() -> new RessourceIntrouvableException("Équipement introuvable : " + equipementId));
        Incident incident = new Incident(equipement, description, LocalDateTime.now(horloge));
        equipement.setEtat(EtatFonctionnement.HORS_SERVICE);

        return incidentRepository.save(incident);
    }

    @Transactional
    @PreAuthorize("hasRole('RESPONSABLE')")
    public void affecter(Long incidentId, Long prestataireId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RessourceIntrouvableException("Incident introuvable : " + incidentId));
        Prestataire prestataire = prestataireRepository.findById(prestataireId)
                .orElseThrow(() -> new RessourceIntrouvableException("Prestataire introuvable : " + prestataireId));
        incident.affecter(prestataire);
    }

    @Transactional
    @PreAuthorize("hasRole('RESPONSABLE')")
    public void resoudre(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RessourceIntrouvableException("Incident introuvable : "+ incidentId));
        incident.resoudre(LocalDateTime.now(horloge));

        Equipement equipement = incident.getEquipement();
        List<Incident> incidentsDeLEquipement = incidentRepository.findByEquipementIdOrderByDateOuvertureDesc(equipement.getId());
        boolean encoreEnPanne = false;
        for (Incident i : incidentsDeLEquipement) {
            if (!i.estResolu()){
                encoreEnPanne = true;
            }
        }
        if (!encoreEnPanne) {
            equipement.setEtat(EtatFonctionnement.EN_SERVICE);
        }
    }

    @Transactional
    @PreAuthorize("hasRole('RESPONSABLE')")
    public void modifierDescription(Long incidentId, String description) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RessourceIntrouvableException("Incident introuvable : "+ incidentId));

        if (incident.estResolu()) {
            throw new RegleMetierException("Impossible de modifier un incident déjà résolu");
        }
        incident.setDescription(description);

    }

    public List<Incident> listerActifs() {
        return incidentRepository.trouverIncidentsActifs();
    }

    public List<Incident> historique() {
        return incidentRepository.trouverHistoriqueResolus();
    }
}
