package org.github.jrds.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatMessage extends Message {

    private String text;

    @JsonCreator
    public ChatMessage(
            @JsonProperty("from") String from, 
            @JsonProperty("to") String to,
            @JsonProperty("text") String text) {
        super(from, to);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "ChatMessage [from=" + getFrom() + ", to=" + getTo() + ", text=" + text + "]";
    }

}
