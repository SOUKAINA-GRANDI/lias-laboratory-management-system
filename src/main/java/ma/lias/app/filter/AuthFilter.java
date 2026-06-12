package ma.lias.app.filter;

import ma.lias.app.entity.Utilisateur;
import ma.lias.app.enums.TypeUtilisateur;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtre de sécurité principal
 * Protège toutes les pages selon le type d'utilisateur
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    // Pages accessibles sans authentification
    private static final String[] PAGES_PUBLIQUES = {
        "/index.xhtml",
        "/login.xhtml",
        "/public/",
        "/javax.faces.resource/"
    };

    // Pages réservées admin uniquement
    private static final String[] PAGES_ADMIN = {
        "/admin/"
    };

    // Pages réservées directeur + admin
    private static final String[] PAGES_DIRECTEUR = {
        "/directeur/",
        "/demandes/"
    };

    // Pages réservées membres permanents + directeur + admin
    private static final String[] PAGES_MEMBRES = {
        "/membre/",
        "/evenements/",
        "/documents/",
        "/materiels/",
        "/conventions/",
        "/pvs/"
    };

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession         session  = request.getSession(false);

        String uri = request.getRequestURI()
                .substring(request.getContextPath().length());

        // ── 1. Pages publiques → laisser passer
        if (estPagePublique(uri)) {
            chain.doFilter(req, res);
            return;
        }

        // ── 2. Pas de session → redirection login
        Utilisateur utilisateur = session != null
                ? (Utilisateur) session.getAttribute("utilisateurConnecte")
                : null;

        if (utilisateur == null) {
            response.sendRedirect(request.getContextPath() + "/login.xhtml");
            return;
        }

        // ── 3. Vérification droits admin
        if (estPageAdmin(uri) && !utilisateur.estAdmin()) {
            response.sendRedirect(request.getContextPath() + "/accesRefuse.xhtml");
            return;
        }

        // ── 4. Vérification droits directeur
        if (estPageDirecteur(uri) && !peutAccederDirecteur(utilisateur)) {
            response.sendRedirect(request.getContextPath() + "/accesRefuse.xhtml");
            return;
        }

        // ── 5. Vérification droits membres internes
        if (estPageMembre(uri) && !peutAccederMembre(utilisateur)) {
            response.sendRedirect(request.getContextPath() + "/accesRefuse.xhtml");
            return;
        }

        // ── 6. Tout est ok → continuer
        chain.doFilter(req, res);
    }

    // ========== MÉTHODES UTILITAIRES ==========

    private boolean estPagePublique(String uri) {
        for (String page : PAGES_PUBLIQUES) {
            if (uri.startsWith(page) || uri.equals(page)) return true;
        }
        return false;
    }

    private boolean estPageAdmin(String uri) {
        for (String page : PAGES_ADMIN) {
            if (uri.startsWith(page)) return true;
        }
        return false;
    }

    private boolean estPageDirecteur(String uri) {
        for (String page : PAGES_DIRECTEUR) {
            if (uri.startsWith(page)) return true;
        }
        return false;
    }

    private boolean estPageMembre(String uri) {
        for (String page : PAGES_MEMBRES) {
            if (uri.startsWith(page)) return true;
        }
        return false;
    }

    private boolean peutAccederDirecteur(Utilisateur u) {
        return u.getTypeUtilisateur() == TypeUtilisateur.DIRECTEUR
            || u.getTypeUtilisateur() == TypeUtilisateur.ADMINISTRATEUR;
    }

    private boolean peutAccederMembre(Utilisateur u) {
        TypeUtilisateur t = u.getTypeUtilisateur();
        return t == TypeUtilisateur.MEMBRE_PERMANENT
            || t == TypeUtilisateur.DIRECTEUR
            || t == TypeUtilisateur.ADMINISTRATEUR;
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}