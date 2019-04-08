package com.yang.ylnote.bean;
import java.io.Serializable;
public class Following implements Serializable{
    private String following_img;
    private String following_name;
    private String contentsText;

    //    public Following(int Following_img, String Following_name) {
//        this.Following_img = Following_img;
//       this.Following_name = Following_name;
//        this.contentsText = contentsText;
//        this.Following_img=Following_img;
//        this.Following_name="";
//    }
    public Following() {
        //        this.Following_img = Following_img;
        //        this.Following_name = Following_name;
        //        this.contentsText = contentsText;
        this.following_img="";
        this.following_name="";
    }

    public String getfollowing_img() {
        return following_img;
    }
    public void setFollowing_img(String following_img) {
        this.following_img = following_img;
    }
    public String getContentsText() {
        contentsText="Following";
        return contentsText;
    }

    public String getFollowing_name() {
        return following_name;
    }

    public void setFollowing_name(String Following_name) {
        this.following_name = Following_name;
    }
    //    public String getUsername() {
//        return username;
//    }
}
