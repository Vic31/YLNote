package com.yang.ylnote.bean;

import java.io.Serializable;

public class User implements Serializable{

    public User(){}

    public User(String username, String pwd){
        this.username = username;
        this.pwd = pwd;
        this.url = "";
        this.id = username+System.currentTimeMillis();
    }
    /**
     * follow : 12
     * followed_by : 15
     * id : yy01181001
     * like : 10
     * pwd : 123123
     * post_list: ""
     * url : https://ylnote-8a903.firebaseio.com/
     * username : 12y
     */

    private int follow = 0;
    private int followed_by = 0;
    private String id;
    private int like = 0;
    private String pwd;
    private String url;
    private String username;
    private String post_list;

    public void setPost_list(String post_list) {
        this.post_list = post_list;
    }

    public String getPost_list() {
        return post_list;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public int getFollowed_by() {
        return followed_by;
    }

    public void setFollowed_by(int followed_by) {
        this.followed_by = followed_by;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd() {
        return pwd;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
