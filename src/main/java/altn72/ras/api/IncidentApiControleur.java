package altn72.ras.api;

import altn72.ras.modele.Incident;
import altn72.ras.service.CalculateurNiveauAttention;
import altn72.ras.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Incidents", description = "Consultation et cycle de vie des incidents")
public class IncidentApiControleur {

    private final IncidentService incidentService;
    private final CalculateurNiveauAttention calculateurNiveauAttention;

    public IncidentApiControleur(IncidentService incidentService,
                                 CalculateurNiveauAttention calculateurNiveauAttention) {
        this.incidentService = incidentService;
        this.calculateurNiveauAttention = calculateurNiveauAttention;
    }

    @GetMapping
    @Operation(summary = "Lister les incidents actifs (non résolus)")
    public List<IncidentDto> listerActifs() {
        return incidentService.listerActifs().stream().map(this::enDto).toList();
    }

    @GetMapping("/historique")
    @Operation(summary = "Lister les incidents résolus, du plus récent au plus ancien")
    public List<IncidentDto> historique() {
        return incidentService.historique().stream().map(this::enDto).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un incident")
    @ApiResponse(responseCode = "404", description = "Incident inexistant")
    public IncidentDto consulter(@PathVariable Long id) {
        return enDto(incidentService.trouver(id));
    }

    @PostMapping
    @Operation(summary = "Déclarer un incident sur un équipement")
    @ApiResponse(responseCode = "201", description = "Incident créé")
    public ResponseEntity<IncidentDto> declarer(@Valid @RequestBody DeclarationIncidentRequete requete) {
        Incident incident = incidentService.declarer(requete.equipementId(), requete.description().strip());
        URI adresse = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(incident.getId()).toUri();
        return ResponseEntity.created(adresse).body(enDto(incident));
    }

    @PutMapping("/{id}/prestataire")
    @Operation(summary = "Affecter ou réaffecter un prestataire (responsable)")
    @ApiResponse(responseCode = "409", description = "Incident déjà résolu")
    public IncidentDto affecter(@PathVariable Long id, @Valid @RequestBody AffectationRequete requete) {
        incidentService.affecter(id, requete.prestataireId());
        return enDto(incidentService.trouver(id));
    }

    @PostMapping("/{id}/resolution")
    @Operation(summary = "Déclarer l'incident résolu (responsable)")
    @ApiResponse(responseCode = "409", description = "Incident déjà résolu")
    public IncidentDto resoudre(@PathVariable Long id) {
        incidentService.resoudre(id);
        return enDto(incidentService.trouver(id));
    }

    private IncidentDto enDto(Incident incident) {
        return IncidentDto.depuis(incident, incident.estResolu() ? null : calculateurNiveauAttention.calculer(incident));
    }
}
