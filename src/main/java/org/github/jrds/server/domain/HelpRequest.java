package org.github.jrds.server.domain;

import java.time.Instant;
import java.util.Objects;

public class HelpRequest implements Comparable<HelpRequest>
{
    private User learner;
    private Instant timeReceived;
    private Status status;

    public HelpRequest(User learner)
    {
        this.learner = Objects.requireNonNull(learner);
        this.timeReceived = Instant.now();
        this.status = Status.NEW;
    }

    public User getLearner()
    {
        return learner;
    }

    public Instant getTimeReceived()
    {
        return timeReceived;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    @Override
    public int compareTo(HelpRequest o)
    {
        return o == null ? 1 : timeReceived.compareTo(o.timeReceived);
    }



}
