package org.github.jrds.server;

public class Instruction {

    private String title;
    private String body;
    private User author;

    
    public Instruction(String title, String body, User author) {
        this.title = title;
        this.body = body;
        this.author = author;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getBody() {
        return body;
    }

    
    public void setBody(String body) {
        this.body = body;
    }


    public User getAuthor() {
        return author;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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
        Instruction other = (Instruction) obj;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }


    
}

//TODO - Should this be a class in lesson?