package ma.lias.app.test;

import java.util.List;
import javax.persistence.EntityManager;
import ma.lias.app.entity.Equipe;
import ma.lias.app.entity.Membre;
import ma.lias.app.enums.StatutMembre;
import ma.lias.app.util.JPAUtil;

/**
 * Test de lecture des membres
 * NE FAIT PAS D'INSERTION - Les données sont ajoutées via SQL
 */
public class TestMembre {

    public static void main(String[] args) {
        
        System.out.println("═══════════════════════════════════════");
        System.out.println("🧪 TEST ENTITÉ MEMBRE");
        System.out.println("═══════════════════════════════════════\n");
        
        EntityManager em = null;
        
        try {
            em = JPAUtil.getEntityManager();
            
            // ========== VÉRIFIER LES TABLES ==========
            System.out.println("📌 Tables créées :");
            @SuppressWarnings("unchecked")
            List<Object> tables = em.createNativeQuery("SHOW TABLES").getResultList();
            for (Object table : tables) {
                System.out.println("   ✅ " + table);
            }
            
            // ========== STATISTIQUES ÉQUIPES ==========
            System.out.println("\n📊 Statistiques Équipes :");
            
            Long totalEquipes = (Long) em.createQuery("SELECT COUNT(e) FROM Equipe e").getSingleResult();
            System.out.println("   Total équipes : " + totalEquipes);
            
            if (totalEquipes > 0) {
                System.out.println("\n📋 Liste des équipes :");
                @SuppressWarnings("unchecked")
                List<Equipe> equipes = em.createQuery("SELECT e FROM Equipe e ORDER BY e.nom").getResultList();
                for (Equipe e : equipes) {
                    System.out.println("   - " + e.getNom() + " (" + e.getAcronyme() + ")");
                }
            }
            
            // ========== STATISTIQUES MEMBRES ==========
            System.out.println("\n📊 Statistiques Membres :");
            
            Long totalMembres = (Long) em.createQuery(
                "SELECT COUNT(m) FROM Membre m WHERE m.actif = true"
            ).getSingleResult();
            System.out.println("   Total membres actifs : " + totalMembres);
            
            if (totalMembres > 0) {
                
                // Par statut
                Long nbPermanents = (Long) em.createNamedQuery("Membre.countByStatut")
                                             .setParameter("statut", StatutMembre.PERMANENT)
                                             .getSingleResult();
                System.out.println("   Membres permanents : " + nbPermanents);
                
                Long nbAssocies = (Long) em.createNamedQuery("Membre.countByStatut")
                                           .setParameter("statut", StatutMembre.ASSOCIE)
                                           .getSingleResult();
                System.out.println("   Membres associés : " + nbAssocies);
                
                Long nbDoctorants = (Long) em.createNamedQuery("Membre.countByStatut")
                                             .setParameter("statut", StatutMembre.DOCTORANT)
                                             .getSingleResult();
                System.out.println("   Doctorants : " + nbDoctorants);
                
                Long nbRetraites = (Long) em.createNamedQuery("Membre.countByStatut")
                                            .setParameter("statut", StatutMembre.RETRAITE)
                                            .getSingleResult();
                if (nbRetraites > 0) {
                    System.out.println("   Retraités : " + nbRetraites);
                }
                
                // ========== LISTER LES MEMBRES ==========
                System.out.println("\n📋 Liste des membres :");
                
                @SuppressWarnings("unchecked")
                List<Membre> membres = em.createNamedQuery("Membre.findAll").getResultList();
                
                for (Membre m : membres) {
                    System.out.println(
                        "   - " + m.getNomComplet() + 
                        " | " + m.getStatut().getLibelle() + 
                        " | " + (m.getGrade() != null ? m.getGrade() : "N/A") +
                        " | Équipe: " + (m.getEquipeActuelle() != null ? 
                                        m.getEquipeActuelle().getNom() : "Sans équipe")
                    );
                }
                
                // ========== MEMBRES PAR ÉQUIPE ==========
                if (totalEquipes > 0) {
                    System.out.println("\n📋 Membres par équipe :");
                    
                    @SuppressWarnings("unchecked")
                    List<Equipe> toutesEquipes = em.createQuery("SELECT e FROM Equipe e ORDER BY e.nom").getResultList();
                    
                    for (Equipe eq : toutesEquipes) {
                        @SuppressWarnings("unchecked")
                        List<Membre> membresEquipe = em.createNamedQuery("Membre.findByEquipe")
                                                        .setParameter("equipeId", eq.getId())
                                                        .getResultList();
                        
                        System.out.println("\n   📌 " + eq.getNom() + " (" + membresEquipe.size() + " membres)");
                        
                        if (!membresEquipe.isEmpty()) {
                            for (Membre m : membresEquipe) {
                                System.out.println("      - " + m.getNomComplet() + 
                                                 " (" + m.getStatut().getLibelle() + ")");
                            }
                        } else {
                            System.out.println("      (Aucun membre)");
                        }
                    }
                }
                
                // ========== TEST MÉTHODES MÉTIER ==========
                System.out.println("\n📌 Test des méthodes métier (premier membre) :");
                
                Membre premierMembre = membres.get(0);
                System.out.println("   Membre testé : " + premierMembre.getNomComplet());
                System.out.println("   Peut modifier profil : " + premierMembre.peutModifierProfil());
                System.out.println("   Accès modules internes : " + premierMembre.aAccesModulesInternes());
                System.out.println("   Est retraité : " + premierMembre.estRetraite());
                System.out.println("   Est ancien membre : " + premierMembre.estAncienMembre());
                System.out.println("   Initiales : " + premierMembre.getInitiales());
                
                if (premierMembre.getDateNaissance() != null) {
                    System.out.println("   Âge : " + premierMembre.getAge() + " ans");
                }
                if (premierMembre.getDateAffiliation() != null) {
                    System.out.println("   Ancienneté LIAS : " + premierMembre.getAnciennete() + " ans");
                }
                
                System.out.println("\n═══════════════════════════════════════");
                System.out.println("✅ TOUS LES TESTS RÉUSSIS !");
                System.out.println("═══════════════════════════════════════");
                
            } else {
                System.out.println("\n⚠️  Aucun membre trouvé dans la base.");
                System.out.println("💡 Exécutez le script data.sql pour insérer des données.");
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ ERREUR : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            JPAUtil.close();
        }
    }
}