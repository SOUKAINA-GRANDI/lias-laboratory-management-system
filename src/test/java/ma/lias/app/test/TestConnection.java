package ma.lias.app.test;

import java.util.List;
import javax.persistence.EntityManager;
import ma.lias.app.entity.Equipe;
import ma.lias.app.util.JPAUtil;

/**
 * Test de connexion et lecture des équipes
 * NE FAIT PAS D'INSERTION - Les données sont ajoutées via SQL
 */
public class TestConnection {

    public static void main(String[] args) {

        System.out.println("═══════════════════════════════════════");
        System.out.println("🔄 TEST DE CONNEXION À LA BASE DE DONNÉES");
        System.out.println("═══════════════════════════════════════");

        EntityManager em = null;

        try {

            // Vérification persistence.xml
            System.out.println("\n📌 Vérification persistence.xml...");
            System.out.println(
                Thread.currentThread()
                      .getContextClassLoader()
                      .getResource("META-INF/persistence.xml")
            );

            // Création EntityManager
            System.out.println("\n📌 Création de l'EntityManager...");
            em = JPAUtil.getEntityManager();

            if (em != null && em.isOpen()) {

                System.out.println("✅ EntityManager créé avec succès");

                // Test SQL simple
                System.out.println("\n📌 Test requête SQL...");
                Object result = em.createNativeQuery("SELECT 1").getSingleResult();
                System.out.println("✅ Requête exécutée - Résultat : " + result);

                // Base active
                System.out.println("\n📌 Base active...");
                String dbName = (String) em.createNativeQuery("SELECT DATABASE()").getSingleResult();
                System.out.println("✅ Base utilisée : " + dbName);

                // Tables existantes
                System.out.println("\n📌 Liste des tables...");
                @SuppressWarnings("unchecked")
                List<Object> tables = em.createNativeQuery("SHOW TABLES").getResultList();

                if (tables.isEmpty()) {
                    System.out.println("ℹ️  Aucune table trouvée");
                } else {
                    System.out.println("📋 Tables trouvées :");
                    for (Object table : tables) {
                        System.out.println("   ✅ " + table);
                    }
                }

                // ========== LECTURE DES ÉQUIPES ==========
                System.out.println("\n📌 Lecture des équipes depuis la base...");

                Long countEquipes = (Long) em.createQuery("SELECT COUNT(e) FROM Equipe e").getSingleResult();
                System.out.println("✅ Nombre d'équipes : " + countEquipes);

                if (countEquipes > 0) {
                    System.out.println("\n📋 Liste des équipes :");
                    @SuppressWarnings("unchecked")
                    List<Equipe> equipes = em.createQuery("SELECT e FROM Equipe e ORDER BY e.nom").getResultList();
                    
                    for (Equipe eq : equipes) {
                        System.out.println("   - " + eq.getNom() + 
                                         (eq.getAcronyme() != null ? " (" + eq.getAcronyme() + ")" : "") +
                                         " | Créée le : " + eq.getDateCreation() +
                                         " | Active : " + (eq.getActive() ? "✅" : "❌"));
                    }
                } else {
                    System.out.println("⚠️  Aucune équipe trouvée.");
                    System.out.println("💡 Exécutez le script data.sql pour insérer des données.");
                }

                System.out.println("\n═══════════════════════════════════════");
                System.out.println("✅ TEST DE CONNEXION RÉUSSI !");
                System.out.println("═══════════════════════════════════════");

            } else {
                System.out.println("❌ EntityManager non créé");
            }

        } catch (Exception e) {
            System.out.println("\n═══════════════════════════════════════");
            System.out.println("❌ ERREUR");
            System.out.println("═══════════════════════════════════════");
            e.printStackTrace();

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                System.out.println("\n🔒 EntityManager fermé");
            }
            JPAUtil.close();
        }
    }
}