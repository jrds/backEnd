package org.github.jrds.server.extensions.lesson;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.github.jrds.server.Main;
import org.github.jrds.server.domain.*;
import org.github.jrds.server.dto.InstructionDto;
import org.github.jrds.server.messages.MessageSocket;
import org.github.jrds.server.messages.MessagingExtension;
import org.github.jrds.server.messages.Request;

import java.util.Arrays;
import java.util.List;

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
    public void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket)
    {
        if (request.getFrom().equals(activeLesson.getAssociatedLessonStructure().getEducator().getId()))
        {
            LessonStructure lessonStructure = activeLesson.getAssociatedLessonStructure();
            for (Attendance a : activeLesson.getActiveLessonAttendances())
            {
                for (Instruction i: lessonStructure.getAllInstructions())
                {
                    InstructionDto instructionDto = new InstructionDto(i);
                    InstructionMessage iM = new InstructionMessage(a.getUser().getId(), instructionDto);
                    messageSocket.sendMessage(iM);

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
