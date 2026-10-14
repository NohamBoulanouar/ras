package altn72.ras.modele;

import altn72.ras.exception.RegleMetierException;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Equipement equipement;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateOuverture;

    @ManyToOne
    private Prestataire prestataire;

    private LocalDateTime dateResolution;

    protected Incident() {
    }

    public Incident(Equipement equipement, String description, LocalDateTime dateOuverture) {
        this.equipement = equipement;
        this.description = description;
        this.dateOuverture = dateOuverture;
    }

    public boolean estResolu() {
        return dateResolution != null;
    }

    public boolean estAffecte() {
        return prestataire != null && !estResolu();
    }

    public Long getId() {
        return id;
    }

    public Equipement getEquipement() {
        return equipement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public Prestataire getPrestataire() {
        return prestataire;
    }

    public void affecter(Prestataire prestataire) {
        if (estResolu()){
            throw new RegleMetierException("Incident déjà résolu");
        }
        this.prestataire = prestataire;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public void resoudre(LocalDateTime dateResolution) {
        if (estResolu()){
            throw new RegleMetierException("Incident déjà résolu");
        }
        if (dateResolution.isBefore(dateOuverture)) {
            throw new RegleMetierException("La date de résolution ne peut pas être antérieure à l’ouverture");
        }
        this.dateResolution = dateResolution;
    }
}
