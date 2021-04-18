package org.github.jrds.codi.core.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Code
{
    private String code = "";
    private List<CompiledCode> allCompiledCode = new ArrayList();

    public void setCode(String code)
    {
        this.code = code;
    }

    public void addCompiledCode(CompiledCode compiledCode)
    {
        allCompiledCode.add(compiledCode);
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
}
