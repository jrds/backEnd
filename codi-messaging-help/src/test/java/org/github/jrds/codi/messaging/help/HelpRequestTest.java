package org.github.jrds.codi.messaging.help;

import org.github.jrds.codi.core.domain.HelpRequest;
import org.github.jrds.codi.core.domain.Status;
import org.github.jrds.codi.core.messages.FailureResponse;
import org.github.jrds.codi.core.messages.Response;
import org.github.jrds.codi.core.messages.SuccessResponse;
import org.github.jrds.codi.server.testing.ApplicationTest;
import org.github.jrds.codi.server.testing.ClientWebSocket;
import org.github.jrds.codi.server.testing.TestClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HelpRequestTest extends ApplicationTest
{
    @BeforeClass
    public static void registerMessageSubtypes()
    {
        ClientWebSocket.registerMessageSubtype(CancelHelpRequest.class);
        ClientWebSocket.registerMessageSubtype(NewHelpRequest.class);
        ClientWebSocket.registerMessageSubtype(UpdateHelpStatusRequest.class);
        ClientWebSocket.registerMessageSubtype(OpenHelpRequestsInfo.class);
    }

    @Test
    public void learnerRequestHelpEducatorReceivesRequest() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, lesson1);
        TestClient c2 = connect(l1Id, lesson1);

        Response state = requestHelp(c2).get(10, TimeUnit.SECONDS);
        Assert.assertTrue(state instanceof SuccessResponse);

        // wait for c1 to have received the message
        c1.getMessageReceived();
        c1.getMessageReceived();
        List<HelpRequest> helpRequests = getOpenHelpRequests(lesson1);
        Assert.assertEquals(1,helpRequests.size());
        Assert.assertEquals(c2.getId(), helpRequests.get(0).getLearner().getId());
    }

    @Test
    public void learnerCancelsAHelpRequest() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, lesson1);
        TestClient c2 = connect(l1Id, lesson1);

        requestHelp(c2).get(10, TimeUnit.SECONDS);

        // wait for c1 to have received the message
        c1.getMessageReceived();
        Assert.assertEquals(1, getOpenHelpRequests(lesson1).size());

        Response state = cancelHelpRequest(c2).get(10, TimeUnit.SECONDS);
        Assert.assertTrue(state instanceof SuccessResponse);

        c1.getMessageReceived();
        Assert.assertEquals(0, getOpenHelpRequests(lesson1).size());
    }

    @Test
    public void helpRequestsAreReceivedInTheCorrectOrder() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, lesson1);
        TestClient c2 = connect(l1Id, lesson1);
        TestClient c3 = connect(l2Id, lesson1);

        requestHelp(c3).get(10, TimeUnit.SECONDS);
        requestHelp(c2).get(10, TimeUnit.SECONDS);

        c1.getMessageReceived();
        c1.getMessageReceived();

        List<HelpRequest> helpRequests = getOpenHelpRequests(lesson1);
        Assert.assertEquals(2,helpRequests.size());
        Assert.assertEquals(l2Id,helpRequests.get(0).getLearner().getId());
        Assert.assertEquals(l1Id,helpRequests.get(1).getLearner().getId());
    }

    @Test
    public void learnerCantSubmit2ActiveHelpRequests() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, lesson1);
        TestClient c2 = connect(l1Id, lesson1);
        TestClient c3 = connect(l2Id, lesson1);

        requestHelp(c2).get(10, TimeUnit.SECONDS);
        requestHelp(c3).get(10, TimeUnit.SECONDS);

        c1.getMessageReceived();
        c1.getMessageReceived();
        Assert.assertEquals(2,getOpenHelpRequests(lesson1).size());

        Response state = requestHelp(c2).get(10, TimeUnit.SECONDS);
        Assert.assertTrue(state instanceof FailureResponse);
        Assert.assertEquals("Learners cannot create more than one active help request",((FailureResponse) state).getFailureReason());

        c1.getMessageReceived();
        Assert.assertEquals(2,getOpenHelpRequests(lesson1).size());

    }

    @Test
    public void educatorMarksHelpRequestAsInProgress() throws InterruptedException, ExecutionException, TimeoutException
    {

        TestClient c1 = connect(eduId, lesson1);
        TestClient c2 = connect(l1Id, lesson1);

        requestHelp(c2).get(10, TimeUnit.SECONDS);

        // wait for c1 to have received the message
        c1.getMessageReceived(OpenHelpRequestsInfo.class);

        HelpRequest requestToChangeStatus = persistenceServices.getActiveLessonStore().getActiveLesson("2905").getOpenHelpRequests().get(l1Id);

        Assert.assertEquals(Status.NEW,requestToChangeStatus.getStatus()); //TODO maybe should be string not Status?


        updateHelpRequest(c1, requestToChangeStatus, Status.IN_PROGRESS);

        c1.getMessageReceived(OpenHelpRequestsInfo.class);

        HelpRequest changedRequest = persistenceServices.getActiveLessonStore().getActiveLesson("2905").getOpenHelpRequests().get(l1Id);
        Assert.assertEquals(Status.IN_PROGRESS,changedRequest.getStatus());
                                                                                        // TODO store time updated
    }

    @Test
    public void educatorMarksHelpRequestAsComplete() throws InterruptedException, ExecutionException, TimeoutException
    {
        // TODO - audit in active lesson of help request closure etc.

        TestClient c1 = connect(eduId, lesson1);
        TestClient c2 = connect(l1Id, lesson1);
        TestClient c3 = connect(l2Id, lesson1);

        requestHelp(c2).get(10, TimeUnit.SECONDS);
        requestHelp(c3).get(10, TimeUnit.SECONDS);

        // wait for c1 to have received the message
        c1.getMessageReceived(OpenHelpRequestsInfo.class);
        c1.getMessageReceived(OpenHelpRequestsInfo.class);

        HelpRequest requestToChangeStatus = persistenceServices.getActiveLessonStore().getActiveLesson("2905").getOpenHelpRequests().get(c2.getId());

        Assert.assertEquals(Status.NEW,requestToChangeStatus.getStatus()); //TODO maybe should be string not Status?

        updateHelpRequest(c1, requestToChangeStatus, Status.COMPLETED);

        c1.getMessageReceived(OpenHelpRequestsInfo.class);

        Assert.assertEquals(1, getOpenHelpRequests(lesson1).size());  // TODO should I try to keep the original message id?
        Assert.assertEquals(l2Id, getOpenHelpRequests(lesson1).get(0).getLearner().getId());
        // TODO store time updated
    }

    @Test
    public void helpRequestOrderingIsNotImpactedWhenOneIsRemoved() throws InterruptedException, ExecutionException, TimeoutException
    {

        TestClient c1 = connect(eduId, lesson2);
        TestClient c2 = connect(l1Id, lesson2);
        TestClient c3 = connect(l2Id, lesson2);
        TestClient c4 = connect(l99Id, lesson2);

        requestHelp(c2).get(10, TimeUnit.SECONDS);
        requestHelp(c3).get(10, TimeUnit.SECONDS);
        requestHelp(c4).get(10, TimeUnit.SECONDS);

        c1.getMessageReceived();
        c1.getMessageReceived();
        c1.getMessageReceived();
        List<HelpRequest> helpRequests = getOpenHelpRequests(lesson2);

        Assert.assertEquals(3,helpRequests.size());

        Collections.sort(helpRequests);

        Assert.assertEquals(l1Id,helpRequests.get(0).getLearner().getId());
        Assert.assertEquals(l2Id,helpRequests.get(1).getLearner().getId());
        Assert.assertEquals(l99Id,helpRequests.get(2).getLearner().getId());

        cancelHelpRequest(c3).get(10, TimeUnit.SECONDS);

        c1.getMessageReceived();

        List<HelpRequest> helpRequests2 = getOpenHelpRequests(lesson2);
        Assert.assertEquals(2, getOpenHelpRequests(lesson2).size());

        Assert.assertEquals(l1Id,helpRequests2.get(0).getLearner().getId());
        Assert.assertEquals(l99Id,helpRequests2.get(1).getLearner().getId());
    }

    @Test
    public void helpRequestOrderingIsNotImpactedWhenStatusesAreChanged() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, lesson2);
        TestClient c2 = connect(l1Id, lesson2);
        TestClient c3 = connect(l2Id, lesson2);
        TestClient c4 = connect(l99Id, lesson2);

        requestHelp(c3).get(10, TimeUnit.SECONDS);
        requestHelp(c2).get(10, TimeUnit.SECONDS);
        requestHelp(c4).get(10, TimeUnit.SECONDS);

        c1.getMessageReceived();
        c1.getMessageReceived();
        c1.getMessageReceived();
        List<HelpRequest> helpRequests = getOpenHelpRequests(lesson2);
        Assert.assertEquals(3,helpRequests.size());

        //Collections.sort(helpRequests); //Todo - why does this fail, when included, but works for the test above **********************************

        Assert.assertEquals(l2Id,helpRequests.get(0).getLearner().getId());
        Assert.assertEquals(l1Id,helpRequests.get(1).getLearner().getId());
        Assert.assertEquals(l99Id,helpRequests.get(2).getLearner().getId());

        updateHelpRequest(c1, helpRequests.get(1),Status.IN_PROGRESS);
        c1.getMessageReceived();

        List<HelpRequest> helpRequests2 = getOpenHelpRequests(lesson2);
        Assert.assertEquals(3, getOpenHelpRequests(lesson2).size());

        Assert.assertEquals(l2Id,helpRequests2.get(0).getLearner().getId());
        Assert.assertEquals(l1Id,helpRequests2.get(1).getLearner().getId());
        Assert.assertEquals(l99Id,helpRequests2.get(2).getLearner().getId());

    }

    private Future<Response> requestHelp(TestClient client)
    {
        NewHelpRequest request = new NewHelpRequest(client.getId());
        return client.sendRequest(request);
    }

    private Future<Response> cancelHelpRequest(TestClient client)
    {
        CancelHelpRequest request = new CancelHelpRequest(client.getId());
        return client.sendRequest(request);
    }

    private Future<Response> updateHelpRequest(TestClient client, HelpRequest helpRequestToUpdate, Status newStatus)
    {
        UpdateHelpStatusRequest request = new UpdateHelpStatusRequest(client.getId(), helpRequestToUpdate.getLearner().getId(), newStatus);
        return client.sendRequest(request);
    }

    private List<HelpRequest> getOpenHelpRequests(String lessonId)
    {
        return new ArrayList<>(persistenceServices.getActiveLessonStore().getActiveLesson(lessonId).getOpenHelpRequests().values());
    }

}