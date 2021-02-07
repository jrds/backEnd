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

        // TODO - QUESTION = YES (test to confirm) - should I have a layer of if() checking if the lesson is present in lesson store?
        // in the else if doesn't connect return a different error - e.g. 400 - Bad Request
        if(Main.lessonStore.getLesson(lessonId).canConnect(user)){
            //carry on with the request
            chain.doFilter(request, response);   
        }
        else {
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        //switched the if and else logic since previous commit 
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

     @Override
    public void destroy() {
    }

    
    
}
