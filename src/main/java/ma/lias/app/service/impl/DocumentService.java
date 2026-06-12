package ma.lias.app.service.impl;

import ma.lias.app.dao.impl.DocumentDAO;
import ma.lias.app.dao.impl.GenericDAOImpl;
import ma.lias.app.entity.Document;

import java.time.LocalDateTime;
import java.util.List;

public class DocumentService extends GenericServiceImpl<Document, Long> {

    private final DocumentDAO documentDAO = new DocumentDAO();

    @Override
    protected GenericDAOImpl<Document, Long> getDAO() {
        return documentDAO;
    }

    public List<Document> findByType(Document.TypeDocument type) {
        return documentDAO.findByType(type);
    }

    public List<Document> findByEvenement(Long evenementId) {
        return documentDAO.findByEvenement(evenementId);
    }

    public List<Document> findByConvention(Long conventionId) {
        return documentDAO.findByConvention(conventionId);
    }

    public List<Document> findByProcesVerbal(Long pvId) {
        return documentDAO.findByProcesVerbal(pvId);
    }

    public List<Document> findPublics() {
        return documentDAO.findPublics();
    }

    public List<Document> rechercher(String mot) {
        if (mot == null || mot.trim().isEmpty()) return findAll();
        return documentDAO.rechercher(mot.trim());
    }

    public List<Document> findByPeriode(LocalDateTime debut, LocalDateTime fin) {
        return documentDAO.findByPeriode(debut, fin);
    }
}