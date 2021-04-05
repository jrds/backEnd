package org.github.jrds.server.extensions.lesson;

import org.github.jrds.server.Main;
import org.github.jrds.server.domain.*;
import org.github.jrds.server.dto.InstructionDto;
import org.github.jrds.server.dto.UserDto;
import org.github.jrds.server.messages.LearnerLessonStateInfo;
import org.github.jrds.server.messages.MessageSocket;
import org.github.jrds.server.messages.MessagingExtension;
import org.github.jrds.server.messages.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LessonMessagingExtension implements MessagingExtension
{
    private final Main server;
    private final List<UserDto> learners = new ArrayList<>();
    private User educator;

    public LessonMessagingExtension(Main server)
    {
        this.server = server;
    }

    @Override
    public boolean handles(Request request)
    {
        return request instanceof LessonStartRequest;
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
                        InstructionInfo iM = new InstructionInfo(learnerId, instructionDto);
                        messageSocket.sendMessage(iM);
                    }
                    LearnerLessonStateInfo llsm = new LearnerLessonStateInfo(learnerId, activeLesson);
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
    public void userJoined(User user, Role userRole, MessageSocket messageSocket)
    {
        if (userRole.equals(Role.LEARNER))
        {
            learners.add(new UserDto(user));
        }
        else
        {
            educator = user;
        }
        if (educator != null)
        {
            LearnersInAttendanceInfo m = new LearnersInAttendanceInfo(educator.getId(),learners);
            messageSocket.sendMessage(m);
        }
    }

    @Override
    public void userLeft(User user, Role userRole)
    {
        if (userRole.equals(Role.LEARNER))
        {
            learners.remove(new UserDto(user));
        }
        else
        {
            educator = null;
        }
    }

    @Override
    public List<Class<?>> getRequestTypes()
    {
        return Arrays.asList(
                InstructionInfo.class,
                LessonStartRequest.class
        );
    }
}
