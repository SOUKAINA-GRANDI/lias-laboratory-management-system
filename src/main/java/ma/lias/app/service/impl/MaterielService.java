package ma.lias.app.service.impl;

import ma.lias.app.dao.impl.AttributionMaterielDAO;
import ma.lias.app.dao.impl.GenericDAOImpl;
import ma.lias.app.dao.impl.MaterielDAO;
import ma.lias.app.entity.Materiel;
import ma.lias.app.entity.Membre;

import java.util.List;

public class MaterielService extends GenericServiceImpl<Materiel, Long> {

    private final MaterielDAO materielDAO             = new MaterielDAO();
    private final AttributionMaterielDAO attributionDAO = new AttributionMaterielDAO();

    @Override
    protected GenericDAOImpl<Materiel, Long> getDAO() {
        return materielDAO;
    }

    public List<Materiel> findDisponibles() {
        return materielDAO.findDisponibles();
    }

    public List<Materiel> findByType(Materiel.TypeMateriel type) {
        return materielDAO.findByType(type);
    }

    public List<Materiel> findByMembre(Long membreId) {
        return materielDAO.findByMembre(membreId);
    }

    /**
     * Attribuer un matériel à un membre
     */
    public boolean attribuer(Long materielId, Membre membre, Membre validePar) {
        return materielDAO.findById(materielId).map(m -> {
            boolean ok = m.attribuerA(membre, validePar);
            if (ok) materielDAO.update(m);
            return ok;
        }).orElse(false);
    }

    /**
     * Récupérer un matériel d'un membre
     */
    public void recuperer(Long materielId, Membre membre) {
        materielDAO.findById(materielId).ifPresent(m -> {
            m.recupererDe(membre);
            materielDAO.update(m);
        });
    }

	public AttributionMaterielDAO getAttributionDAO() {
		return attributionDAO;
	}
}