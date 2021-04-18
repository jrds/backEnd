package org.github.jrds.codi.language.java;

import org.github.jrds.codi.core.language.CodeExecutor;
import org.github.jrds.codi.core.language.LanguageExtension;

public class JavaLanguageExtension implements LanguageExtension
{
    @Override
    public String getLanguage()
    {
        return "java";
    }

    @Override
    public CodeExecutor getCodeExecutor(String codeToExecute)
    {
        return new JavaCodeExecutor(codeToExecute);
    }
}
