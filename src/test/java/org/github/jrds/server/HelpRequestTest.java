package org.github.jrds.server;

import org.junit.Assert;
import org.junit.Test;

public class HelpRequestTest extends ApplicationTest
{

    @Test
    public void learnerRequestHelpEducatorReceivesRequest()
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        c2.requestHelp();
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

    public void helpRequestOrderingIsNotImpactedWhenStatusesAreChanged(){

    }
}