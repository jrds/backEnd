package org.github.jrds.server;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class AuthorisationFilter implements Filter
{

    private final Main server;

    public AuthorisationFilter(Main server)
    {
        this.server = server;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        String user = ((HttpServletRequest) request).getUserPrincipal().getName();
        String[] urlInfo = ((HttpServletRequest) request).getRequestURI().split("/");
        String lessonId = urlInfo[(urlInfo.length - 1)];

        if (server.lessonStore.getLesson(lessonId) != null)
        {
            if (server.lessonStore.getLesson(lessonId).canConnect(server.usersStore.getUser(user)))
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
