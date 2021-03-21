package org.github.jrds.server.extensions.lesson;

import org.github.jrds.server.Main;
import org.github.jrds.server.domain.*;
import org.github.jrds.server.dto.InstructionDto;
import org.github.jrds.server.messages.LearnerLessonStateMessage;
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
            activeLesson.setActiveLessonState(ActiveLessonState.IN_PROGRESS);
            LessonStructure lessonStructure = activeLesson.getAssociatedLessonStructure();

            for (Instruction i: lessonStructure.getAllInstructions()){
                InstructionDto instructionDto = new InstructionDto(i);
                activeLesson.addToInstructionSent(instructionDto);
            }

            for (Attendance a : activeLesson.getActiveLessonAttendances())
            {
                if (a.getRole().equals(Role.LEARNER))
                {
                    String learnerId = a.getUser().getId();

                    for (Instruction i : lessonStructure.getAllInstructions())
                    {
                        InstructionDto instructionDto = new InstructionDto(i);
                        InstructionMessage iM = new InstructionMessage(learnerId, instructionDto);
                        messageSocket.sendMessage(iM);
                    }
                    LearnerLessonStateMessage llsm = new LearnerLessonStateMessage(learnerId, activeLesson);
                    messageSocket.sendMessage(llsm);
                }
                // TODO send educator lesson state message to a.getEducator()
            }
        }
        else
        {
            throw new IllegalArgumentException("Learner cannot start a lesson");
        }
    }

    @Override
    public List<Class<?>> getRequestTypes()
    {
        return Arrays.asList(
                InstructionMessage.class,
                LessonStartMessage.class
        );
    }
}
