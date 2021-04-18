package org.github.jrds.codi.core.language;

public interface LanguageExtension
{
    String getLanguage();
    CodeExecutor getCodeExecutor(String codeToExecute);
}
