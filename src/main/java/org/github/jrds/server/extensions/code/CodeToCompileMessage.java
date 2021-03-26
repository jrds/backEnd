package org.github.jrds.server.extensions.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Request;

public class CodeToCompileMessage extends Request
{
    private final String codeToCompile;

    @JsonCreator
    public CodeToCompileMessage(
            @JsonProperty("from") String from,
            @JsonProperty("codeToCompile") String codeToCompile)
    {
        super(from, null);
        this.codeToCompile = codeToCompile;
    }

    public String getCodeToCompile()
    {
        return codeToCompile;
    }
}
