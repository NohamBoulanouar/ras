package altn72.ras.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeclarationIncidentRequete(
        @NotNull(message = "L'identifiant de l'équipement est obligatoire.")
        Long equipementId,
        @NotBlank(message = "La description est obligatoire.")
        @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères.")
        String description) {
}
