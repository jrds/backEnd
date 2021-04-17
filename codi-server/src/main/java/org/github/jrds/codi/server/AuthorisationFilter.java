package org.github.jrds.codi.server;

import org.github.jrds.codi.core.persistence.PersistenceServices;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ServiceLoader;


public class AuthorisationFilter implements Filter
{

    private final Main server;
    private final PersistenceServices persistenceServices;

    public AuthorisationFilter(Main server)
    {
        this.server = server;
        this.persistenceServices = ServiceLoader.load(PersistenceServices.class).findFirst().orElseThrow();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        String user = ((HttpServletRequest) request).getUserPrincipal().getName();
        String[] urlInfo = ((HttpServletRequest) request).getRequestURI().split("/");
        String lessonId = urlInfo[(urlInfo.length - 1)];

        if (persistenceServices.getLessonStructureStore().getLessonStructure(lessonId) != null)
        {
            if (persistenceServices.getLessonStructureStore().getLessonStructure(lessonId).canConnect(persistenceServices.getUsersStore().getUser(user)))
            {
                //carry on with the request
                chain.doFilter(request, response);
            }
            else
            {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
            }
            //switched the if and else logic since previous commit 
        }
        else
        {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void destroy()
    {
    }


}
