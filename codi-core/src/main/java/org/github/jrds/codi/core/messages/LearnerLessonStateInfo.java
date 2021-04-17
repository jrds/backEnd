package org.github.jrds.codi.core.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.codi.core.persistence.PersistenceServices;
import org.github.jrds.codi.core.persistence.UsersStore;
import org.github.jrds.codi.core.domain.*;
import org.github.jrds.codi.core.dto.InstructionDto;
import org.github.jrds.codi.core.extensions.lesson.ActiveLessonState;
import java.util.List;
import java.util.ServiceLoader;


public class LearnerLessonStateInfo extends Info
{
    private List<InstructionDto> instructionsSent;
    private ActiveLessonState activeLessonState;
    //private List<ChatMessage> learnerChatMessages;
    private String educatorId;
    private String helpRequestStatus;


    public LearnerLessonStateInfo(String to, ActiveLesson activeLesson)
    {
        super(to);
        UsersStore usersStore = ServiceLoader.load(PersistenceServices.class).findFirst().orElseThrow().getUsersStore();
        this.activeLessonState = activeLesson.getActiveLessonState();
        this.instructionsSent = activeLesson.getInstructionsSent();
        this.helpRequestStatus = activeLesson.getAttendance(usersStore.getUser(to)).getHelpRequestStatus().toString();
        //this.learnerChatMessages = activeLesson.getAttendance(usersStore.getUser(to)).getChatHistory();
        this.educatorId = activeLesson.getAssociatedLessonStructure().getEducator().getId();
    }

    @JsonCreator
    public LearnerLessonStateInfo(
            @JsonProperty("to") String to,
            @JsonProperty("activeLesson") ActiveLessonState activeLessonState,
            @JsonProperty("helpRequestStatus") String helpRequestStatus,
            @JsonProperty("instructionsSent") List<InstructionDto> instructionsSent,
            //@JsonProperty("learnerChatMessages") List<ChatMessage> learnerChatMessages,
            @JsonProperty("educatorId") String educatorId)
    {
        super(to);
        this.activeLessonState = activeLessonState;
        this.instructionsSent = instructionsSent;
        this.helpRequestStatus = helpRequestStatus;
        //this.learnerChatMessages = learnerChatMessages;
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

//    public List<ChatMessage> getLearnerChatMessages()
//    {
//        return learnerChatMessages;
//    }

    public String getEducatorId()
    {
        return educatorId;
    }
}
