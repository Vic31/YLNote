package com.yang.ylnote.util;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yang.ylnote.PostActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionActivity extends AppCompatActivity {
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    private boolean checkPermissions(){
        int result;
        List listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
                Log.w("-----------", "not granted permission- " + p);
            } else {
                Log.w("-----------", "granted permission" + p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, (String [])listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 100);
            return false;
        } else {
            startActivity(new Intent(this, PostActivity.class));
            finish();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
                startActivity(new Intent(PermissionActivity.this, PostActivity.class));
                finish();
            }
            return;
        }
    }

}
