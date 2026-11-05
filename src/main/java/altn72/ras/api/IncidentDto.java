package altn72.ras.api;

import altn72.ras.modele.Incident;
import altn72.ras.modele.NiveauAttention;

import java.time.LocalDateTime;

public record IncidentDto(
        Long id,
        String equipement,
        String station,
        String description,
        LocalDateTime dateOuverture,
        StatutIncident statut,
        NiveauAttention niveauAttention,
        String prestataire,
        LocalDateTime dateResolution) {

    public enum StatutIncident {
        NON_AFFECTE, AFFECTE, RESOLU
    }

    static IncidentDto depuis(Incident incident, NiveauAttention niveau) {
        return new IncidentDto(
                incident.getId(),
                incident.getEquipement().getReference(),
                incident.getEquipement().getStation().getNom(),
                incident.getDescription(),
                incident.getDateOuverture(),
                statutDe(incident),
                incident.estResolu() ? null : niveau,
                incident.getPrestataire() == null ? null : incident.getPrestataire().getNom(),
                incident.getDateResolution());
    }

    private static StatutIncident statutDe(Incident incident) {
        if (incident.estResolu()) {
            return StatutIncident.RESOLU;
        }
        return incident.estAffecte() ? StatutIncident.AFFECTE : StatutIncident.NON_AFFECTE;
    }
}
