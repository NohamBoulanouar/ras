package altn72.ras.modele;

public enum NiveauAttention {
    VERT("Moins de 48 h"),
    ORANGE("48 h à 72 h"),
    ROUGE("72 h et plus"),
    PRIS_EN_CHARGE("Pris en charge");

    private final String libelle;

    NiveauAttention(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
