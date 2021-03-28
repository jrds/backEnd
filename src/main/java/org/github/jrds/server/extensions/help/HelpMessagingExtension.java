package org.github.jrds.server.extensions.help;

import org.github.jrds.server.Main;
import org.github.jrds.server.domain.ActiveLesson;
import org.github.jrds.server.domain.Status;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.messages.MessageSocket;
import org.github.jrds.server.messages.MessagingExtension;
import org.github.jrds.server.messages.Request;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HelpMessagingExtension implements MessagingExtension
{

    @Override
    public boolean handles(Request request)
    {
        return request instanceof HelpRequest || request instanceof CancelHelpRequest || request instanceof UpdateHelpStatusRequest;
    }

    @Override
    public void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket)
    {
        try
        {
            if (request instanceof HelpRequest)
            {
                if (activeLesson.getOpenHelpRequests().stream().noneMatch(hr -> hr.getLearner().getId().equals(request.getFrom())))
                {
                    org.github.jrds.server.domain.HelpRequest helpRequest = new org.github.jrds.server.domain.HelpRequest(Main.defaultInstance.usersStore.getUser(request.getFrom()));
                    activeLesson.addHelpRequest(helpRequest);
                }
                else
                {
                    throw new IllegalStateException("Learners cannot create more than one active help request");
                }
            }
            else if (request instanceof UpdateHelpStatusRequest)
            {
                Optional<org.github.jrds.server.domain.HelpRequest> toUpdate = activeLesson.getOpenHelpRequests().stream()
                        .filter(hr -> hr.getLearner().getId().equals(((UpdateHelpStatusRequest) request).getLearnerId()))
                        .findFirst();
                if (toUpdate.isEmpty())
                {
                    throw new IllegalStateException("No open help request found for this learner");
                }
                else
                {
                    if (((UpdateHelpStatusRequest) request).getNewStatus().equals(Status.IN_PROGRESS))
                    {
                        toUpdate.get().setStatus(((UpdateHelpStatusRequest) request).getNewStatus());
                    }
                    else if (((UpdateHelpStatusRequest) request).getNewStatus().equals(Status.COMPLETED))
                    {
                        toUpdate.get().setStatus(((UpdateHelpStatusRequest) request).getNewStatus());
                        //WOULD WRITE TO DB WHEN IT DEVELOPS BEYOND A PROTOTYPE
                        if(toUpdate.isPresent())
                        {
                            org.github.jrds.server.domain.HelpRequest toDelete = toUpdate.get();
                            activeLesson.removeHelpRequest(toDelete);
                        }
                    }
                    else
                        throw new IllegalStateException("Once a request has been created, it cannot be reset to a NEW request");
                }
            }
            else
            {
                Optional<org.github.jrds.server.domain.HelpRequest> toRemove = activeLesson.getOpenHelpRequests().stream()
                        .filter(hr -> hr.getLearner().getId().equals(request.getFrom()))
                        .findFirst();
                if(toRemove.isPresent())
                {
                    org.github.jrds.server.domain.HelpRequest toDelete = toRemove.get();
                    activeLesson.removeHelpRequest(toDelete);
                }
            }
        }
        finally
        {
            List<HelpRequestDto> dtos = activeLesson.getOpenHelpRequests().stream().map(HelpRequestDto::new).collect(Collectors.toList());
            messageSocket.sendMessage(new OpenHelpRequestsInfo(activeLesson.getAssociatedLessonStructure().getEducator().getId(), dtos));
        }
    }

    @Override
    public List<Class<?>> getRequestTypes()
    {
        return Arrays.asList(
                HelpRequest.class,
                CancelHelpRequest.class,
                OpenHelpRequestsInfo.class,
                UpdateHelpStatusRequest.class
        );
    }
}
