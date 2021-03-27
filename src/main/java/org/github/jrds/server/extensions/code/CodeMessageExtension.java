package org.github.jrds.server.extensions.code;

import org.github.jrds.server.Main;
import org.github.jrds.server.domain.*;
import org.github.jrds.server.messages.MessageSocket;
import org.github.jrds.server.messages.MessagingExtension;
import org.github.jrds.server.messages.Request;

import java.util.Collections;
import java.util.List;

public class CodeMessageExtension implements MessagingExtension
{
    @Override
    public boolean handles(Request request)
    {
        return request instanceof CodeToCompileMessage;
    }

    @Override
    public void handle(Request request, ActiveLesson activeLesson, MessageSocket messageSocket)
    {
        User from = Main.defaultInstance.usersStore.getUser(request.getFrom());
        Attendance attendance = activeLesson.getAttendance(from);

        if ( attendance != null)
        {
            String codeToCompile = ((CodeToCompileMessage) request).getCodeToCompile();
            Code code = attendance.getCode();

            code.setCode(codeToCompile);
            code.executeCode();

            ExecuteCodeProcess process = attendance.getCode().getExecuteCodeProcess();
            if (process != null)
            {
                if (process.getStatus() == ExecutionStatus.COMPILE_FAILED)
                {
                    messageSocket.sendMessage(new CompiledCodeMessage(from.getId(), process.getStatus().toString(), process.getCompilationErrors(), process.getTimeCompiled().toString()));
                }
                else if (process.getStatus() == ExecutionStatus.EXECUTION_FAILED_TO_START)
                {
                    messageSocket.sendMessage(new CompiledCodeMessage(from.getId(), process.getStatus().toString(), "", process.getTimeExecutionStarted().toString()));
                }
                else if (process.getStatus() == ExecutionStatus.EXECUTION_STARTED)
                {
                    messageSocket.sendMessage(new CompiledCodeMessage(from.getId(), process.getStatus().toString(), process.getOutput(), process.getTimeExecutionStarted().toString()));
                }
                else if (process.getStatus() == ExecutionStatus.EXECUTION_FINISHED)
                {
                    messageSocket.sendMessage(new CompiledCodeMessage(from.getId(), process.getStatus().toString(), process.getOutput(), process.getTimeExecutionStarted().toString()));
                }
            }
        }
        else
        {
            throw new IllegalStateException("No registered attendance for this user");
        }
    }

    @Override
    public List<Class<?>> getRequestTypes()
    {
        return Collections.singletonList(CodeToCompileMessage.class);
    }
}
