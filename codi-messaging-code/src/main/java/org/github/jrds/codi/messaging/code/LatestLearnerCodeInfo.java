package org.github.jrds.codi.messaging.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.codi.core.messages.Info;

public class LatestLearnerCodeInfo extends Info
{
    private final String learner;
    private final String latestCode;

    @JsonCreator
    public LatestLearnerCodeInfo(
        @JsonProperty("to") String to,
        @JsonProperty("learner") String learner,
        @JsonProperty("latestCode") String latestCode)
    {
        super(to);
        this.learner = learner;
        this.latestCode = latestCode;
    }

    public String getLearner()
    {
        return learner;
    }

    public String getLatestCode()
    {
        return latestCode;
    }
}
