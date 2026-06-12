package ma.lias.app.filter;

import ma.lias.app.entity.Utilisateur;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtre spécifique aux pages admin
 */
@WebFilter("/admin/*")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession         session  = request.getSession(false);

        Utilisateur u = session != null
                ? (Utilisateur) session.getAttribute("utilisateurConnecte")
                : null;

        if (u == null || !u.estAdmin()) {
            response.sendRedirect(request.getContextPath() + "/accesRefuse.xhtml");
            return;
        }

        chain.doFilter(req, res);
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}