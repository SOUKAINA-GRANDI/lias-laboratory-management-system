package ma.lias.app.dao.impl;

import ma.lias.app.entity.Document;

import java.time.LocalDateTime;
import java.util.List;

public class DocumentDAO extends GenericDAOImpl<Document, Long> {

    public List<Document> findByType(Document.TypeDocument type) {
        return getEM().createNamedQuery("Document.findByType", Document.class)
                .setParameter("type", type)
                .getResultList();
    }

    public List<Document> findByEvenement(Long evenementId) {
        return getEM().createNamedQuery("Document.findByEvenement", Document.class)
                .setParameter("evenementId", evenementId)
                .getResultList();
    }

    public List<Document> findByConvention(Long conventionId) {
        return getEM().createQuery(
                "SELECT d FROM Document d WHERE d.convention.id = :id ORDER BY d.dateUpload DESC",
                Document.class)
                .setParameter("id", conventionId)
                .getResultList();
    }

    public List<Document> findByProcesVerbal(Long pvId) {
        return getEM().createQuery(
                "SELECT d FROM Document d WHERE d.procesVerbal.id = :id ORDER BY d.dateUpload DESC",
                Document.class)
                .setParameter("id", pvId)
                .getResultList();
    }

    public List<Document> findPublics() {
        return getEM().createQuery(
                "SELECT d FROM Document d WHERE d.publicVisible = true ORDER BY d.dateUpload DESC",
                Document.class)
                .getResultList();
    }

    public List<Document> rechercher(String mot) {
        return getEM().createNamedQuery("Document.recherche", Document.class)
                .setParameter("mot", "%" + mot.toLowerCase() + "%")
                .getResultList();
    }

    public List<Document> findByPeriode(LocalDateTime debut, LocalDateTime fin) {
        return getEM().createNamedQuery("Document.findByAnnee", Document.class)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .getResultList();
    }
}