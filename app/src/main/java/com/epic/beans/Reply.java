package com.epic.beans;

public class Reply {
    private int id;
    private String author;
    private String text;
    private String profileIcon;
    private String replyDate;
    private String likeCount;

    public Reply(int id, String author, String text, String profileIcon, String replyDate,String likeCount) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.profileIcon = profileIcon;
        this.replyDate = replyDate;
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

    public String getProfileIcon() {
        return profileIcon;
    }

    public String getReplyDate() {
        return replyDate;
    }

    public String getLikeCount() {
        return likeCount;
    }
}
