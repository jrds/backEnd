package org.github.jrds.server.messages;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.github.jrds.server.domain.ActiveLesson;

import java.util.List;

public interface MessagingExtension
{
    default boolean handles(Request request)
    {
        return getRequestTypes().contains(request.getClass());
    }

    void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket);

    List<Class<?>> getRequestTypes();
}
