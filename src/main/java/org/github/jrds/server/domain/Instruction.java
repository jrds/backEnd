package org.github.jrds.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Instruction {

    private String title;
    private String body;
    private User author;


    Instruction(String title, String body, User author) {
        this.title = Objects.requireNonNull(title);
        this.body = body;
        this.author = Objects.requireNonNull(author);
    }


    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    
    void setBody(String body) {
        this.body = body;
    }

    public User getAuthor() {
        return author;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instruction that = (Instruction) o;
        return title.equals(that.title) && author.equals(that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }
}