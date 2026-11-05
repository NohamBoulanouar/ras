package altn72.ras.controleur;

import altn72.ras.service.EvaluationService;
import altn72.ras.service.ExportHistoriqueService;
import altn72.ras.service.IncidentService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.charset.StandardCharsets;

@Controller
public class HistoriqueControleur {

    // Marque d'ordre des octets : sans elle, Excel affiche mal les accents d'un CSV en UTF-8
    private static final String BOM_UTF8 = "﻿";

    private final IncidentService incidentService;
    private final EvaluationService evaluationService;
    private final ExportHistoriqueService exportHistoriqueService;

    public HistoriqueControleur(IncidentService incidentService,
                                EvaluationService evaluationService,
                                ExportHistoriqueService exportHistoriqueService) {
        this.incidentService = incidentService;
        this.evaluationService = evaluationService;
        this.exportHistoriqueService = exportHistoriqueService;
    }

    @GetMapping("/historique")
    public String historique(Model model) {
        model.addAttribute("incidents", incidentService.historique());
        model.addAttribute("notes", evaluationService.notesParIncident());
        return "historique";
    }

    @GetMapping("/historique/export.csv")
    public ResponseEntity<byte[]> exporter() {
        byte[] contenu = (BOM_UTF8 + exportHistoriqueService.exporterEnCsv()).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("historique-incidents.csv").build().toString())
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(contenu);
    }
}
