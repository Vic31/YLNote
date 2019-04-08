package com.yang.ylnote.bean;

public class Follower {
    private String follower_img;
    private String follower_name;
    private String contentsText;

    public Follower() {
        this.follower_img="";
        this.follower_name="";
    }

    public String getFollower_img() {
        return follower_img;
    }
    public void setFollower_img(String following_img) {
        this.follower_img = following_img;
    }
    public String getContentsText() {
        contentsText="Follow";
        return contentsText;
    }


    public String getFollower_name() {
        return follower_name;
    }

    public void setFollower_name(String Following_name) {
        this.follower_name = Following_name;
    }
    //    public String getUsername() {
//        return username;
//    }
}
