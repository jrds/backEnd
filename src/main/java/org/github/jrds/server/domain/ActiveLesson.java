package org.github.jrds.server.domain;

import org.github.jrds.server.dto.InstructionDto;
import org.github.jrds.server.extensions.lesson.ActiveLessonState;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class ActiveLesson
{
    private final String id;
    private final LessonStructure associatedLessonStructure;
    private final List<Attendance> activeLessonAttendance = new ArrayList<>();
    private final Map<String, HelpRequest> openHelpRequests = new HashMap<>();
    private ActiveLessonState activeLessonState = ActiveLessonState.NOT_STARTED;
    private List<InstructionDto> instructionsSent = new ArrayList<>();

    public ActiveLesson(LessonStructure associatedLessonStructure)
    {
        this.id = associatedLessonStructure.getId();
        this.associatedLessonStructure = associatedLessonStructure;
    }

    public String getId()
    {
        return id;
    }

    public LessonStructure getAssociatedLessonStructure()
    {
        return associatedLessonStructure;
    }

    public Attendance registerAttendance(User user)
    {
        Attendance attendanceToRegister = new Attendance(user, associatedLessonStructure);
        activeLessonAttendance.add(attendanceToRegister);
        return attendanceToRegister;
    }

    public List<Attendance> getActiveLessonAttendances()
    {
        return Collections.unmodifiableList(activeLessonAttendance);
    }

    public void removeAttendance(Attendance attendanceToRemove)
    {
        activeLessonAttendance.remove(attendanceToRemove);
    }

    public Attendance getAttendance(User user)
    {

        if (activeLessonAttendance.stream().noneMatch(attendance -> attendance.getUser().equals(user)))
        {
            throw new IllegalStateException("No attendance registered for this user");
        }
        else
        {
            return activeLessonAttendance.stream().filter(attendance -> attendance.getUser().equals(user)).findFirst().get();
        }
    }

    public Map<String, HelpRequest> getOpenHelpRequests()
    {
        return Collections.unmodifiableMap(openHelpRequests);
    }

    public void addHelpRequest(HelpRequest helpRequest)
    {
        openHelpRequests.put(helpRequest.getLearner().getId(), helpRequest);
    }

    public void removeHelpRequest(HelpRequest helpRequest)
    {
        openHelpRequests.remove(helpRequest.getLearner().getId());
    }

    public void updateHelpRequest(HelpRequest helpRequest, Status newStatus)
    {
        openHelpRequests.get(helpRequest.getLearner()).setStatus(newStatus);
    }

    public ActiveLessonState getActiveLessonState()
    {
        return activeLessonState;
    }

    public void setActiveLessonState(ActiveLessonState activeLessonState)
    {
        this.activeLessonState = activeLessonState;
    }

    public List<InstructionDto> getInstructionsSent()
    {
        return instructionsSent;
    }

    public void addToInstructionSent(InstructionDto i)
    {
        instructionsSent.add(i);
    }
}