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
    private final SortedSet<HelpRequest> openHelpRequests = new ConcurrentSkipListSet<>();
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

    public Set<HelpRequest> getOpenHelpRequests()
    {
        return Collections.unmodifiableSet(openHelpRequests);
    }

    public void addHelpRequest(HelpRequest helpRequest)
    {
        openHelpRequests.add(helpRequest);
    }

    public void removeHelpRequest(HelpRequest toUpdate)
    {
        openHelpRequests.remove(toUpdate);
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