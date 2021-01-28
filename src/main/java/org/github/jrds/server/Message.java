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

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((from == null) ? 0 : from.hashCode());
      result = prime * result + ((to == null) ? 0 : to.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Message other = (Message) obj;
      if (from == null) {
        if (other.from != null)
          return false;
      } else if (!from.equals(other.from))
        return false;
      if (to == null) {
        if (other.to != null)
          return false;
      } else if (!to.equals(other.to))
        return false;
      return true;
    }

    
}
