package org.github.jrds.server;

import org.github.jrds.server.domain.HelpRequest;
import org.github.jrds.server.domain.Instruction;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.messages.Message;
import org.github.jrds.server.messages.RequestHelpMessage;
import org.github.jrds.server.messages.Response;
import org.github.jrds.server.messages.SuccessMessage;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
        Assert.assertFalse(helpRequests.isEmpty());
    }

    @Test
    public void learnerCancelsAHelpRequest()
    {

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
    public void helpRequestsAreReceivedInTheCorrectOrder()
    {

    }

    @Test
    public void helpRequestOrderingIsNotImpactedWhenOnesAreRemoved(){

    }

    @Test
    public void helpRequestOrderingIsNotImpactedWhenStatusesAreChanged(){

    }
}