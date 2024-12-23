package com.kaway.epic.beans;

import java.util.List;

public class Comment {
    private int id;
    private String author;
    private String text;
    private List<Reply> replies;

    private String profileIconUrl;
    private String commentDate;
    private String likeCount;

    public Comment(int id, String author,
                            String text,
                            List<Reply> replies,
                            String profileIconUrl,
                            String commentDate,
                            String likeCount) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.replies = replies;
        this.profileIconUrl = profileIconUrl;
        this.commentDate = commentDate;
        this.likeCount = likeCount;
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

    public String getProfileIconUrl() {
        return profileIconUrl;
    }

    public String getCommentDate() {
        return commentDate;
    }
    public String getLikeCount() {
        return likeCount;
    }
}

