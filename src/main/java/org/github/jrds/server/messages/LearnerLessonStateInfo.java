package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.Main;
import org.github.jrds.server.domain.*;
import org.github.jrds.server.dto.InstructionDto;
import org.github.jrds.server.extensions.chat.ChatMessage;
import org.github.jrds.server.extensions.lesson.ActiveLessonState;
import java.util.List;


public class LearnerLessonStateInfo extends Info
{
    private List<InstructionDto> instructionsSent;
    private ActiveLessonState activeLessonState;
    private List<ChatMessage> learnerChatMessages;
    private String educatorId;
    private String helpRequestStatus;


    public LearnerLessonStateInfo(String to, ActiveLesson activeLesson)
    {
        super(to);
        this.activeLessonState = activeLesson.getActiveLessonState();
        this.instructionsSent = activeLesson.getInstructionsSent();
        this.helpRequestStatus = activeLesson.getAttendance(Main.defaultInstance.usersStore.getUser(to)).getHelpRequestStatus().toString();
        this.learnerChatMessages = activeLesson.getAttendance(Main.defaultInstance.usersStore.getUser(to)).getChatHistory();
        this.educatorId = activeLesson.getAssociatedLessonStructure().getEducator().getId();
    }

    @JsonCreator
    public LearnerLessonStateInfo(
            @JsonProperty("to") String to,
            @JsonProperty("activeLesson") ActiveLessonState activeLessonState,
            @JsonProperty("helpRequestStatus") String helpRequestStatus,
            @JsonProperty("instructionsSent") List<InstructionDto> instructionsSent,
            @JsonProperty("learnerChatMessages") List<ChatMessage> learnerChatMessages,
            @JsonProperty("educatorId") String educatorId)
    {
        super(to);
        this.activeLessonState = activeLessonState;
        this.instructionsSent = instructionsSent;
        this.helpRequestStatus = helpRequestStatus;
        this.learnerChatMessages = learnerChatMessages;
        this.educatorId = educatorId;
    }


    public List<InstructionDto> getInstructionsSent()
    {
        return instructionsSent;
    }

    public String getHelpRequestStatus()
    {
        return helpRequestStatus;
    }

    public ActiveLessonState getActiveLessonState()
    {
        return activeLessonState;
    }

    public List<ChatMessage> getLearnerChatMessages()
    {
        return learnerChatMessages;
    }

    public String getEducatorId()
    {
        return educatorId;
    }
}
