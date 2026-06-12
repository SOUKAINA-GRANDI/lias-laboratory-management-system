package ma.lias.app.enums;

/**
 * Statut d'un membre dans le laboratoire
 * Définit QUI est le membre (son affiliation)
 */
public enum StatutMembre {
    
    PERMANENT("Membre Permanent", "Membre officiel du laboratoire LIAS"),
    ASSOCIE("Membre Associé", "Membre d'un autre laboratoire, collaborateur du LIAS"),
    DOCTORANT("Doctorant", "Étudiant en doctorat"),
    RETRAITE("Retraité", "Ancien membre en fin de carrière"),
    ANCIEN_MEMBRE("Ancien Membre", "Membre ayant quitté le laboratoire");
    
    private final String libelle; /* libelle =le texte affiché à l'utilisateur.*/
    private final String description;
    
    StatutMembre(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    /* Vérifier si le membre peut être actif*/
    public boolean peutEtreActif() {
        return this == PERMANENT || this == ASSOCIE || this == DOCTORANT;
    }
    
    /* Vérifier si le membre peut modifier son profil*/
    
    public boolean peutModifierProfil() {
        return this != RETRAITE && this != ANCIEN_MEMBRE;
    }
    
    /*Vérifier si le membre peut accéder aux modules internes*/
    
    public boolean aAccesModulesInternes() {
        return this == PERMANENT || this == ASSOCIE;
    }
}