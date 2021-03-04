package org.github.jrds.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        while(!testClients.isEmpty()){
            disconnect(testClients.get(0));
        }
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

    protected void disconnect(TestClient testClient){
        testClient.disconnect();
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

}
