package org.github.jrds.codi.server.testing;

import org.github.jrds.codi.core.domain.Attendance;
import org.github.jrds.codi.core.messages.MessagingContext;
import org.github.jrds.codi.core.persistence.PersistenceServices;
import org.github.jrds.codi.core.persistence.StaticPersistenceServices;
import org.github.jrds.codi.server.Main;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

public abstract class ApplicationTest
{
    protected static final PersistenceServices persistenceServices = ServiceLoader.load(PersistenceServices.class).findFirst().orElseThrow();

    protected Main server;

    protected String eduId = "e0001";
    protected String l1Id = "u1900";
    protected String l2Id = "u1901";
    protected String l99Id = "u9999";
    protected String lesson1 = "2905";
    protected String lesson2 = "5029";
    private final List<TestClient> testClients = new ArrayList<>();

    @Before
    public void setUp()
    {
        StaticPersistenceServices.reset();
        MessagingContext.reset();
        server = new Main();
        server.start();
    }

    @After
    public void tearDown()
    {
        System.out.println("DISCONNECTING");
        while (!testClients.isEmpty())
        {
            disconnect(testClients.get(0));
        }
        System.out.println("STOPPING SERVER");
        server.stop();
    }

    protected TestClient connect(String id, String lessonId)
    {
        TestClient c = new TestClient(id);
        if (!testClients.contains(c))
        {
            c.connect(lessonId);
            testClients.add(c);
        }
        return c;
    }

    //TODO - if time, look at how this was running prior to 11/03/21, using the if to check the status of the client
    protected void disconnect(TestClient testClient)
    {
        try
        {
            testClient.disconnect();
        }
        catch (Exception e)
        {
            // Ignore assuming connection is no longer usable
        }
        testClients.remove(testClient);
    }

    public static Throwable findRootCause(Throwable throwable)
    {
        Objects.requireNonNull(throwable);
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause)
        {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    protected Attendance getAttendance(String userId, String lessonId)
    {
        try
        {
            return persistenceServices.getActiveLessonStore().getActiveLesson(lessonId).getActiveLessonAttendances().stream()
                    .filter(a -> a.getUser().getId().equals(userId))
                    .findFirst()
                    .orElse(null);
        }
        catch (NullPointerException e){
            return null;
            // TODO - Review:
            // had to put in the catch to get unregisteredLessonAttendanceNotRecorded passing
        }
    }

}
