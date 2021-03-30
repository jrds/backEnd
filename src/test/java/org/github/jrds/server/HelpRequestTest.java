package org.github.jrds.server;

import org.github.jrds.server.domain.HelpRequest;
import org.github.jrds.server.domain.Status;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.messages.FailureResponse;
import org.github.jrds.server.messages.Response;
import org.github.jrds.server.messages.SuccessResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HelpRequestTest extends ApplicationTest
{

    @Test
    public void learnerRequestHelpEducatorReceivesRequest() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        Response state = c2.requestHelp().get(10, TimeUnit.SECONDS);
        Assert.assertTrue(state instanceof SuccessResponse);

        // wait for c1 to have received the message
        c1.getMessageReceived();
        List<HelpRequest> helpRequests = c1.getOpenHelpRequests();
        Assert.assertEquals(1,helpRequests.size());
        Assert.assertEquals(c2.getId(), helpRequests.get(0).getLearner().getId());
    }

    @Test
    public void learnerCancelsAHelpRequest() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        c2.requestHelp().get(10, TimeUnit.SECONDS);

        // wait for c1 to have received the message
        c1.getMessageReceived();
        Assert.assertEquals(1,c1.getOpenHelpRequests().size());

        Response state = c2.cancelHelpRequest().get(10, TimeUnit.SECONDS);
        Assert.assertTrue(state instanceof SuccessResponse);

        c1.getMessageReceived();
        Assert.assertEquals(0,c1.getOpenHelpRequests().size());

    }

    @Test
    public void helpRequestsAreReceivedInTheCorrectOrder() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);
        TestClient c3 = connect(l2Id, l2Name, lesson1);

        c3.requestHelp().get(10, TimeUnit.SECONDS);
        c2.requestHelp().get(10, TimeUnit.SECONDS);

        c1.getMessageReceived();
        c1.getMessageReceived();

        List<HelpRequest> helpRequests = c1.getOpenHelpRequests();
        Assert.assertEquals(2,helpRequests.size());
        Assert.assertEquals(l2Id,helpRequests.get(0).getLearner().getId());
        Assert.assertEquals(l1Id,helpRequests.get(1).getLearner().getId());
    }

    @Test
    public void learnerCantSubmit2HelpRequests() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);
        TestClient c3 = connect(l2Id, l2Name, lesson1);

        c2.requestHelp().get(10, TimeUnit.SECONDS);
        c3.requestHelp().get(10, TimeUnit.SECONDS);

        c1.getMessageReceived();
        c1.getMessageReceived();
        Assert.assertEquals(2,c1.getOpenHelpRequests().size());

        Response state = c2.requestHelp().get(10, TimeUnit.SECONDS);
        Assert.assertTrue(state instanceof FailureResponse);
        Assert.assertEquals("Learners cannot create more than one active help request",((FailureResponse) state).getFailureReason());

        c1.getMessageReceived();
        Assert.assertEquals(2,c1.getOpenHelpRequests().size());

    }

    @Test
    public void educatorMarksHelpRequestAsInProgress() throws InterruptedException, ExecutionException, TimeoutException
    {
        // TODO if time - progress note idea, for changing status.

        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        c2.requestHelp().get(10, TimeUnit.SECONDS);

        // wait for c1 to have received the message
        c1.getMessageReceived();

        HelpRequest requestToChangeStatus = Main.defaultInstance.activeLessonStore.getActiveLesson("2905").getOpenHelpRequests().get(l1Id);

        Assert.assertEquals(Status.NEW,requestToChangeStatus.getStatus()); //TODO maybe should be string not Status?


        c1.updateHelpRequest(requestToChangeStatus, Status.IN_PROGRESS);

        c1.getMessageReceived();

        HelpRequest changedRequest = Main.defaultInstance.activeLessonStore.getActiveLesson("2905").getOpenHelpRequests().get(l1Id);
        Assert.assertEquals(Status.IN_PROGRESS,changedRequest.getStatus());
                                                                                        // TODO store time updated
    }

    @Test
    public void educatorMarksHelpRequestAsComplete() throws InterruptedException, ExecutionException, TimeoutException
    {
        // TODO - audit in active lesson of help request closure etc.

        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);
        TestClient c3 = connect(l2Id, l2Name, lesson1);

        c2.requestHelp().get(10, TimeUnit.SECONDS);
        c3.requestHelp().get(10, TimeUnit.SECONDS);

        // wait for c1 to have received the message
        c1.getMessageReceived();
        c1.getMessageReceived();

        HelpRequest requestToChangeStatus = Main.defaultInstance.activeLessonStore.getActiveLesson("2905").getOpenHelpRequests().get(c2.getId());

        Assert.assertEquals(Status.NEW,requestToChangeStatus.getStatus()); //TODO maybe should be string not Status?

        c1.updateHelpRequest(requestToChangeStatus, Status.COMPLETED);

        c1.getMessageReceived();

        Assert.assertEquals(1, c1.getOpenHelpRequests().size());  // TODO should I try to keep the original message id?
        Assert.assertEquals(l2Id, c1.getOpenHelpRequests().get(0).getLearner().getId());
        // TODO store time updated
    }

    @Test
    public void helpRequestOrderingIsNotImpactedWhenOnesAreRemoved() throws InterruptedException, ExecutionException, TimeoutException
    {

        TestClient c1 = connect(eduId, eduName, lesson2);
        TestClient c2 = connect(l1Id, l1Name, lesson2);
        TestClient c3 = connect(l2Id, l2Name, lesson2);
        TestClient c4 = connect(l99Id, l99Name, lesson2);

        c2.requestHelp().get(10, TimeUnit.SECONDS);
        c3.requestHelp().get(10, TimeUnit.SECONDS);
        c4.requestHelp().get(10, TimeUnit.SECONDS);

        c1.getMessageReceived();
        c1.getMessageReceived();
        c1.getMessageReceived();
        List<HelpRequest> helpRequests = c1.getOpenHelpRequests();
        Assert.assertEquals(3,helpRequests.size());

        Assert.assertEquals(l1Id,helpRequests.get(0).getLearner().getId());
        Assert.assertEquals(l2Id,helpRequests.get(1).getLearner().getId());
        Assert.assertEquals(l99Id,helpRequests.get(2).getLearner().getId());

        c3.cancelHelpRequest().get(10, TimeUnit.SECONDS);

        c1.getMessageReceived();

        List<HelpRequest> helpRequests2 = c1.getOpenHelpRequests();
        Assert.assertEquals(2,c1.getOpenHelpRequests().size());

        Assert.assertEquals(l1Id,helpRequests2.get(0).getLearner().getId());
        Assert.assertEquals(l99Id,helpRequests2.get(1).getLearner().getId());
    }

    @Test
    public void helpRequestOrderingIsNotImpactedWhenStatusesAreChanged() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, eduName, lesson2);
        TestClient c2 = connect(l1Id, l1Name, lesson2);
        TestClient c3 = connect(l2Id, l2Name, lesson2);
        TestClient c4 = connect(l99Id, l99Name, lesson2);

        c3.requestHelp().get(10, TimeUnit.SECONDS);
        c2.requestHelp().get(10, TimeUnit.SECONDS);
        c4.requestHelp().get(10, TimeUnit.SECONDS);

        c1.getMessageReceived();
        c1.getMessageReceived();
        c1.getMessageReceived();
        List<HelpRequest> helpRequests = c1.getOpenHelpRequests();
        Assert.assertEquals(3,helpRequests.size());

        Assert.assertEquals(l2Id,helpRequests.get(0).getLearner().getId());
        Assert.assertEquals(l1Id,helpRequests.get(1).getLearner().getId());
        Assert.assertEquals(l99Id,helpRequests.get(2).getLearner().getId());

        c1.updateHelpRequest(helpRequests.get(1),Status.IN_PROGRESS);
        c1.getMessageReceived();

        List<HelpRequest> helpRequests2 = c1.getOpenHelpRequests();
        Assert.assertEquals(3,c1.getOpenHelpRequests().size());

        Assert.assertEquals(l2Id,helpRequests2.get(0).getLearner().getId());
        Assert.assertEquals(l1Id,helpRequests2.get(1).getLearner().getId());
        Assert.assertEquals(l99Id,helpRequests2.get(2).getLearner().getId());

    }
}