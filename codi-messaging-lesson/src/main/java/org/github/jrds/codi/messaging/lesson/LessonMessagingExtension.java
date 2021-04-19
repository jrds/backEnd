package org.github.jrds.codi.messaging.lesson;

import org.github.jrds.codi.core.domain.*;
import org.github.jrds.codi.core.dto.InstructionDto;
import org.github.jrds.codi.core.dto.UserDto;
import org.github.jrds.codi.core.messages.LearnerLessonStateInfo;
import org.github.jrds.codi.core.messages.MessageSocket;
import org.github.jrds.codi.core.messages.MessagingExtension;
import org.github.jrds.codi.core.messages.Request;
import org.github.jrds.codi.core.persistence.PersistenceServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class LessonMessagingExtension implements MessagingExtension
{
    private final List<UserDto> learnersInAttendance = new ArrayList<>();
    private User educator;
    private Object lock = new Object();

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
            }

        }
        else
        {
            throw new IllegalArgumentException("Learner cannot start a lesson");
        }
    }

    @Override
    public void userJoined(User user, ActiveLesson activeLesson, Role userRole, MessageSocket messageSocket)
    {
        List<UserDto> learnersAttending;

        if (userRole.equals(Role.LEARNER))
        {
            UserDto learner = new UserDto(user);
            synchronized (lock)
            {
                learnersInAttendance.add(learner);
                learnersAttending = new ArrayList<>(this.learnersInAttendance);
            }
        }
        else
        {
            educator = user;
            synchronized (lock)
            {
                learnersAttending = new ArrayList<>(this.learnersInAttendance);
            }
        }

        if (educator != null)
        {
            List<UserDto> learnersExpected = activeLesson.getAssociatedLessonStructure().getLearners().stream().map(UserDto::new).collect(Collectors.toList());
            learnersExpected.removeAll(learnersAttending);
            LearnersInfo m = new LearnersInfo(educator.getId(), learnersAttending, learnersExpected);
            messageSocket.sendMessage(m);
        }
    }

    @Override
    public void userLeft(User user, ActiveLesson activeLesson, Role userRole)
    {
        if (userRole.equals(Role.LEARNER))
        {
            UserDto learner = new UserDto(user);
            synchronized (lock)
            {
                learnersInAttendance.remove(learner);
            }
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
