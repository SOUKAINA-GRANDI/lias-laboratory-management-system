package ma.lias.app.test;

import java.util.List;

import javax.persistence.EntityManager;

import ma.lias.app.entity.Utilisateur;
import ma.lias.app.enums.TypeUtilisateur;
import ma.lias.app.util.JPAUtil;

public class TestUtilisateur {

    public static void main(String[] args) {
        
        System.out.println("═══════════════════════════════════════");
        System.out.println("🔐 TEST ENTITÉ UTILISATEUR");
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
            
            // ========== COMPTER LES UTILISATEURS ==========
            System.out.println("\n📊 Statistiques :");
            
            Long totalUtilisateurs = (Long) em.createQuery(
                "SELECT COUNT(u) FROM Utilisateur u"
            ).getSingleResult();
            System.out.println("   Total utilisateurs : " + totalUtilisateurs);
            
            if (totalUtilisateurs > 0) {
                
                Long actifs = (Long) em.createQuery(
                    "SELECT COUNT(u) FROM Utilisateur u WHERE u.compteActif = true"
                ).getSingleResult();
                System.out.println("   Comptes actifs : " + actifs);
                
                // ========== LISTER LES UTILISATEURS ==========
                System.out.println("\n📋 Liste des utilisateurs :");
                
                @SuppressWarnings("unchecked")
                List<Utilisateur> utilisateurs = em.createNamedQuery("Utilisateur.findAll")
                                                    .getResultList();
                
                for (Utilisateur u : utilisateurs) {
                    System.out.println(
                        "   - " + u.getUsername() + 
                        " | " + u.getTypeUtilisateur().getLibelle() +
                        " | Membre: " + (u.getMembre() != null ? u.getMembre().getNomComplet() : "Aucun") +
                        " | Actif: " + (u.getCompteActif() ? "✅" : "❌")
                    );
                }
                
                // ========== STATISTIQUES PAR TYPE ==========
                System.out.println("\n📊 Utilisateurs par type :");
                
                for (TypeUtilisateur type : TypeUtilisateur.values()) {
                    Long count = (Long) em.createNamedQuery("Utilisateur.findByType")
                                          .setParameter("type", type)
                                          .getResultList()
                                          .stream()
                                          .count();
                    if (count > 0) {
                        System.out.println("   " + type.getLibelle() + " : " + count);
                    }
                }
                
                // ========== TEST MÉTHODES MÉTIER ==========
                if (!utilisateurs.isEmpty()) {
                    System.out.println("\n📌 Test des méthodes métier :");
                    
                    Utilisateur premier = utilisateurs.get(0);
                    System.out.println("   Utilisateur testé : " + premier.getUsername());
                    System.out.println("   Nom complet : " + premier.getNomComplet());
                    System.out.println("   Est admin : " + premier.estAdmin());
                    System.out.println("   Est directeur : " + premier.estDirecteur());
                    System.out.println("   Peut valider : " + premier.peutValider());
                    System.out.println("   Est verrouillé : " + premier.estVerrouille());
                    System.out.println("   Première connexion : " + premier.getPremiereConnexion());
                    System.out.println("   Tentatives échouées : " + premier.getNombreTentativesEchouees());
                    
                    if (premier.getDerniereConnexion() != null) {
                        System.out.println("   Dernière connexion : " + premier.getDerniereConnexion());
                    }
                }
                
                System.out.println("\n═══════════════════════════════════════");
                System.out.println("✅ TOUS LES TESTS RÉUSSIS !");
                System.out.println("═══════════════════════════════════════");
                
            } else {
                System.out.println("\n⚠️  Aucun utilisateur trouvé.");
                System.out.println("💡 Exécutez le script SQL pour créer des utilisateurs de test.");
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