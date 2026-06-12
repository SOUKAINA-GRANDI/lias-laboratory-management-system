package ma.lias.app.dao.impl;

import java.util.List;

import ma.lias.app.entity.Publication;

public class PublicationDAO extends GenericDAOImpl<Publication, Long> {

    public List<Publication> findByAnnee(int annee) {
        return getEM().createNamedQuery("Publication.findByAnnee", Publication.class)
                .setParameter("annee", annee)
                .getResultList();
    }

    public List<Publication> findByMembre(Long membreId) {
        return getEM().createNamedQuery("Publication.findByMembre", Publication.class)
                .setParameter("membreId", membreId)
                .getResultList();
    }

    public List<Publication> findByEquipe(Long equipeId) {
        return getEM().createNamedQuery("Publication.findByEquipe", Publication.class)
                .setParameter("equipeId", equipeId)
                .getResultList();
    }

    public List<Publication> findByType(Publication.TypePublication type) {
        return getEM().createNamedQuery("Publication.findByType", Publication.class)
                .setParameter("type", type)
                .getResultList();
    }

    public List<Publication> rechercher(String mot) {
        String m = "%" + mot.toLowerCase() + "%";
        return getEM().createQuery(
                "SELECT p FROM Publication p WHERE " +
                "LOWER(p.titre) LIKE :mot OR " +
                "LOWER(p.motsCles) LIKE :mot OR " +
                "LOWER(p.revue) LIKE :mot", Publication.class)
                .setParameter("mot", m)
                .getResultList();
    }
}