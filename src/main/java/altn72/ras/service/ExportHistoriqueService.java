package altn72.ras.service;

import altn72.ras.modele.Incident;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class ExportHistoriqueService {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Point-virgule : séparateur attendu par Excel en français
    private static final CSVFormat FORMAT_CSV = CSVFormat.DEFAULT.builder()
            .setDelimiter(';')
            .setHeader("Incident", "Station", "Équipement", "Type", "Description", "Ouverture",
                    "Résolution", "Durée (heures)", "Prestataire", "Note")
            .get();

    private final IncidentService incidentService;
    private final EvaluationService evaluationService;

    public ExportHistoriqueService(IncidentService incidentService, EvaluationService evaluationService) {
        this.incidentService = incidentService;
        this.evaluationService = evaluationService;
    }

    @PreAuthorize("hasRole('RESPONSABLE')")
    public String exporterEnCsv() {
        Map<Long, Integer> notes = evaluationService.notesParIncident();
        StringWriter sortie = new StringWriter();
        try (CSVPrinter csv = new CSVPrinter(sortie, FORMAT_CSV)) {
            for (Incident incident : incidentService.historique()) {
                csv.printRecord(
                        incident.getId(),
                        incident.getEquipement().getStation().getNom(),
                        incident.getEquipement().getReference(),
                        incident.getEquipement().getType().getLibelle(),
                        incident.getDescription(),
                        incident.getDateOuverture().format(FORMAT_DATE),
                        incident.getDateResolution().format(FORMAT_DATE),
                        incidentService.dureeOuverture(incident).toHours(),
                        incident.getPrestataire() == null ? "" : incident.getPrestataire().getNom(),
                        notes.getOrDefault(incident.getId(), null));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Échec de la génération de l'export CSV", e);
        }
        return sortie.toString();
    }
}
