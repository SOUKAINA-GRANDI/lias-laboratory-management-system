package ma.lias.app.filter;

import ma.lias.app.util.JPAUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Filtre qui ferme l'EntityManager après chaque requête
 * Pattern : Open EntityManager in View
 */
@WebFilter("/*")
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } finally {
            // Fermer l'EntityManager à la fin de chaque requête
            JPAUtil.closeEntityManager();
        }
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}