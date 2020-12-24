package com.example.myblog;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class myBlog extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
