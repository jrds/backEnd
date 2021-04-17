package org.github.jrds.codi.core.messages;

import org.github.jrds.codi.core.domain.ActiveLesson;
import org.github.jrds.codi.core.domain.Role;
import org.github.jrds.codi.core.domain.User;

import java.util.List;

public interface MessagingExtension
{
    default boolean handles(Request request)
    {
        return getRequestTypes().contains(request.getClass());
    }

    void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket);

    void userJoined(User user, ActiveLesson activeLesson, Role userRole, MessageSocket messageSocket);

    void userLeft(User user, ActiveLesson activeLesson, Role userRole);

    List<Class<?>> getRequestTypes();
}
