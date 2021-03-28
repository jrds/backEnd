package org.github.jrds.server.extensions.code;

import org.github.jrds.server.Main;
import org.github.jrds.server.domain.*;
import org.github.jrds.server.messages.MessageSocket;
import org.github.jrds.server.messages.MessagingExtension;
import org.github.jrds.server.messages.Request;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CodeMessageExtension implements MessagingExtension
{
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private Object currentProcessLock = new Object();
    private ExecuteCodeProcess currentProcess;
    private MessageSocket currentProcessMessageSocket;
    private User currentProcessFrom;
    private ScheduledFuture<?> currentProcessScheduledFuture;

    @Override
    public void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket)
    {
        User from = Main.defaultInstance.usersStore.getUser(request.getFrom());
        Attendance attendance = activeLesson.getAttendance(from);

        if ( attendance != null)
        {
            if (request instanceof ExecuteCodeMessage)
            {
                handleExecuteCodeMessage((ExecuteCodeMessage) request, attendance, messageSocket);
            }
            else
            {
                handleTerminateExecutionMessage((TerminateExecutionMessage) request, attendance, messageSocket);
            }
        }
        else
        {
            throw new IllegalStateException("No registered attendance for this user");
        }
    }

    private void handleExecuteCodeMessage(ExecuteCodeMessage message, Attendance attendance, MessageSocket messageSocket)
    {
        String codeToExecute = message.getCodeToExecute();
        Code code = attendance.getCode();
        User from = attendance.getUser();

        code.setCode(codeToExecute);
        code.executeCode();

        ExecuteCodeProcess process = attendance.getCode().getExecuteCodeProcess();
        if (process != null)
        {
            if (process.getStatus() == ExecutionStatus.COMPILE_FAILED)
            {
                messageSocket.sendMessage(new ExecuteProcessMessage(from.getId(), process.getStatus().toString(), process.getCompilationErrors(), process.getTimeCompiled().toString()));
            }
            else if (process.getStatus() == ExecutionStatus.EXECUTION_FAILED_TO_START)
            {
                messageSocket.sendMessage(new ExecuteProcessMessage(from.getId(), process.getStatus().toString(), "", process.getTimeExecutionStarted().toString()));
            }
            else if (process.getStatus() == ExecutionStatus.EXECUTION_IN_PROGRESS)
            {
                messageSocket.sendMessage(new ExecuteProcessMessage(from.getId(), process.getStatus().toString(), process.getUnreadOutput(), process.getTimeExecutionStarted().toString()));
                synchronized (currentProcessLock)
                {
                    currentProcess = process;
                    currentProcessMessageSocket = messageSocket;
                    currentProcessFrom = from;
                    currentProcessScheduledFuture = scheduler.scheduleAtFixedRate(this::checkProcess,1, 1, TimeUnit.SECONDS);
                }
            }
            else if (process.getStatus() == ExecutionStatus.EXECUTION_FINISHED)
            {
                messageSocket.sendMessage(new ExecuteProcessMessage(from.getId(), process.getStatus().toString(), process.getUnreadOutput(), process.getTimeExecutionEnded().toString()));
            }
        }
    }

    private void handleTerminateExecutionMessage(TerminateExecutionMessage message, Attendance attendance, MessageSocket messageSocket)
    {
        attendance.getCode().terminateExecutionProcess();
    }

    private void checkProcess()
    {
        synchronized (currentProcessLock)
        {
            try
            {
                if (!currentProcessScheduledFuture.isCancelled())
                {
                    String executionStatus = currentProcess.getStatus().toString();
                    String unreadOutput = currentProcess.getUnreadOutput();
                    if (currentProcess.getStatus() == ExecutionStatus.EXECUTION_IN_PROGRESS && !unreadOutput.isEmpty())
                    {
                        currentProcessMessageSocket.sendMessage(new ExecuteProcessMessage(currentProcessFrom.getId(), executionStatus, unreadOutput, Instant.now().toString()));
                    }
                    else if (currentProcess.getStatus() == ExecutionStatus.EXECUTION_FINISHED)
                    {
                        currentProcessMessageSocket.sendMessage(new ExecuteProcessMessage(currentProcessFrom.getId(), executionStatus, unreadOutput, currentProcess.getTimeExecutionEnded().toString()));
                        currentProcessScheduledFuture.cancel(false);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Class<?>> getRequestTypes()
    {
        return Arrays.asList(ExecuteCodeMessage.class, TerminateExecutionMessage.class);
    }
}
