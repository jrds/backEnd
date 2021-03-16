package org.github.jrds.server.extensions.help;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.github.jrds.server.domain.Attendance;
import org.github.jrds.server.domain.HelpRequest;
import org.github.jrds.server.domain.Status;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.messages.MessageSocket;
import org.github.jrds.server.messages.MessagingExtension;
import org.github.jrds.server.messages.Request;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class HelpMessagingExtension implements MessagingExtension
{
    private final SortedSet<HelpRequest> openHelpRequests = new ConcurrentSkipListSet<>();

    @Override
    public boolean handles(Request request)
    {
        return request instanceof RequestHelpMessage || request instanceof CancelHelpRequestMessage || request instanceof UpdateHelpRequestStatusMessage;
    }

    @Override
    public void handle(Request request, Attendance attendance, MessageSocket messageSocket)
    {
        if (request instanceof RequestHelpMessage)
        {
            if (openHelpRequests.stream().noneMatch(hr -> hr.getLearner().getId().equals(request.getFrom())))
            {
                HelpRequest helpRequest = new HelpRequest(attendance.getUser());
                openHelpRequests.add(helpRequest);
                List<HelpRequestDto> dtos = openHelpRequests.stream().map(HelpRequestDto::new).collect(Collectors.toList());
                messageSocket.sendMessage(new OpenHelpRequestsMessage(attendance.getLesson().getEducator().getId(), dtos));
            }
            else
            {
                throw new IllegalStateException("Learners cannot create more than one active help request");
            }
        }
        else if (request instanceof UpdateHelpRequestStatusMessage)
        {
            Optional<HelpRequest> toUpdate = openHelpRequests.stream()
                    .filter(hr -> hr.getLearner().getId().equals(((UpdateHelpRequestStatusMessage) request).getLearnerId()))
                    .findFirst();
            if (toUpdate.isEmpty())
            {
                throw new IllegalStateException("No open help request found for this learner");
            }
            else
            {
                if (((UpdateHelpRequestStatusMessage) request).getNewStatus().equals(Status.IN_PROGRESS))
                {
                    toUpdate.get().setStatus(((UpdateHelpRequestStatusMessage) request).getNewStatus());
                    List<HelpRequestDto> dtos = openHelpRequests.stream().map(HelpRequestDto::new).collect(Collectors.toList());
                    messageSocket.sendMessage(new OpenHelpRequestsMessage(attendance.getLesson().getEducator().getId(), dtos));
                }
                else if (((UpdateHelpRequestStatusMessage) request).getNewStatus().equals(Status.COMPLETED))
                {
                    toUpdate.get().setStatus(((UpdateHelpRequestStatusMessage) request).getNewStatus());
                    //WOULD WRITE TO DB WHEN IT DEVELOPS BEYOND A PROTOTYPE
                    toUpdate.ifPresent(openHelpRequests::remove);
                    List<HelpRequestDto> dtos = openHelpRequests.stream().map(HelpRequestDto::new).collect(Collectors.toList());
                    messageSocket.sendMessage(new OpenHelpRequestsMessage(attendance.getLesson().getEducator().getId(), dtos));
                }
                else throw new IllegalStateException("Once a request has been created, it cannot be reset to a NEW request");
            }
        }
        else
        {
            Optional<HelpRequest> toRemove = openHelpRequests.stream()
                    .filter(hr -> hr.getLearner().getId().equals(request.getFrom()))
                    .findFirst();
            toRemove.ifPresent(openHelpRequests::remove);
            List<HelpRequestDto> dtos = openHelpRequests.stream().map(HelpRequestDto::new).collect(Collectors.toList());
            messageSocket.sendMessage(new OpenHelpRequestsMessage(attendance.getLesson().getEducator().getId(), dtos));
        }
    }

    @Override
    public List<NamedType> getRequestNamedTypes()
    {
        return Arrays.asList(
                new NamedType(RequestHelpMessage.class, "requestHelp"),
                new NamedType(CancelHelpRequestMessage.class, "requestHelpCancel"),
                new NamedType(OpenHelpRequestsMessage.class, "openHelpRequests"),
                new NamedType(UpdateHelpRequestStatusMessage.class, "UpdateHelpRequestStatusMessage")
        );
    }
}
