package com.example.loganpatino.mlspickem;

import com.firebase.client.Firebase;

/**
 * Created by loganpatino on 4/12/16.
 */
public class MyApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
