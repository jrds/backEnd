package org.github.jrds.server;

public class Message {
    
    private String from;
    private String to;
    private String text;

    public Message() {
        // For Jackson deserialisation
    }

    public Message(String from, String to, String msg) {
        this.from = from;
        this.to = to;
        this.text = msg;
    }

	public String getFrom() {
		return from;
	}

	public String getText() {
		return text;
	}

    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "Message [from=" + from  + ", to=" + to + ", text=" + text+ "]";
    }

    
}
