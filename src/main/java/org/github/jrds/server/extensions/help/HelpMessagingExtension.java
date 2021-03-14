package org.github.jrds.server.extensions.help;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.github.jrds.server.domain.Attendance;
import org.github.jrds.server.domain.HelpRequest;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.messages.MessageSocket;
import org.github.jrds.server.messages.MessagingExtension;
import org.github.jrds.server.messages.Request;

import java.util.*;
import java.util.stream.Collectors;

public class HelpMessagingExtension implements MessagingExtension
{
    private final SortedSet<HelpRequest> openHelpRequests = new TreeSet<>();

    @Override
    public boolean handles(Request request)
    {
        return request instanceof RequestHelpMessage || request instanceof CancelHelpRequestMessage;
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
                new NamedType(OpenHelpRequestsMessage.class, "openHelpRequests")

        );
    }
}
