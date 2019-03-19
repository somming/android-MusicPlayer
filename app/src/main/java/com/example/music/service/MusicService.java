package com.example.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.music.etc.BroadcastActions;
import com.example.music.etc.CommandActions;
import com.example.music.etc.NotificationPlayer;
import com.example.music.model.Music;

import java.util.ArrayList;

public class MusicService extends Service {
    private static final String TAG = MusicService.class.getSimpleName();
    private final IBinder mBinder = new MusicServiceBinder();
    private MediaPlayer mPlayer;
    private boolean isPrepared;
    private ArrayList<Music> mMusicList = new ArrayList<>();
    private int mPosition;
    private int curseek;
    private Music mMusicItem;
    private NotificationPlayer mNotificationPlayer;
    private int repeatoption=0; //0: 반복업음 1: 한곡반복 2: 전체반복
    private boolean shuffle=false;

    public class MusicServiceBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationPlayer = new NotificationPlayer(this);
        mPlayer = new MediaPlayer();
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d("onPrepared : ","준비");
                isPrepared = true;
                mp.start();
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
                updateNotificationPlayer();
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("onComplete : ","완료");
                isPrepared = false;

                if(repeatoption==1){
                    play(mPosition);
                }else if(shuffle){
                    play(shuffle());
                }else if(repeatoption==0 && mPosition==mMusicList.size()-1){
                    stop();
                }else{
                    forward();
                }
            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPrepared = false;
                Log.d("onError : ","에러");
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
                updateNotificationPlayer();
                return false;
            }
        });
        mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });
    }

    private void prepare() {
        try {
            mPlayer.setDataSource(mMusicItem.getLink());
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        mPlayer.stop();
        mPlayer.reset();
    }

    public void setPlayList(ArrayList<Music> items,boolean appendtop) {
        Log.d("플레이리스트 설정 : ",items.size()+"개");
        if(appendtop){
            mMusicList.addAll(0,items);
            play(0);
        }else{
            mMusicList.addAll(items);
        }
        for(int i=0;i<mMusicList.size();i++){
            Log.d("리스트 내 음악 : ",mMusicList.get(i).getTitle()+"");
        }
    }

    //나의 재생리스트에서 클릭했을 때, 해당 음악 재생
    public void play(int position) {
        if(mMusicItem!=null){
            mMusicItem.setPlay(false);
        }
        mPosition=position;
        mMusicItem = mMusicList.get(position);
        mMusicItem.setPlay(true);
        stop();
        prepare();
    }

    //음악 컨트롤
    public void play() {
        if (isPrepared) {
            mPlayer.start();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
            updateNotificationPlayer();
        }else{
            prepare();
        }
    }

    public void pause() {
        if (isPrepared) {
            mPlayer.pause();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
            updateNotificationPlayer();
        }
    }

    public void forward() {
        if(shuffle){//셔플
            mPosition = shuffle();
        }else if (mMusicList.size() - 1 > mPosition) {
            mPosition++; // 다음 포지션으로 이동.
        }else {
            mPosition = 0; // 처음 포지션으로 이동.
        }
        play(mPosition);
    }

    public void rewind() {
        if (mPosition > 0) {
            mPosition--; // 이전 포지션으로 이동.
        } else {
            mPosition = mMusicList.size() - 1; // 마지막 포지션으로 이동.
        }
        play(mPosition);
    }

    public Music getMusicItem() {
        return mMusicItem;
    }

    public int getCurseek(){
        curseek = mPlayer.getCurrentPosition();
        return curseek;
    }

    public int getDuration(){
        int duration = mPlayer.getDuration();
        return duration;
    }

    public int setMusicseek(int curseek){
        mPlayer.seekTo(curseek);
        return mPlayer.getCurrentPosition();
    }

    public int setRepeatOption(){
        repeatoption=repeatoption+1;
        if(repeatoption>2){
            repeatoption=0;
        }
        return repeatoption;
    }

    public boolean toggleShuffle(){
        shuffle = !shuffle;

        return shuffle;
    }

    public int shuffle(){
        int random = (int)(Math.random() * mMusicList.size());

        return random;
    }

    public int getmPosition(){
        return mPosition;
    }

    public ArrayList<Music> getPlaylist(){
        return mMusicList;
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    private void updateNotificationPlayer() {
        if (mNotificationPlayer != null) {
            mNotificationPlayer.updateNotificationPlayer();
        }
    }

    private void removeNotificationPlayer() {
        if (mNotificationPlayer != null) {
            mNotificationPlayer.removeNotificationPlayer();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (CommandActions.TOGGLE_PLAY.equals(action)) {
                if (isPlaying()) {
                    pause();
                } else {
                    play();
                }
            } else if (CommandActions.REWIND.equals(action)) {
                rewind();
            } else if (CommandActions.FORWARD.equals(action)) {
                forward();
            } else if (CommandActions.CLOSE.equals(action)) {
                pause();
                removeNotificationPlayer();
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;

        }
    }
}
