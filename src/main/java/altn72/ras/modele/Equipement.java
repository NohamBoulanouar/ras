package altn72.ras.modele;

import jakarta.persistence.*;

@Entity
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private String libelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEquipement type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatFonctionnement etat;

    @ManyToOne(optional = false)
    private Station station;

    protected Equipement() {
    }

    public Equipement(String reference, String libelle, TypeEquipement type, Station station) {
        this.reference = reference;
        this.libelle = libelle;
        this.type = type;
        this.etat = EtatFonctionnement.EN_SERVICE;
        this.station = station;
    }

    public Long getId() {
        return id;
    }
    public String getReference() {
        return reference;
    }
    public String getLibelle() {
        return libelle;
    }
    public TypeEquipement getType() {
        return type;
    }
    public Station getStation() {
        return station;
    }
    public EtatFonctionnement getEtat() {
        return etat;
    }

    public void setEtat(EtatFonctionnement etat) {
        this.etat = etat;
    }

}
