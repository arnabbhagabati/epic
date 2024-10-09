package com.kaway.epic.beans;

public class Reply {
    private int id;
    private String author;
    private String text;

    public Reply(int id, String author, String text) {
        this.id = id;
        this.author = author;
        this.text = text;
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
}
