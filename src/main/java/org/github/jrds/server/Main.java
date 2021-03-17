package org.github.jrds.server;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.github.jrds.server.extensions.chat.ChatMessagingExtension;
import org.github.jrds.server.extensions.help.HelpMessagingExtension;
import org.github.jrds.server.extensions.lesson.LessonMessagingExtension;
import org.github.jrds.server.messages.MessageSocket;
import org.github.jrds.server.messages.MessageStats;
import org.github.jrds.server.messages.MessagingExtension;
import org.github.jrds.server.persistence.ActiveLessonStore;
import org.github.jrds.server.persistence.AttendanceStore;
import org.github.jrds.server.persistence.LessonStructureStore;
import org.github.jrds.server.persistence.UsersStore;

import javax.servlet.DispatcherType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

public class Main
{
    public static Main defaultInstance;

    public UsersStore usersStore = new UsersStore();
    public LessonStructureStore lessonStructureStore = new LessonStructureStore(usersStore);
    public ActiveLessonStore activeLessonStore = new ActiveLessonStore(lessonStructureStore);
    public AttendanceStore attendanceStore = new AttendanceStore();

    private Server server;
    private Collection<? extends MessagingExtension> extensions;
    private MessageStats messageStats;

    public Main()
    {
        defaultInstance = this;
        extensions = Arrays.asList(
            new HelpMessagingExtension(),
            new ChatMessagingExtension(),
            new LessonMessagingExtension(this));
    }

    public void setMessageStats(MessageStats messageStats)
    {
        this.messageStats = messageStats;
    }

    public MessageStats getMessageStats()
    {
        return messageStats;
    }

    public void start()
    {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setSecurityHandler(auth());
        context.setContextPath("/");
        context.addFilter(new FilterHolder(new AuthorisationFilter(this)), "/lesson/*", EnumSet.allOf(DispatcherType.class));
        server.setHandler(context);


        WebSocketServerContainerInitializer.configure(context, (servletContext, wsContainer) -> {
            wsContainer.setDefaultMaxTextMessageBufferSize(65535);
            wsContainer.addEndpoint(MessageSocket.class);
        });

        try
        {
            server.start();

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private SecurityHandler auth()
    {

        HashLoginService l = new HashLoginService();
        l.setUserStore(usersStore.getAuthUserStore());
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

    public void stop()
    {
        try
        {
            server.stop();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Collection<? extends MessagingExtension> getMessagingExtension()
    {
        return extensions;
    }
}
