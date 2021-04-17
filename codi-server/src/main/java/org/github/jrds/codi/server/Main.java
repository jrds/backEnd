

package org.github.jrds.codi.server;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.github.jrds.codi.core.messages.MessageSocket;
import org.github.jrds.codi.core.persistence.PersistenceServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.io.*;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.Objects;
import java.util.ServiceLoader;

public class Main
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Server server;

    public void start()
    {
        server = new Server();
        server.setRequestLog(new CustomRequestLog(s -> LOGGER.info(s), CustomRequestLog.EXTENDED_NCSA_FORMAT));

        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);

        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());

        File tempJks;
        try
        {
            InputStream stream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("codi.jrds.jks"), "Cannot find resource codi.jrds.jks");
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);

            tempJks = Files.createTempFile("codi-", "jks").toFile();
            tempJks.deleteOnExit();
            OutputStream outStream = new FileOutputStream(tempJks);
            outStream.write(buffer);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(tempJks.getAbsolutePath());
        sslContextFactory.setKeyStorePassword("CodiSecret");

        ServerConnector wsConnector = new ServerConnector(server);
        wsConnector.setHost("0.0.0.0");
        wsConnector.setPort(8080);
        server.addConnector(wsConnector);

        ServerConnector wssConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory,
                        HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));

        wssConnector.setHost("0.0.0.0");
        wssConnector.setPort(8443);
        server.addConnector(wssConnector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setSecurityHandler(auth());
        context.setContextPath("/");
        context.addFilter(new FilterHolder(new AuthorisationFilter(this)), "/lesson/*", EnumSet.allOf(DispatcherType.class));
        server.setHandler(context);


        WebSocketServerContainerInitializer.configure(context, (servletContext, wsContainer) -> {
            wsContainer.setDefaultMaxTextMessageBufferSize(65535);
            wsContainer.setDefaultMaxSessionIdleTimeout(60 * 60 * 1000);
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
        l.setUserStore(ServiceLoader.load(PersistenceServices.class).findFirst().orElseThrow().getUsersStore().getAuthUserStore());
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

    public static void main(String[] args)
    {
        Main server = new Main();
        server.start();
    }
}
