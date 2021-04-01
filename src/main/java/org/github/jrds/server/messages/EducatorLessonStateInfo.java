package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.Main;
import org.github.jrds.server.domain.ActiveLesson;
import org.github.jrds.server.domain.HelpRequest;
import org.github.jrds.server.domain.User;
import org.github.jrds.server.dto.InstructionDto;
import org.github.jrds.server.extensions.chat.ChatMessage;
import org.github.jrds.server.extensions.lesson.ActiveLessonState;

import java.util.List;
import java.util.Map;


public class EducatorLessonStateInfo extends Info
{
    private List<InstructionDto> instructionsSent;
    private ActiveLessonState activeLessonState;
    private Map<String, ChatMessage> chatMessages;
    private List<String> learnersInAttendance;
    private Map<String, HelpRequest> openHelpRequests;


    public EducatorLessonStateInfo(String to, ActiveLesson activeLesson)
    {
        super(to);
        this.activeLessonState = activeLesson.getActiveLessonState();
        this.instructionsSent = activeLesson.getInstructionsSent();
        this.openHelpRequests = activeLesson.getOpenHelpRequests();
        this.chatMessages = activeLesson.getChatMessageForEducator();
        this.learnersInAttendance = activeLesson.getLearnersInAttendance();
    }

    @JsonCreator
    public EducatorLessonStateInfo(
            @JsonProperty("to") String to,
            @JsonProperty("activeLesson") ActiveLessonState activeLessonState,
            @JsonProperty("openHelpRequests") Map<String, HelpRequest> openHelpRequests,
            @JsonProperty("instructionsSent") List<InstructionDto> instructionsSent,
            @JsonProperty("chatMessages") Map<String, ChatMessage> chatMessages,
            @JsonProperty("learnersInAttendance") List<String> learnersInAttendance)
    {
        super(to);
        this.activeLessonState = activeLessonState;
        this.instructionsSent = instructionsSent;
        this.openHelpRequests = openHelpRequests;
        this.chatMessages = chatMessages;
        this.learnersInAttendance = learnersInAttendance;
    }


    public List<InstructionDto> getInstructionsSent()
    {
        return instructionsSent;
    }

    public ActiveLessonState getActiveLessonState()
    {
        return activeLessonState;
    }

    public Map<String, ChatMessage> getChatMessages()
    {
        return chatMessages;
    }

    public List<String> getLearnersInAttendance()
    {
        return learnersInAttendance;
    }

    public Map<String, HelpRequest> getOpenHelpRequests()
    {
        return openHelpRequests;
    }
}
