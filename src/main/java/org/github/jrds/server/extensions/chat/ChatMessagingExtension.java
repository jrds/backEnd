package org.github.jrds.server.extensions.chat;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.github.jrds.server.domain.Attendance;
import org.github.jrds.server.domain.HelpRequest;
import org.github.jrds.server.messages.MessageSocket;
import org.github.jrds.server.messages.MessagingExtension;
import org.github.jrds.server.messages.Request;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ChatMessagingExtension implements MessagingExtension
{
    @Override
    public boolean handles(Request request)
    {
        return request instanceof ChatMessage;
    }

    @Override
    public void handle(Request request, Attendance attendance, MessageSocket messageSocket)
    {
        messageSocket.sendMessage(request);
    }

    @Override
    public List<NamedType> getRequestNamedTypes()
    {
        return Collections.singletonList(new NamedType(ChatMessage.class, "chat"));
    }
}
