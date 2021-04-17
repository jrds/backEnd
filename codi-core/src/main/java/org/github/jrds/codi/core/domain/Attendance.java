package org.github.jrds.codi.core.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Attendance
{

    private final User user;
    private final LessonStructure lessonStructure;
    private final Role role;
    private List<ChatMessage> chatHistory;
    private Code code;
    private HelpRequest helpRequest;

    public Attendance(User user, LessonStructure lessonStructure)
    {
        this.user = Objects.requireNonNull(user, "Invalid user");
        this.lessonStructure = Objects.requireNonNull(lessonStructure, "Invalid lesson");
        this.role = lessonStructure.getUserRole(user);
        this.chatHistory = new ArrayList<>();
        this.code = new Code();

        if (this.role == Role.NONE)
        {
            throw new IllegalArgumentException("User cannot attend this lesson");
        } else if (this.role == Role.LEARNER)
        {
            this.helpRequest = new HelpRequest(user);
        }

        // TODO - Make tests for role.

    }

    public LessonStructure getLessonStructure()
    {
        return lessonStructure;
    }

    public Status getHelpRequestStatus()
    {
        return helpRequest.getStatus();
    }

    public HelpRequest getHelpRequest()
    {
        return helpRequest;
    }

    public Code getCode()
    {
        return code;
    }

    public User getUser()
    {
        return user;
    }

    public LessonStructure getLesson()
    {
        return lessonStructure;
    }

    public Role getRole()
    {
        return role;
    }

    public List<ChatMessage> getChatHistory(){
        return chatHistory;
    }

    public List<ChatMessage> addMessageToChatHistory(ChatMessage newMessage){
        chatHistory.add(newMessage);
        return chatHistory;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Attendance that = (Attendance) o;
        return user.equals(that.user) && lessonStructure.equals(that.lessonStructure);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(user, lessonStructure);
    }
}
