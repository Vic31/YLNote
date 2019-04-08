package com.yang.ylnote.bean;

import java.io.Serializable;

public class Comment implements Serializable {

    private String commentContent;
    private String commentName;
    public Comment(){}

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentName(String commentName) {
        this.commentName = commentName;
    }

    public String getCommentName() {
        return commentName;
    }
}
