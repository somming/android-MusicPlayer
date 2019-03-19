package com.example.music.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.music.model.Music;

import java.util.ArrayList;

public class MusicServiceInterface {
    private ServiceConnection mServiceConnection;
    private MusicService mService;

    public MusicServiceInterface(Context context) {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((MusicService.MusicServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, MusicService.class)
                .setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void setPlayList(ArrayList<Music> items,boolean appendtop) {
        if (mService != null) {
            mService.setPlayList(items,appendtop);
        }
    }

    public void play(int position) {
        if (mService != null) {
            mService.play(position);
        }
    }

    public void play() {
        if (mService != null) {
            mService.play();
        }
    }

    public void pause() {
        if (mService != null) {
            mService.play();
        }
    }

    public void forward() {
        if (mService != null) {
            mService.forward();
        }
    }

    public void rewind() {
        if (mService != null) {
            mService.rewind();
        }
    }

    public void togglePlay() {
        if (isPlaying()) {
            mService.pause();
        } else {
            mService.play();
        }
    }

    public boolean isPlaying() {
        if (mService != null) {
            return mService.isPlaying();
        }
        return false;
    }

    public Music getMusicItem() {
        if (mService != null) {
            return mService.getMusicItem();
        }
        return null;
    }

    public int getCurseek() {
        if (mService != null) {
            return mService.getCurseek();
        }
        return 0;
    }

    public int getDuration() {
        if (mService != null) {
            return mService.getDuration();
        }
        return 0;
    }

    public int getmPosition(){
        if (mService != null) {
            return mService.getmPosition();
        }
        return -1;
    }

    public int setMusicseek(int curseek){
        if (mService != null) {
            return mService.setMusicseek(curseek);
        }
        return 0;
    }

    public ArrayList<Music> getPlaylist(){
        return mService.getPlaylist();
    }

    public int setRepeatOption(){
        if (mService != null) {
            return mService.setRepeatOption();
        }
        return 0;
    }

    public boolean toggleShuffle(){
        if (mService != null) {
            return mService.toggleShuffle();
        }
        return false;
    }
}
