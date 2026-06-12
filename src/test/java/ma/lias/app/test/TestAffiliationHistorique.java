package ma.lias.app.test;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import ma.lias.app.entity.AffiliationHistorique;
import ma.lias.app.entity.Membre;
import ma.lias.app.util.JPAUtil;

public class TestAffiliationHistorique {

    public static void main(String[] args) {
        
        System.out.println("═══════════════════════════════════════");
        System.out.println("📜 TEST ENTITÉ AFFILIATION_HISTORIQUE");
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
            
            // ========== COMPTER LES AFFILIATIONS ==========
            System.out.println("\n📊 Statistiques :");
            
            Long totalAffiliations = (Long) em.createQuery(
                "SELECT COUNT(a) FROM AffiliationHistorique a"
            ).getSingleResult();
            System.out.println("   Total affiliations : " + totalAffiliations);
            
            if (totalAffiliations > 0) {
                
                Long actives = (Long) em.createQuery(
                    "SELECT COUNT(a) FROM AffiliationHistorique a WHERE a.periodeActive = true"
                ).getSingleResult();
                System.out.println("   Affiliations actives : " + actives);
                
                Long terminees = (Long) em.createQuery(
                    "SELECT COUNT(a) FROM AffiliationHistorique a WHERE a.periodeActive = false"
                ).getSingleResult();
                System.out.println("   Affiliations terminées : " + terminees);
                
                // ========== LISTER LES AFFILIATIONS ==========
                System.out.println("\n📋 Liste des affiliations :");
                
                @SuppressWarnings("unchecked")
                List<AffiliationHistorique> affiliations = em.createQuery(
                    "SELECT a FROM AffiliationHistorique a ORDER BY a.membre.nom, a.dateDebut DESC"
                ).getResultList();
                
                for (AffiliationHistorique a : affiliations) {
                    System.out.println(
                        "   - " + (a.getMembre() != null ? a.getMembre().getNomComplet() : "?") + 
                        " | " + a.getDescriptionPeriode() +
                        " | " + (a.getStatutPendantPeriode() != null ? a.getStatutPendantPeriode().getLibelle() : "?") +
                        " | " + (a.estEnCours() ? "✅ En cours" : "❌ Terminée")
                    );
                }
                
                // ========== AFFILIATIONS PAR MEMBRE ==========
                System.out.println("\n📊 Membres avec plusieurs affiliations :");
                
                @SuppressWarnings("unchecked")
                List<Object[]> membresPlusieursAffiliations = em.createQuery(
                    "SELECT m, COUNT(a) FROM AffiliationHistorique a " +
                    "JOIN a.membre m " +
                    "GROUP BY m " +
                    "HAVING COUNT(a) > 1 " +
                    "ORDER BY COUNT(a) DESC"
                ).getResultList();
                
                if (membresPlusieursAffiliations.isEmpty()) {
                    System.out.println("   Aucun membre avec plusieurs affiliations pour le moment.");
                } else {
                    for (Object[] result : membresPlusieursAffiliations) {
                        Membre m = (Membre) result[0];
                        Long count = (Long) result[1];
                        System.out.println("   - " + m.getNomComplet() + " : " + count + " affiliation(s)");
                        
                        // Afficher le détail
                        @SuppressWarnings("unchecked")
                        List<AffiliationHistorique> affsMembre = em.createNamedQuery("AffiliationHistorique.findByMembre")
                                                                    .setParameter("membreId", m.getId())
                                                                    .getResultList();
                        for (AffiliationHistorique aff : affsMembre) {
                            System.out.println("      → " + aff.getDescriptionPeriode() + 
                                             " (" + (aff.estEnCours() ? "En cours" : aff.getMotifDepart()) + ")");
                        }
                    }
                }
                
                // ========== TEST MÉTHODES MÉTIER ==========
                if (!affiliations.isEmpty()) {
                    System.out.println("\n📌 Test des méthodes métier :");
                    
                    AffiliationHistorique premiere = affiliations.get(0);
                    System.out.println("   Affiliation testée : " + premiere);
                    System.out.println("   Durée en années : " + premiere.getDureeEnAnnees());
                    System.out.println("   Durée en mois : " + premiere.getDureeEnMois());
                    System.out.println("   Est en cours : " + premiere.estEnCours());
                    System.out.println("   Est terminée : " + premiere.estTerminee());
                    System.out.println("   Contient aujourd'hui : " + premiere.contientDate(LocalDate.now()));
                }
                
                System.out.println("\n═══════════════════════════════════════");
                System.out.println("✅ TOUS LES TESTS RÉUSSIS !");
                System.out.println("═══════════════════════════════════════");
                
            } else {
                System.out.println("\n⚠️  Aucune affiliation trouvée.");
                System.out.println("💡 Exécutez le script SQL pour créer des affiliations de test.");
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