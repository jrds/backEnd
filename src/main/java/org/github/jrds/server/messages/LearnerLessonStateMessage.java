package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    public LearnerLessonStateMessage(
            @JsonProperty("to") String to,
            @JsonProperty("activeLesson") ActiveLesson activeLesson)
    {
        super(to);
        this.activeLessonState = activeLesson.getActiveLessonState();
        this.instructionsSent = activeLesson.getInstructionsSent();
        this.openHelpRequestForThisLearner = activeLesson.getOpenHelpRequests().stream().anyMatch(helpRequest -> helpRequest.getLearner().toString().equals(to));
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
