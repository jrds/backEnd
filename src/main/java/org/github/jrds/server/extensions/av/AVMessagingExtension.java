package org.github.jrds.server.extensions.av;

import org.github.jrds.server.domain.ActiveLesson;
import org.github.jrds.server.domain.Role;
import org.github.jrds.server.domain.User;
import org.github.jrds.server.messages.MessageSocket;
import org.github.jrds.server.messages.MessagingExtension;
import org.github.jrds.server.messages.Request;

import java.util.Arrays;
import java.util.List;

public class AVMessagingExtension implements MessagingExtension
{
    @Override
    public void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket)
    {
        messageSocket.sendMessage(request);
    }

    @Override
    public List<Class<?>> getRequestTypes()
    {
        return Arrays.asList(AVOffer.class, AVAnswer.class, AVReject.class, AVIceCandidate.class, AVClose.class);
    }

    @Override
    public void userJoined(User user, ActiveLesson activeLesson, Role userRole, MessageSocket messageSocket)
    {
    }

    @Override
    public void userLeft(User user, ActiveLesson activeLesson, Role userRole)
    {
    }
}