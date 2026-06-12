package ma.lias.app.service.impl;

import ma.lias.app.dao.impl.GenericDAOImpl;
import ma.lias.app.dao.impl.PublicationDAO;
import ma.lias.app.entity.Membre;
import ma.lias.app.entity.Publication;

import java.util.List;

public class PublicationService extends GenericServiceImpl<Publication, Long> {

    private final PublicationDAO publicationDAO = new PublicationDAO();

    @Override
    protected GenericDAOImpl<Publication, Long> getDAO() {
        return publicationDAO;
    }

    public List<Publication> findByAnnee(int annee) {
        return publicationDAO.findByAnnee(annee);
    }

    public List<Publication> findByMembre(Long membreId) {
        return publicationDAO.findByMembre(membreId);
    }

    public List<Publication> findByEquipe(Long equipeId) {
        return publicationDAO.findByEquipe(equipeId);
    }

    public List<Publication> findByType(Publication.TypePublication type) {
        return publicationDAO.findByType(type);
    }

    public List<Publication> rechercher(String mot) {
        return publicationDAO.rechercher(mot);
    }

    public void ajouterAuteur(Long publicationId, Membre membre) {
        publicationDAO.findById(publicationId).ifPresent(p -> {
            p.ajouterAuteur(membre);
            publicationDAO.update(p);
        });
    }
}