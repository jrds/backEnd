package org.github.jrds.server.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Code
{
    private static final Pattern CLASS = Pattern.compile(".*?class\\s+([A-Za-z_][A-Za-z_]*)\\s*\\{.*");
    private String code = "";
    private Path codeDirectory;
    private List<CompiledCode> allCompiledCode = new ArrayList();
    private ExecuteCodeProcess executeCodeProcess;

    public Code()
    {
        try
        {
            codeDirectory = Files.createTempDirectory("codi");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public List<CompiledCode> getAllCompiledCode()
    {
        return allCompiledCode;
    }

    public ExecuteCodeProcess getExecuteCodeProcess()
    {
        return executeCodeProcess;
    }

    public CompiledCode getLatestCompiledCode(){
        if (allCompiledCode.size() > 0)
        {
            return allCompiledCode.get(allCompiledCode.size()-1);
        }
        else
        {
            return null;
        }
    }

    public void executeCode()
    {
        // TODO check existing
        executeCodeProcess = new ExecuteCodeProcess(code, codeDirectory);
        executeCodeProcess.startExecution();
    }

    public void terminateExecutionProcess()
    {
        if (executeCodeProcess != null)
        {
            executeCodeProcess.terminate();
        }
    }
}
