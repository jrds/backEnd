package org.github.jrds.server.messages;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.github.jrds.server.domain.ActiveLesson;

import java.util.List;

public interface MessagingExtension
{
    boolean handles(Request request);

    void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket);

    List<NamedType> getRequestNamedTypes();
}
