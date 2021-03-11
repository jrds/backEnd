package org.github.jrds.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.github.jrds.server.domain.Attendance;
import org.junit.After;
import org.junit.Before;

public abstract class ApplicationTest {
    
    protected Main server;
    protected String eduId = "e0001";
    protected String eduName = "Educator Rebecca";
    protected String l1Id = "u1900";
    protected String l1Name = "Jordan";
    protected String l2Id = "u1901";
    protected String l2Name = "Savanna";
    protected String l99Id = "u9999";
    protected String l99Name = "Jack";
    protected String lesson1 = "2905";
    protected String lesson2 = "5029";
    private List<TestClient> testClients = new ArrayList<TestClient>();

    @Before
    public void setUp() {
        server = new Main();
        server.start();
    }

    @After
    public void tearDown() {
        System.out.println("DISCONNECTING");
        while(!testClients.isEmpty()){
            disconnect(testClients.get(0));
        }
        System.out.println("STOPPING SERVER");
        server.stop();
    }

    protected TestClient connect(String id, String userName, String lessonId) {
        TestClient c = new TestClient(id, userName);
        if (!testClients.contains(c)){
            c.connect(lessonId);
            testClients.add(c);
        }
        return c;
    }

    //TODO - if time, look at how this was running prior to 11/03/21, using the if to check the status of the client
    protected void disconnect(TestClient testClient){
        try {
            testClient.disconnect();
        } catch (Exception e) {
            // Ignore assuming connection is no longer usable
        }
        testClients.remove(testClient);
    }

    public static Throwable findRootCause(Throwable throwable) {
        Objects.requireNonNull(throwable);
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    protected Attendance getAttendance(String userId, String lessonId){
        return server.attendanceStore.getAllAttendances().stream()
            .filter(a -> a.getUser().getId().equals(userId))
            .filter(a -> a.getLesson().getId().equals(lessonId))
            .findFirst()
            .orElse(null);
    }

}
