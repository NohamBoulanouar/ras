package altn72.ras.service;

import altn72.ras.exception.RegleMetierException;
import altn72.ras.exception.RessourceIntrouvableException;
import altn72.ras.modele.Evaluation;
import altn72.ras.modele.Incident;
import altn72.ras.repository.EvaluationRepository;
import altn72.ras.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class EvaluationService {

    private final IncidentRepository incidentRepository;
    private final EvaluationRepository evaluationRepository;
    private final Clock horloge;

    public EvaluationService(IncidentRepository incidentRepository,
                             EvaluationRepository evaluationRepository,
                             Clock horloge) {
        this.incidentRepository = incidentRepository;
        this.evaluationRepository = evaluationRepository;
        this.horloge = horloge;
    }

    @Transactional
    public Evaluation evaluer(Long incidentId, int note, String commentaire) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RessourceIntrouvableException("Incident introuvable : " + incidentId));

        if (!incident.estResolu()) {
            throw new RegleMetierException("Impossible d'évaluer un incident non résolu");
        }

        if (incident.getPrestataire() == null) {
            throw new RegleMetierException("Impossible d'évaluer : l'incident a été résolu sans prestataire");
        }

        if (evaluationRepository.existsByIncidentId(incidentId)) {
            throw new RegleMetierException("Cet incident a déjà été évalué");
        }

        if (note < 1 || note > 5) {
            throw new RegleMetierException("La note doit être comprise entre 1 et 5");
        }

        Evaluation evaluation = new Evaluation(incident, note, commentaire, LocalDateTime.now(horloge));
        return evaluationRepository.save(evaluation);
    }
}
