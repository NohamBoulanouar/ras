package altn72.ras.service;

import altn72.ras.exception.RessourceIntrouvableException;
import altn72.ras.modele.Prestataire;
import altn72.ras.repository.EvaluationRepository;
import altn72.ras.repository.PrestataireRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrestataireService {

    private final PrestataireRepository prestataireRepository;
    private final EvaluationRepository evaluationRepository;

    public PrestataireService(PrestataireRepository prestataireRepository,
                              EvaluationRepository evaluationRepository) {
        this.prestataireRepository = prestataireRepository;
        this.evaluationRepository = evaluationRepository;
    }

    public List<Prestataire> lister() {
        return prestataireRepository.findAllByOrderByNomAsc();
    }

    public Prestataire trouver(Long id) {
        return prestataireRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Prestataire introuvable : " + id));
    }

    // Vide tant que le prestataire n'a reçu aucune évaluation
    public Optional<Double> noteMoyenne(Long prestataireId) {
        return Optional.ofNullable(evaluationRepository.calculerNoteMoyenne(prestataireId));
    }
}
