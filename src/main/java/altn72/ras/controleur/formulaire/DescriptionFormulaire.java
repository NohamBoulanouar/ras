package altn72.ras.controleur.formulaire;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DescriptionFormulaire {

    @NotBlank(message = "La description ne peut pas être vide.")
    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères.")
    private String description;

    public DescriptionFormulaire() {
    }

    public DescriptionFormulaire(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
