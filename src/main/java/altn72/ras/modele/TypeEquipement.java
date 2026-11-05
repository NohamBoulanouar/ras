package altn72.ras.modele;

public enum TypeEquipement {
    ASCENSEUR("Ascenseur"),
    ESCALIER_MECANIQUE("Escalier mécanique"),
    PORTIQUE("Portique"),
    DISTRIBUTEUR_TITRES("Distributeur de titres"),
    ECRAN_INFORMATION("Écran d'information");

    private final String libelle;

    TypeEquipement(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
