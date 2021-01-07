package org.github.jrds.server;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

public class Main {

    private Server server;

    public void start() {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setSecurityHandler(auth());
        context.setContextPath("/");
        server.setHandler(context);

        WebSocketServerContainerInitializer.configure(context, (servletContext, wsContainer) -> {
            wsContainer.setDefaultMaxTextMessageBufferSize(65535);
            wsContainer.addEndpoint(MessageSocket.class);
        });

        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SecurityHandler auth() {
        UserStore userStore = new UserStore();
        userStore.addUser("Learner 1", new Password("pw"), new String[] { "user"});
        userStore.addUser("Educator", new Password("pw"), new String[] { "user"});
        
        HashLoginService l = new HashLoginService();
        l.setUserStore(userStore);
        l.setName("realm");
        
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[]{"user"});
        constraint.setAuthenticate(true);
         
        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec("/*");
        
        ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
        csh.setAuthenticator(new BasicAuthenticator());
        csh.setRealmName("myrealm");
        csh.addConstraintMapping(cm);
        csh.setLoginService(l);
        
        return csh;
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}