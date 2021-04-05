package org.github.jrds.server.extensions.chat;

import org.github.jrds.server.Main;
import org.github.jrds.server.domain.ActiveLesson;
import org.github.jrds.server.domain.Attendance;
import org.github.jrds.server.domain.Role;
import org.github.jrds.server.domain.User;
import org.github.jrds.server.messages.LearnerLessonStateInfo;
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

        User from = Main.defaultInstance.usersStore.getUser(request.getFrom());
        Attendance fromAttendance = activeLesson.getAttendance(from);
        Role fromRole = fromAttendance.getRole();

        User to = Main.defaultInstance.usersStore.getUser(request.getTo());
        Attendance toAttendance = activeLesson.getAttendance(to);
        Role toRole = toAttendance.getRole();

        fromAttendance.addMessageToChatHistory((ChatMessage) request);
        toAttendance.addMessageToChatHistory((ChatMessage) request);

        if (fromRole.equals(Role.LEARNER) && toRole.equals(Role.EDUCATOR)){
            messageSocket.sendMessage(new LearnerLessonStateInfo(from.getId(), activeLesson));
            //            messageSocket.sendMessage(new EducatorLessonStateMessage(to.getId(), activeLesson));
        }
        else if (fromRole.equals(Role.EDUCATOR) && toRole.equals(Role.LEARNER)){
            messageSocket.sendMessage(new LearnerLessonStateInfo(to.getId(), activeLesson));
            //            messageSocket.sendMessage(new EducatorLessonStateMessage(from.getId(), activeLesson));
        }


    }

    @Override
    public List<Class<?>> getRequestTypes()
    {
        return Collections.singletonList(ChatMessage.class);
    }

    @Override
    public void userJoined(User user, Role userRole, MessageSocket messageSocket)
    {

    }

    @Override
    public void userLeft(User user, Role userRole)
    {

    }
}
