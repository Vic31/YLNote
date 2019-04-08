package com.yang.ylnote.bean;

import java.io.Serializable;

public class NoteDetail implements Serializable{

    /**
     * collect_num : 7
     * comment_id : comment_20181201203210
     * comment_num : 503
     * like_num : 10
     * note_id : note20181201203210
     * pic : yyang31_1201_203201_post.jpg
     * post_time : 2018-12-01 20h
     * text : This is my hand. \n()
     * username : yyang31
     */

    public NoteDetail(){
        collect_num = 0;
        comment_num = 0;
        like_num = 0;
        pic = "";
    }
    private int collect_num;
    private String comment_id;
    private int comment_num;
    private int like_num;
    private String note_id;
    private String pic;
    private String post_time;
    private String text;
    private String username;

    public int getCollect_num() {
        return collect_num;
    }

    public void setCollect_num(int collect_num) {
        this.collect_num = collect_num;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public int getLike_num() {
        return like_num;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public String getNote_id() {
        return note_id;
    }

    public void setNote_id(String note_id) {
        this.note_id = note_id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
