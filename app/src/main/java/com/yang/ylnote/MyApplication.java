package com.yang.ylnote;

import android.app.Application;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class MyApplication extends Application {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }

    public static FirebaseDatabase getDBInstance(){
        return FirebaseDatabase.getInstance();
    }
    public static FirebaseStorage getStoreInstance() {return FirebaseStorage.getInstance(); }
}
