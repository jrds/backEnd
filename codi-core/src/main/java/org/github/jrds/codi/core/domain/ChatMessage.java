package org.github.jrds.codi.core.domain;

import java.time.Instant;

public class ChatMessage
{
    private final User from;
    private final User to;
    private final Instant timeSent;
    private final String text;

    public ChatMessage(User from, User to, Instant timeSent, String text)
    {
        this.to = to;
        this.from = from;
        this.timeSent = timeSent;
        this.text = text;
    }

    public User getFrom()
    {
        return from;
    }

    public User getTo()
    {
        return to;
    }

    public Instant getTimeSent()
    {
        return timeSent;
    }

    public String getText()
    {
        return text;
    }
}
