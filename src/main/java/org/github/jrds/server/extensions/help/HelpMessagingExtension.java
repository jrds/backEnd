package org.github.jrds.server.extensions.help;

import org.github.jrds.server.Main;
import org.github.jrds.server.domain.*;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.messages.LearnerLessonStateInfo;
import org.github.jrds.server.messages.MessageSocket;
import org.github.jrds.server.messages.MessagingExtension;
import org.github.jrds.server.messages.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HelpMessagingExtension implements MessagingExtension
{

    @Override
    public boolean handles(Request request)
    {
        return request instanceof NewHelpRequest || request instanceof CancelHelpRequest || request instanceof UpdateHelpStatusRequest;
    }

    @Override
    public void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket)
    {
        Attendance attendance = activeLesson.getAttendance(Main.defaultInstance.usersStore.getUser(request.getFrom()));
        HelpRequest helpRequest = attendance.getHelpRequest();

        try
        {
            if (request instanceof NewHelpRequest)
            {
                if (helpRequest.getStatus() == Status.NONE || helpRequest.getStatus() == Status.COMPLETED)
                {
                    helpRequest.setStatus(Status.NEW);
                    activeLesson.addHelpRequest(helpRequest);
                    messageSocket.sendMessage(new LearnerLessonStateInfo(request.getFrom(), activeLesson));
                }
                else
                {
                    throw new IllegalStateException("Learners cannot create more than one active help request");
                }
            }
            else if (request instanceof UpdateHelpStatusRequest)
            {
                HelpRequest learnerRequestToBeUpdated = activeLesson.getOpenHelpRequests().get(((UpdateHelpStatusRequest) request).getLearnerId())  ;

                if (learnerRequestToBeUpdated.getStatus() == Status.COMPLETED || learnerRequestToBeUpdated.getStatus() == Status.NONE)
                {
                    throw new IllegalStateException("No open help request found for this learner");
                }
                else
                {
                    if (((UpdateHelpStatusRequest) request).getNewStatus().equals(Status.IN_PROGRESS))
                    {
                        learnerRequestToBeUpdated.setStatus(Status.IN_PROGRESS);
                        messageSocket.sendMessage(new LearnerLessonStateInfo(request.getFrom(), activeLesson));
                    }
                    else if (((UpdateHelpStatusRequest) request).getNewStatus().equals(Status.COMPLETED))
                    {
                        learnerRequestToBeUpdated.setStatus(Status.COMPLETED);
                        //WOULD WRITE TO DB WHEN IT DEVELOPS BEYOND A PROTOTYPE
                        activeLesson.removeHelpRequest(learnerRequestToBeUpdated);
                        messageSocket.sendMessage(new LearnerLessonStateInfo(request.getFrom(), activeLesson));
                    }
                    else
                        throw new IllegalStateException("Once a request has been created, it cannot be reset to a NEW request");
                }
            }
            else
            {
                helpRequest.setStatus(Status.NONE);
                activeLesson.removeHelpRequest(helpRequest);
                messageSocket.sendMessage(new LearnerLessonStateInfo(request.getFrom(), activeLesson));
            }

        }
        finally
        {
            List<HelpRequestDto> dtos = new ArrayList<>();
            activeLesson.getOpenHelpRequests().values().forEach(hr -> dtos.add(new HelpRequestDto(hr)));
            messageSocket.sendMessage(new OpenHelpRequestsInfo(activeLesson.getAssociatedLessonStructure().getEducator().getId(), dtos));
        }
    }

    @Override
    public List<Class<?>> getRequestTypes()
    {
        return Arrays.asList(
                NewHelpRequest.class,
                CancelHelpRequest.class,
                OpenHelpRequestsInfo.class,
                UpdateHelpStatusRequest.class
        );
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
