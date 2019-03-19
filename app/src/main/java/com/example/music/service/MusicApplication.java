package com.example.music.service;

import android.app.Application;

public class MusicApplication extends Application {
    private static MusicApplication mInstance;
    private MusicServiceInterface mInterface;
    public static boolean MusicPlayerFragmentAlive = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mInterface = new MusicServiceInterface(getApplicationContext());
    }

    public static MusicApplication getInstance() {
        return mInstance;
    }

    public MusicServiceInterface getServiceInterface() {
        return mInterface;
    }
}
