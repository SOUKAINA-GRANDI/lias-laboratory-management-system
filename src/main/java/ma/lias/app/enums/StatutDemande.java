package ma.lias.app.enums;

/*Statut d'une demande (adhésion, matériel, etc.)*/
public enum StatutDemande {
    
    EN_ATTENTE("En Attente", "Demande soumise, en attente de traitement"),
    EN_COURS("En Cours", "Demande en cours d'examen"),
    ACCEPTEE("Acceptée", "Demande acceptée"),
    REFUSEE("Refusée", "Demande refusée"),
    ANNULEE("Annulée", "Demande annulée par le demandeur");
    
    private final String libelle;
    private final String description;
    
    StatutDemande(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    /* Vérifier si la demande peut être modifiée*/
    public boolean peutEtreModifiee() {
        return this == EN_ATTENTE || this == EN_COURS;
    }
    
    /*Vérifier si la demande est finalisée*/
    public boolean estFinalisee() {
        return this == ACCEPTEE || this == REFUSEE || this == ANNULEE;
    }
}