package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.Main;
import org.github.jrds.server.domain.*;
import org.github.jrds.server.dto.InstructionDto;
import org.github.jrds.server.extensions.chat.ChatMessage;
import org.github.jrds.server.extensions.lesson.ActiveLessonState;
import java.util.List;


public class LearnerLessonStateMessage extends Info
{
    private List<InstructionDto> instructionsSent;
    private boolean openHelpRequestForThisLearner;
    private ActiveLessonState activeLessonState;
    private List<ChatMessage> learnerChatMessages;

    public LearnerLessonStateMessage(String to, ActiveLesson activeLesson)
    {
        super(to);
        this.activeLessonState = activeLesson.getActiveLessonState();
        this.instructionsSent = activeLesson.getInstructionsSent();
        this.openHelpRequestForThisLearner = activeLesson.getOpenHelpRequests().stream().anyMatch(helpRequest -> helpRequest.getLearner().toString().equals(to));
        this.learnerChatMessages = activeLesson.getAttendance(Main.defaultInstance.usersStore.getUser(to)).getChatHistory();
    }

    @JsonCreator
    public LearnerLessonStateMessage(
            @JsonProperty("to") String to,
            @JsonProperty("activeLesson") ActiveLessonState activeLessonState,
            @JsonProperty("openHelpRequestForThisLearner") boolean openHelpRequestForThisLearner,
            @JsonProperty("instructionsSent") List<InstructionDto> instructionsSent,
            @JsonProperty("learnerChatMessages") List<ChatMessage> learnerChatMessages)

    {
        super(to);
        this.activeLessonState = activeLessonState;
        this.instructionsSent = instructionsSent;
        this.openHelpRequestForThisLearner = openHelpRequestForThisLearner;
        this.learnerChatMessages = learnerChatMessages;
    }


    public List<InstructionDto> getInstructionsSent()
    {
        return instructionsSent;
    }

    public boolean isOpenHelpRequestForThisLearner()
    {
        return openHelpRequestForThisLearner;
    }

    public ActiveLessonState getActiveLessonState()
    {
        return activeLessonState;
    }

    public List<ChatMessage> getLearnerChatMessages()
    {
        return learnerChatMessages;
    }
}
