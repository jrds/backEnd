package org.github.jrds.server.extensions.chat;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.github.jrds.server.domain.ActiveLesson;
import org.github.jrds.server.messages.MessageSocket;
import org.github.jrds.server.messages.MessagingExtension;
import org.github.jrds.server.messages.Request;

import java.util.Collections;
import java.util.List;

public class ChatMessagingExtension implements MessagingExtension
{
    @Override
    public boolean handles(Request request)
    {
        return request instanceof ChatMessage;
    }

    @Override
    public void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket)
    {
        messageSocket.sendMessage(request);
    }

    @Override
    public List<NamedType> getRequestNamedTypes()
    {
        return Collections.singletonList(new NamedType(ChatMessage.class, "chat"));
    }
}
