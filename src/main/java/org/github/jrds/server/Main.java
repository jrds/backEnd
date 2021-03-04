package org.github.jrds.server;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.DispatcherType;

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
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

public class Main {

    public static LessonStore lessonStore = new LessonStore();
    public static UsersStore usersStore = new UsersStore();
    public static AttendanceStore attendanceStore = new AttendanceStore();
    public static UserStore authUserStore = new UserStore();

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
        context.addFilter(AuthorisationFilter.class, "/lesson/*", EnumSet.allOf(DispatcherType.class));
        server.setHandler(context);


        WebSocketServerContainerInitializer.configure(context, (servletContext, wsContainer) -> {
            wsContainer.setDefaultMaxTextMessageBufferSize(65535);
            wsContainer.addEndpoint(MessageSocket.class);
        });

        try {
            fillLessonStore(); 
            fillUsersStore();  // TODO - method will evolve to fill ALL stores (user, lesson etc)
            server.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private SecurityHandler auth() {

        HashLoginService l = new HashLoginService();
        l.setUserStore(authUserStore);
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

    private void fillLessonStore(){
        Set<String> learners2905 = new HashSet<String>();
        learners2905.addAll(Arrays.asList(new String[] {"u1900", "u1901"}));  

        Set<String> learners5029 = new HashSet<String>();
        learners5029.addAll(Arrays.asList(new String[] {"u1900", "u1901", "u9999"}));  

        Lesson l1 = new Lesson("2905", "e0001", learners2905);
        Lesson l2 = new Lesson("5029", "e0001", learners5029);

        lessonStore.saveLesson(l1);
        lessonStore.saveLesson(l2);
    }; 

    private void fillUsersStore() {

        usersStore.addUser("u1900", "Jordan"); // TODO - NOTE - these match to the strings defined in application test
        usersStore.addUser("e0001", "Educator Rebecca");
        usersStore.addUser("u1901", "Savanna");
        usersStore.addUser("u9999", "Jack");
        // TODO - once the application user store is established, then it can iterate through that user store to add the users the auth user store
    }

}
