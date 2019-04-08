package com.yang.ylnote.bean;

import java.io.Serializable;

public class Note implements Serializable {

    /**
     * pic : https://imgc.allpostersimages.com/img/print/plakater/petra-wegner-golden-retriever-dog_a-G-3200654-14258389.jpg
     * post_time : 10/01/2018
     * text : Lyric is a dog!
     * user_url : http://dsjkfn
     * userid : 0000001
     * username : yyang31
     */
    private String note_id;
    private String pic;
    private String post_time;
    private String text;
    private String user_url;
    private String userid;
    private String username;

    public Note() {
        this.text = "";
        this.pic = "";
    }


    public void setNote_id(String note_id) {
        this.note_id = note_id;
    }

    public String getNote_id() {
        return note_id;
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

    public String getUser_url() {
        return user_url;
    }

    public void setUser_url(String user_url) {
        this.user_url = user_url;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
