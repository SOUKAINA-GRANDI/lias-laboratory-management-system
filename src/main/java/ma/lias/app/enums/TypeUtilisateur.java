package ma.lias.app.enums;

/*Type d'utilisateur pour l'authentification et les droits d'accès*/
public enum TypeUtilisateur {
    
    VISITEUR("Visiteur", "Accès public uniquement"),
    DOCTORANT("Doctorant", "Accès limité : publications et consultation"),
    MEMBRE_ASSOCIE("Membre Associé", "Accès limité : consultation et certains événements"),
    MEMBRE_PERMANENT("Membre Permanent", "Accès complet selon rôle"),
    DIRECTEUR("Directeur", "Accès complet + validation demandes"),
    ADMINISTRATEUR("Administrateur", "Accès total au système");
    
    private final String libelle;
    private final String description;
    
    TypeUtilisateur(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    /*Vérifier si a accès à l'administration*/
    public boolean estAdmin() {
        return this == ADMINISTRATEUR;
    }
    
    /* Vérifier si peut valider des demandes*/
    public boolean peutValider() {
        return this == DIRECTEUR || this == ADMINISTRATEUR;
    }
    
    /*Vérifier si peut créer des événements*/
    public boolean peutCreerEvenements() {
        return this == MEMBRE_PERMANENT || this == DIRECTEUR || this == ADMINISTRATEUR;
    }
    
    /* Vérifier si peut ajouter des publications*/
    public boolean peutAjouterPublications() {
        return this != VISITEUR;
    }
    
    /* Vérifier si a accès aux modules internes*/
    public boolean aAccesModulesInternes() {
        return this == MEMBRE_PERMANENT || this == DIRECTEUR || this == ADMINISTRATEUR;
    }
}