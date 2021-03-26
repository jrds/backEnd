package org.github.jrds.server.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Code
{
    private static final Pattern CLASS = Pattern.compile(".*?class\\s+([A-Za-z_][A-Za-z_]*)\\s*\\{.*");
    private String code = "";
    private Path codeDirectory;
    //private String compilationResult;
    //private String compilationError;
    //private String executionError;
    private List<CompiledCode> allCompiledCode = new ArrayList();
    // instead of capturing the strings, call get(AllCompiledCode.length()-1) then there's accesss to the state.

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

    public void compileCode()
    {
        long t0 = System.currentTimeMillis();
        Matcher matcher = CLASS.matcher(code);
        if (matcher.matches())
        {
            try
            {
                Path file = codeDirectory.resolve(matcher.group(1) + ".java");
                Files.writeString(file, code);
                // Path to javac should be an application configuration
                Process compile = new ProcessBuilder().command("c:\\Program Files\\AdoptOpenJDK\\jdk-11.0.9.101-hotspot\\bin\\javac", file.toString()).directory(codeDirectory.toFile()).start();
                int compileResult = compile.waitFor();
                if (compileResult == 0)
                {
                    System.out.println("Compile worked");
                    Process execute = new ProcessBuilder().command("c:\\Program Files\\AdoptOpenJDK\\jdk-11.0.9.101-hotspot\\bin\\java", matcher.group(1)).directory(codeDirectory.toFile()).start();
                    int executeResult = compile.waitFor();
                    if (executeResult == 0)
                    {
                        System.out.println("Execute worked");
                        String stdOut = new BufferedReader(new InputStreamReader(execute.getInputStream()))
                                .lines()
                                .collect(Collectors.joining("\n"));
                        System.out.println(stdOut);
                        //compilationResult = stdOut;
                        allCompiledCode.add(new CompiledCode(code, stdOut, CompilationStatus.COMPILED_SUCCESSFULLY));
                    }
                    else
                    {
                        System.out.println("Execute failed");
                        String stdErr = new BufferedReader(new InputStreamReader(compile.getErrorStream()))
                                .lines()
                                .collect(Collectors.joining("\n"));
                        System.out.println(stdErr);
                        //executionError = stdErr;
                        allCompiledCode.add(new CompiledCode(code,stdErr, CompilationStatus.EXECUTION_ERROR));
                    }
                }
                else
                {
                    System.out.println("Compile failed");
                    String stdErr = new BufferedReader(new InputStreamReader(compile.getErrorStream()))
                            .lines()
                            .collect(Collectors.joining("\n"));
                    System.out.println(stdErr);
                    //compilationError = stdErr;
                    allCompiledCode.add(new CompiledCode(code,stdErr, CompilationStatus.COMPILATION_ERROR));
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            throw new IllegalArgumentException("Not a valid class definition");
        }
        System.out.println("Time taken: " + (System.currentTimeMillis() - t0));
    }

}
