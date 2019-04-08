package com.yang.ylnote.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.yang.ylnote.BuildConfig;
import com.yang.ylnote.PostActivity;

import java.io.File;

public class Util {
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    //default two clicks cannot within 1 seconds
    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    //check if click too many times in few seconds
    public static boolean isFastClick(int milliseconds) {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= milliseconds) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    public static void openCamera(Activity context){
        // 启动系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri mImageCaptureUri;
        // 判断7.0android系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            PostActivity.uri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID+".provider",
                    new File(Environment.getExternalStorageDirectory(), "temp.jpg"));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, PostActivity.uri);
        } else {
            mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg"));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        }
        context.startActivityForResult(intent, Config.CAMERA);
    }

    /**
     * delete temperory picture
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return true;
    }

    public static void updateFile(Context context,String filepath){
        String where=MediaStore.Audio.Media.DATA+" like \""+filepath+"%"+"\"";
        int i=  context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,where,null);
        if(i>0){
            Log.e("delete picture","delete success！");
        }
    }
}
