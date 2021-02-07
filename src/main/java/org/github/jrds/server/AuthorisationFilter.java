package org.github.jrds.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class AuthorisationFilter implements Filter {
   
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String user = ((HttpServletRequest)request).getUserPrincipal().getName();
        String[] urlInfo = ((HttpServletRequest) request).getRequestURI().split("/");
        String lessonId = urlInfo[(urlInfo.length-1)];
       
        if(Main.lessonStore.getLessonStore().containsKey(lessonId)){
            if(Main.lessonStore.getLesson(lessonId).canConnect(user)){
                //carry on with the request
                chain.doFilter(request, response);   
            }
            else {
                ((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);
            }
            //switched the if and else logic since previous commit 
        }
        else {
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

     @Override
    public void destroy() {
    }

    
    
}
