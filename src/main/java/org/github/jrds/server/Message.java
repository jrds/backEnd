package org.github.jrds.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME, 
  include = JsonTypeInfo.As.PROPERTY, 
  property = "_type")
@JsonSubTypes({ 
  @Type(value = ChatMessage.class, name = "chat"), 
  @Type(value = SessionEndMessage.class, name = "sessionEnd") 
})

public abstract class Message {
    
    private String from;
    private String to;

    public Message(String from, String to) {
        this.from = from;
        this.to = to;
    }

	public String getFrom() {
		return from;
	}

	public String getTo() {
        return to;
    }    
}
