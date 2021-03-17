package org.github.jrds.server.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HelpRequest implements Comparable<HelpRequest>
{
    private static final AtomicInteger ID_GEN = new AtomicInteger();
    private static final Object LOCK = new Object();

    private final int id;
    private final User learner;
    private final Instant timeReceived;
    private Status status;

    public HelpRequest(User learner)
    {
        synchronized (LOCK)
        {
            this.id = ID_GEN.incrementAndGet();
            this.timeReceived = Instant.now();
        }
        this.learner = Objects.requireNonNull(learner);
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
        return o == null ? 1 : id - o.id;
    }
}
