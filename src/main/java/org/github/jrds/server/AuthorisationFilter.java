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
        if(user.equals("Learner 99")){
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        else {
            //carry on with the request
            chain.doFilter(request, response);
        }
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

     @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    
    
}
