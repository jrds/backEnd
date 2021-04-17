package org.github.jrds.codi.messaging.chat;

import org.github.jrds.codi.core.domain.ActiveLesson;
import org.github.jrds.codi.core.domain.Attendance;
import org.github.jrds.codi.core.domain.Role;
import org.github.jrds.codi.core.domain.User;
import org.github.jrds.codi.core.messages.LearnerLessonStateInfo;
import org.github.jrds.codi.core.messages.MessageSocket;
import org.github.jrds.codi.core.messages.MessagingExtension;
import org.github.jrds.codi.core.messages.Request;
import org.github.jrds.codi.core.persistence.PersistenceServices;
import org.github.jrds.codi.core.persistence.UsersStore;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

public class ChatMessagingExtension implements MessagingExtension
{
    private final PersistenceServices persistenceServices;

    public ChatMessagingExtension()
    {
        persistenceServices = ServiceLoader.load(PersistenceServices.class).findFirst().orElseThrow();
    }

    @Override
    public boolean handles(Request request)
    {
        return request instanceof ChatMessage;
    }

    @Override
    public void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket)
    {
        messageSocket.sendMessage(request);

        User from = persistenceServices.getUsersStore().getUser(request.getFrom());
        Attendance fromAttendance = activeLesson.getAttendance(from);
        Role fromRole = fromAttendance.getRole();

        User to = persistenceServices.getUsersStore().getUser(request.getTo());
        Attendance toAttendance = activeLesson.getAttendance(to);
        Role toRole = toAttendance.getRole();

        org.github.jrds.codi.core.domain.ChatMessage domainMessage = toDomain((ChatMessage) request);
        fromAttendance.addMessageToChatHistory(domainMessage);
        toAttendance.addMessageToChatHistory(domainMessage);

        if (fromRole.equals(Role.LEARNER) && toRole.equals(Role.EDUCATOR)){
            messageSocket.sendMessage(new LearnerLessonStateInfo(from.getId(), activeLesson));
            //            messageSocket.sendMessage(new EducatorLessonStateMessage(to.getId(), activeLesson));
        }
        else if (fromRole.equals(Role.EDUCATOR) && toRole.equals(Role.LEARNER)){
            messageSocket.sendMessage(new LearnerLessonStateInfo(to.getId(), activeLesson));
            //            messageSocket.sendMessage(new EducatorLessonStateMessage(from.getId(), activeLesson));
        }


    }

    private org.github.jrds.codi.core.domain.ChatMessage toDomain(ChatMessage request)
    {
        UsersStore usersStore = persistenceServices.getUsersStore();
        return new org.github.jrds.codi.core.domain.ChatMessage(
                usersStore.getUser(request.getFrom()),
                usersStore.getUser(request.getTo()),
                Instant.now(),
                request.getText()
        );
    }

    @Override
    public List<Class<?>> getRequestTypes()
    {
        return Collections.singletonList(ChatMessage.class);
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
