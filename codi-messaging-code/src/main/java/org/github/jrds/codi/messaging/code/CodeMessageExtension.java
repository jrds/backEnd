package org.github.jrds.codi.messaging.code;

import org.github.jrds.codi.core.domain.*;
import org.github.jrds.codi.core.messages.MessageSocket;
import org.github.jrds.codi.core.messages.MessagingExtension;
import org.github.jrds.codi.core.messages.Request;
import org.github.jrds.codi.core.persistence.PersistenceServices;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CodeMessageExtension implements MessagingExtension
{
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final PersistenceServices persistenceServices;

    private Object currentProcessLock = new Object();
    private ExecuteCodeProcess currentProcess;
    private MessageSocket currentProcessMessageSocket;
    private User currentProcessFrom;
    private ScheduledFuture<?> currentProcessScheduledFuture;

    public CodeMessageExtension()
    {
        persistenceServices = ServiceLoader.load(PersistenceServices.class).findFirst().orElseThrow();
    }

    @Override
    public void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket)
    {
        User from = persistenceServices.getUsersStore().getUser(request.getFrom());
        Attendance attendance = activeLesson.getAttendance(from);

        if ( attendance != null)
        {
            if (request instanceof ExecuteCodeRequest)
            {
                handleExecuteCodeMessage((ExecuteCodeRequest) request, attendance, messageSocket);
            }
            else if (request instanceof CodeExecutionInputRequest)
            {
                handleCodeExecutionInput((CodeExecutionInputRequest) request, attendance, messageSocket);
            }
            else if (request instanceof UpdateLiveCodeRequest)
            {
                handleLiveCodeRequest((UpdateLiveCodeRequest) request, attendance, messageSocket);
            }
            else
            {
                handleTerminateExecutionMessage((TerminateExecutionRequest) request, attendance, messageSocket);
            }
        }
        else
        {
            throw new IllegalStateException("No registered attendance for this user");
        }
    }

    private void handleLiveCodeRequest(UpdateLiveCodeRequest message, Attendance attendance, MessageSocket messageSocket)
    {
        String latestCode = message.getlatestCode();
        User learner = attendance.getUser();
        User educator = attendance.getLessonStructure().getEducator();

        messageSocket.sendMessage(new LatestLearnerCodeInfo(educator.getId(), learner.getId(), latestCode));

    }

    private void handleExecuteCodeMessage(ExecuteCodeRequest message, Attendance attendance, MessageSocket messageSocket)
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
                messageSocket.sendMessage(new CodeExecutionInfo(from.getId(), process.getStatus().toString(), "", process.getCompilationErrors(), process.getTimeCompiled().toString()));
            }
            else if (process.getStatus() == ExecutionStatus.EXECUTION_FAILED_TO_START)
            {
                messageSocket.sendMessage(new CodeExecutionInfo(from.getId(), process.getStatus().toString(), "", "", process.getTimeExecutionStarted().toString()));
            }
            else if (process.getStatus() == ExecutionStatus.EXECUTION_IN_PROGRESS)
            {
                String[] outputs = process.getUnreadOutput();
                messageSocket.sendMessage(new CodeExecutionInfo(from.getId(), process.getStatus().toString(), outputs[ExecuteCodeProcess.STD_OUT], outputs[ExecuteCodeProcess.STD_ERR], process.getTimeExecutionStarted().toString()));
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
                String[] outputs = process.getUnreadOutput();
                messageSocket.sendMessage(new CodeExecutionInfo(from.getId(), process.getStatus().toString(), outputs[ExecuteCodeProcess.STD_OUT], outputs[ExecuteCodeProcess.STD_ERR], process.getTimeExecutionEnded().toString()));
            }
        }
    }

    private void handleCodeExecutionInput(CodeExecutionInputRequest request, Attendance attendance, MessageSocket messageSocket)
    {
        attendance.getCode().acceptInput(request.getInput());
    }

    private void handleTerminateExecutionMessage(TerminateExecutionRequest message, Attendance attendance, MessageSocket messageSocket)
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
                    String[] unreadOutputs = currentProcess.getUnreadOutput();
                    if (currentProcess.getStatus() == ExecutionStatus.EXECUTION_IN_PROGRESS && (!unreadOutputs[ExecuteCodeProcess.STD_OUT].isEmpty() || !unreadOutputs[ExecuteCodeProcess.STD_ERR].isEmpty()))
                    {
                        currentProcessMessageSocket.sendMessage(new CodeExecutionInfo(currentProcessFrom.getId(), executionStatus, unreadOutputs[ExecuteCodeProcess.STD_OUT], unreadOutputs[ExecuteCodeProcess.STD_ERR], Instant.now().toString()));
                    }
                    else if (currentProcess.getStatus() == ExecutionStatus.EXECUTION_FINISHED)
                    {
                        currentProcessMessageSocket.sendMessage(new CodeExecutionInfo(currentProcessFrom.getId(), executionStatus, unreadOutputs[ExecuteCodeProcess.STD_OUT], unreadOutputs[ExecuteCodeProcess.STD_ERR], currentProcess.getTimeExecutionEnded().toString()));
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
        return Arrays.asList(ExecuteCodeRequest.class, TerminateExecutionRequest.class, CodeExecutionInputRequest.class, UpdateLiveCodeRequest.class);
    }

    @Override
    public void userJoined(User user, ActiveLesson activeLesson, Role userRole, MessageSocket messageSocket)
    {

    }

    @Override
    public void userLeft(User user, ActiveLesson activeLesson, Role userRole)
    {

    }
}
