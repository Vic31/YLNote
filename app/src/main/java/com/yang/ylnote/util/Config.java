package com.yang.ylnote.util;

import java.security.PublicKey;

public class Config {
    //picture choose
    public static final int CAMERA = 0;
    public static final int GALLERY = 1;
    public static final int PHOTO_CROP = 2;
    public static String upLoadImageTypes = "";
    public static String uploadImageName = "";

    public static final int UPDATE_POST_LIST = 10001;
    public static final int UPDATE_COMMENT_LIST = 10002;
    public static final int UPDATE_COLLECT_LIKE_LIST = 10003;
    public static final int UPDATE_FOLLOW_LIST = 10004;
    public static final int REQUEST_PERMISSION = 100;

    public static final String LIKED_LIST = "LIKED_LIST";
    public static final String COLLECT_LIST = "COLLECT_LIST";
    public static final String NOTE_LIST = "NOTE_LIST";
}
