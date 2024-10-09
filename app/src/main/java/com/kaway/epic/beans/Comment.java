package com.kaway.epic.beans;

import java.util.List;

public class Comment {
    private int id;
    private String author;
    private String text;
    private List<Reply> replies;

    public Comment(int id, String author, String text, List<Reply> replies) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.replies = replies;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public List<Reply> getReplies() {
        return replies;
    }
}

