package org.github.jrds.codi.messaging.code;

import org.github.jrds.codi.core.domain.*;
import org.github.jrds.codi.core.language.CodeExecutor;
import org.github.jrds.codi.core.language.ExecuteCodeOutputs;
import org.github.jrds.codi.core.language.LanguageExtension;
import org.github.jrds.codi.core.messages.MessageSocket;
import org.github.jrds.codi.core.messages.MessagingExtension;
import org.github.jrds.codi.core.messages.Request;
import org.github.jrds.codi.core.persistence.PersistenceServices;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CodeMessageExtension implements MessagingExtension
{
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static Map<String, LanguageExtension> languageExtensions = ServiceLoader.load(LanguageExtension.class)
            .stream()
            .map(ServiceLoader.Provider::get)
            .collect(Collectors.toMap(LanguageExtension::getLanguage, Function.identity()));

    private final PersistenceServices persistenceServices;

    private Object currentProcessLock = new Object();
    private CodeExecutor currentExecutor;
    private MessageSocket currentExecutorMessageSocket;
    private User currentExecutorFrom;
    private ScheduledFuture<?> currentExecutorScheduledFuture;

    public CodeMessageExtension()
    {
        persistenceServices = ServiceLoader.load(PersistenceServices.class).findFirst().orElseThrow();
    }

    @Override
    public void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket)
    {
        User from = persistenceServices.getUsersStore().getUser(request.getFrom());
        Attendance attendance = activeLesson.getAttendance(from);
        String language = activeLesson.getAssociatedLessonStructure().getLanguage();
        LanguageExtension languageExtension = Objects.requireNonNull(languageExtensions.get(language), language + " not supported");

        if ( attendance != null)
        {
            if (request instanceof ExecuteCodeRequest)
            {
                handleExecuteCodeMessage((ExecuteCodeRequest) request, attendance, messageSocket, languageExtension);
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

    private void handleExecuteCodeMessage(ExecuteCodeRequest message, Attendance attendance, MessageSocket messageSocket, LanguageExtension languageExtension)
    {
        String codeToExecute = message.getCodeToExecute();
        Code code = attendance.getCode();
        User from = attendance.getUser();

        code.setCode(codeToExecute);
        CodeExecutor executor = languageExtension.getCodeExecutor(codeToExecute);
        executor.execute();

        if (executor.getStatus() == ExecutionStatus.COMPILE_FAILED)
        {
            messageSocket.sendMessage(new CodeExecutionInfo(from.getId(), executor.getStatus().toString(), "", executor.getCompilationErrors(), executor.getTimeCompiled().toString()));
        }
        else if (executor.getStatus() == ExecutionStatus.EXECUTION_FAILED_TO_START)
        {
            messageSocket.sendMessage(new CodeExecutionInfo(from.getId(), executor.getStatus().toString(), "", "", executor.getTimeExecutionStarted().toString()));
        }
        else if (executor.getStatus() == ExecutionStatus.EXECUTION_IN_PROGRESS)
        {
            ExecuteCodeOutputs outputs = executor.getUnreadOutputs();
            messageSocket.sendMessage(new CodeExecutionInfo(from.getId(), executor.getStatus().toString(), outputs.stdOut(), outputs.stdErr(), executor.getTimeExecutionStarted().toString()));
            synchronized (currentProcessLock)
            {
                currentExecutor = executor;
                currentExecutorMessageSocket = messageSocket;
                currentExecutorFrom = from;
                currentExecutorScheduledFuture = scheduler.scheduleAtFixedRate(this::checkExecution,1, 1, TimeUnit.SECONDS);
            }
        }
        else if (executor.getStatus() == ExecutionStatus.EXECUTION_FINISHED)
        {
            ExecuteCodeOutputs outputs = executor.getUnreadOutputs();
            messageSocket.sendMessage(new CodeExecutionInfo(from.getId(), executor.getStatus().toString(), outputs.stdOut(), outputs.stdErr(), executor.getTimeExecutionEnded().toString()));
        }
    }

    private void handleCodeExecutionInput(CodeExecutionInputRequest request, Attendance attendance, MessageSocket messageSocket)
    {
        currentExecutor.input(request.getInput());
    }

    private void handleTerminateExecutionMessage(TerminateExecutionRequest message, Attendance attendance, MessageSocket messageSocket)
    {
        currentExecutor.terminate();
    }

    private void checkExecution()
    {
        synchronized (currentProcessLock)
        {
            try
            {
                if (!currentExecutorScheduledFuture.isCancelled())
                {
                    String executionStatus = currentExecutor.getStatus().toString();
                    ExecuteCodeOutputs outputs = currentExecutor.getUnreadOutputs();
                    if (currentExecutor.getStatus() == ExecutionStatus.EXECUTION_IN_PROGRESS && (!outputs.stdOut().isEmpty() || !outputs.stdErr().isEmpty()))
                    {
                        currentExecutorMessageSocket.sendMessage(new CodeExecutionInfo(currentExecutorFrom.getId(), executionStatus, outputs.stdOut(), outputs.stdErr(), Instant.now().toString()));
                    }
                    else if (currentExecutor.getStatus() == ExecutionStatus.EXECUTION_FINISHED)
                    {
                        currentExecutorMessageSocket.sendMessage(new CodeExecutionInfo(currentExecutorFrom.getId(), executionStatus, outputs.stdOut(), outputs.stdErr(), currentExecutor.getTimeExecutionEnded().toString()));
                        currentExecutorScheduledFuture.cancel(false);
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
