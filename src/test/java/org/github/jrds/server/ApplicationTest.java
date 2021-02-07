package org.github.jrds.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Before;

public abstract class ApplicationTest {
    
    protected Main server;
    protected String edu = "Educator";
    protected String l1 = "Learner 1";
    protected String l2 = "Learner 2";
    protected String l99 = "Learner 99";
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

    protected TestClient connect(String userName, String lessonId){
        TestClient c = new TestClient(userName);
        c.connect(lessonId);
        testClients.add(c);
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
