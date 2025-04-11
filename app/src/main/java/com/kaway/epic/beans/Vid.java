package com.kaway.epic.beans;

import java.util.List;

public class Vid {
    private String title;
    private List<Comment> comments;

    public Vid(String title, List<Comment> comments) {
        this.title = title;
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
