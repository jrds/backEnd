package org.github.jrds.server;

import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.messages.Response;
import org.github.jrds.server.messages.SuccessMessage;
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
        Assert.assertTrue(state instanceof SuccessMessage);

        // wait for c1 to have received the message
        c1.getMessageReceived();
        List<HelpRequestDto> helpRequests = c1.getHelpRequests();
        Assert.assertEquals(1,helpRequests.size());
        Assert.assertEquals(c2.getId(), helpRequests.get(0).getLearnerId());
    }

    @Test
    public void learnerCancelsAHelpRequest() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        c2.requestHelp().get(10, TimeUnit.SECONDS);

        // wait for c1 to have received the message
        c1.getMessageReceived();
        Assert.assertEquals(1,c1.getHelpRequests().size());

        Response state = c2.cancelHelpRequest().get(10, TimeUnit.SECONDS);
        Assert.assertTrue(state instanceof SuccessMessage);

        c1.getMessageReceived();
        Assert.assertEquals(0,c1.getHelpRequests().size());

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

        List<HelpRequestDto> helpRequests = c1.getHelpRequests();
        Assert.assertEquals(2,helpRequests.size());
        Assert.assertEquals(l2Id,helpRequests.get(0).getLearnerId());
        Assert.assertEquals(l1Id,helpRequests.get(1).getLearnerId());
    }

    @Test
    public void learnerCantSubmit2HelpRequests()
    {

    }

    @Test
    public void educatorMarksHelpRequestAsComplete()
    {

    }

    @Test
    public void educatorMarksHelpRequestAsInProgress()
    {

    }



    @Test
    public void helpRequestOrderingIsNotImpactedWhenOnesAreRemoved(){

    }

    @Test
    public void helpRequestOrderingIsNotImpactedWhenStatusesAreChanged(){

    }
}