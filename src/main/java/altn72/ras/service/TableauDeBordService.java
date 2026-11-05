package altn72.ras.service;

import altn72.ras.modele.Incident;
import altn72.ras.modele.NiveauAttention;
import altn72.ras.repository.IncidentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static altn72.ras.modele.NiveauAttention.ORANGE;
import static altn72.ras.modele.NiveauAttention.PRIS_EN_CHARGE;
import static altn72.ras.modele.NiveauAttention.ROUGE;
import static altn72.ras.modele.NiveauAttention.VERT;

@Service
public class TableauDeBordService {

    // Le plus urgent en premier ; les incidents pris en charge passent après ceux qui attendent encore
    private static final List<NiveauAttention> ORDRE_D_URGENCE = List.of(ROUGE, ORANGE, VERT, PRIS_EN_CHARGE);

    private static final Comparator<LigneTableauDeBord> PLUS_URGENT_D_ABORD =
            Comparator.comparing((LigneTableauDeBord ligne) -> ORDRE_D_URGENCE.indexOf(ligne.niveau()))
                    .thenComparing(ligne -> ligne.incident().getDateOuverture());

    private final IncidentRepository incidentRepository;
    private final IncidentService incidentService;
    private final CalculateurNiveauAttention calculateurNiveauAttention;

    public TableauDeBordService(IncidentRepository incidentRepository,
                                IncidentService incidentService,
                                CalculateurNiveauAttention calculateurNiveauAttention) {
        this.incidentRepository = incidentRepository;
        this.incidentService = incidentService;
        this.calculateurNiveauAttention = calculateurNiveauAttention;
    }

    @PreAuthorize("hasRole('RESPONSABLE')")
    public List<LigneTableauDeBord> incidentsActifs(Long stationId, NiveauAttention niveau) {
        return incidentService.listerActifs().stream()
                .filter(incident -> stationId == null || incident.getEquipement().getStation().getId().equals(stationId))
                .map(this::enLigne)
                .filter(ligne -> niveau == null || ligne.niveau() == niveau)
                .sorted(PLUS_URGENT_D_ABORD)
                .toList();
    }

    @PreAuthorize("hasRole('RESPONSABLE')")
    public IndicateursTableauDeBord indicateurs() {
        List<LigneTableauDeBord> actifs = incidentService.listerActifs().stream().map(this::enLigne).toList();
        return new IndicateursTableauDeBord(
                incidentRepository.compterIncidentsActifs(),
                actifs.stream().filter(ligne -> ligne.niveau() != PRIS_EN_CHARGE).count(),
                actifs.stream().filter(ligne -> ligne.niveau() == ROUGE).count());
    }

    private LigneTableauDeBord enLigne(Incident incident) {
        return new LigneTableauDeBord(incident, calculateurNiveauAttention.calculer(incident),
                incidentService.dureeOuverture(incident));
    }
}
