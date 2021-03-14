package org.github.jrds.server.extensions.lesson;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.github.jrds.server.Main;
import org.github.jrds.server.domain.*;
import org.github.jrds.server.dto.InstructionDto;
import org.github.jrds.server.messages.*;

import java.util.*;

public class LessonMessagingExtension implements MessagingExtension
{
    private final Main server;

    public LessonMessagingExtension(Main server)
    {
        this.server = server;
    }

    @Override
    public boolean handles(Request request)
    {
        return request instanceof LessonStartMessage;
    }

    @Override
    public void handle(Request request, Attendance attendance, MessageSocket messageSocket)
    {
        if (request.getFrom().equals(attendance.getLesson().getEducator().getId()))
        {
            Lesson lesson = attendance.getLesson();
            for (Instruction i : lesson.getAllInstructions())
            {
                for (User learner : lesson.getLearners())
                {
                    if (server.attendanceStore.getAttendance(learner, lesson) != null)
                    {
                        InstructionDto instructionDto = new InstructionDto(i);
                        InstructionMessage iM = new InstructionMessage(learner.getId(), instructionDto);
                        messageSocket.sendMessage(iM);
                    }
//                    else
//                    {
//                        throw new IllegalArgumentException("Learner: " + learner.getId() +  " has no registered attendance");
//                        // TODO - consider how to test this - as there could be several messages (mainly success) in the educators queue.
//                    }
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("Learner cannot start a lesson");
        }
    }


    @Override
    public List<NamedType> getRequestNamedTypes()
    {
        return Arrays.asList(
                new NamedType(InstructionMessage.class, "instruction"),
                new NamedType(LessonStartMessage.class, "lessonStart")
        );
    }
}
