package org.github.jrds.server.messages;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.github.jrds.server.domain.ActiveLesson;
import org.github.jrds.server.domain.Role;
import org.github.jrds.server.domain.User;

import java.util.List;

public interface MessagingExtension
{
    default boolean handles(Request request)
    {
        return getRequestTypes().contains(request.getClass());
    }

    void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket);

    void userJoined(User user, Role userRole, MessageSocket messageSocket);

    void userLeft(User user, Role userRole);

    List<Class<?>> getRequestTypes();
}
