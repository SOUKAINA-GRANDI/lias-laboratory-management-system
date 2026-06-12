package ma.lias.app.enums;

/* Rôle d'un membre dans le laboratoire
 * Définit la FONCTION/RESPONSABILITÉ du membre
 * ⚠️ Un membre peut changer de rôle sans changer de statut !
 */
public enum RoleMembre {
    
    DIRECTEUR("Directeur", "Responsable du laboratoire", 1),
    VICE_DIRECTEUR("Vice-Directeur", "Adjoint au directeur", 2),
    CHEF_EQUIPE("Chef d'Équipe", "Responsable d'une équipe de recherche", 3),
    MEMBRE_EQUIPE("Membre d'Équipe", "Membre simple d'une équipe", 4),
    SANS_ROLE("Sans Rôle", "Aucun rôle spécifique", 5);
    
    private final String libelle;
    private final String description;
    private final int niveau; // Pour hiérarchie
    
    RoleMembre(String libelle, String description, int niveau) {
        this.libelle = libelle;
        this.description = description;
        this.niveau = niveau;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getNiveau() {
        return niveau;
    }
    
    /*Vérifier si c'est un rôle de gouvernance*/
    public boolean estRoleGouvernance() {
        return this == DIRECTEUR || this == VICE_DIRECTEUR || this == CHEF_EQUIPE;
    }
    
    /*Vérifier si peut valider des demandes*/
    public boolean peutValiderDemandes() {
        return this == DIRECTEUR || this == VICE_DIRECTEUR;
    }
    
    /*Vérifier si peut gérer son équipe*/
    public boolean peutGererEquipe() {
        return this == CHEF_EQUIPE || this == DIRECTEUR;
    }
}