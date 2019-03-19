package com.example.music.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.music.R;
import com.example.music.etc.BroadcastActions;
import com.example.music.fragment.MusicListFragment;
import com.example.music.fragment.MusicPlayerFragment;
import com.example.music.model.Music;
import com.example.music.service.MusicApplication;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton btn_backtolist, btn_play_pause, btn_rewind, btn_forward, btn_repeat, btn_shuffle, btn_playlist;
    private boolean isFragmentPlay = true ;
    private int repeatoption=0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        btn_backtolist = findViewById(R.id.btn_backtolist);
        btn_play_pause = findViewById(R.id.btn_play_stop);
        btn_backtolist.setOnClickListener(this);
        btn_play_pause.setOnClickListener(this);

        btn_shuffle = findViewById(R.id.btn_play_shuffle);
        btn_shuffle.setOnClickListener(this);

        btn_repeat = findViewById(R.id.btn_play_repeat);
        btn_repeat.setOnClickListener(this);

        findViewById(R.id.btn_play_forward).setOnClickListener(this);
        findViewById(R.id.btn_play_rewind).setOnClickListener(this);
        btn_playlist=findViewById(R.id.btn_playlist);
        btn_playlist.setOnClickListener(this);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.musicplayer_or_list, new MusicPlayerFragment(),"MusicPlayer");
        fragmentTransaction.commit();

        updateUI();
        registerBroadcast();
    }


    public void switchFragment() {
        Fragment fr;
        String tag="";
        final Bitmap[] bmcover = new Bitmap[1];

        if (isFragmentPlay) {
            fr = new MusicListFragment() ;
            tag = "MusicPlaylist";
            Music item = MusicApplication.getInstance().getServiceInterface().getMusicItem();
            Glide.with(this)
                    .load(item.getCover())
                    .into(btn_playlist);

            btn_playlist.setBackground(new ShapeDrawable(new OvalShape()));
            if(Build.VERSION.SDK_INT >= 21) {
                btn_playlist.setClipToOutline(true);
            }
            //btn_playlist.setImageBitmap(bmcover[0]);
        } else {
            fr = new MusicPlayerFragment() ;
            tag = "MusicPlayer";
            btn_playlist.setImageResource(R.drawable.ic_playlist);
            btn_playlist.setBackgroundResource(0);
        }

        isFragmentPlay = (isFragmentPlay) ? false : true ;
        Log.d("isFragment바뀜? : ",isFragmentPlay+"");

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.musicplayer_or_list, fr,tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_backtolist:
                finish();
                break;
            case R.id.btn_play_forward:
                if(isFragmentPlay){
                    ((MusicPlayerFragment) getFragmentManager().findFragmentByTag("MusicPlayer")).stopThread();
                }
                MusicApplication.getInstance().getServiceInterface().forward();
                break;
            case R.id.btn_play_rewind:
                if(isFragmentPlay){
                    ((MusicPlayerFragment) getFragmentManager().findFragmentByTag("MusicPlayer")).stopThread();
                }
                MusicApplication.getInstance().getServiceInterface().rewind();
                break;
            case R.id.btn_play_stop:
                if(isFragmentPlay){
                    ((MusicPlayerFragment) getFragmentManager().findFragmentByTag("MusicPlayer")).stopThread();
                    ((MusicPlayerFragment) getFragmentManager().findFragmentByTag("MusicPlayer")).togglePlay();
                }
                MusicApplication.getInstance().getServiceInterface().togglePlay();
                break;
            case R.id.btn_play_repeat:
                repeatoption = MusicApplication.getInstance().getServiceInterface().setRepeatOption();

                if(repeatoption==2){
                    btn_repeat.setImageResource(R.drawable.ic_repeat);
                    btn_repeat.setColorFilter(getResources().getColor(R.color.colorAccent));
                }else if(repeatoption==1){
                    btn_repeat.setImageResource(R.drawable.ic_repeat_one);
                    btn_repeat.setColorFilter(getResources().getColor(R.color.colorAccent));
                }else{
                    btn_repeat.setImageResource(R.drawable.ic_repeat);
                    btn_repeat.setColorFilter(getResources().getColor(R.color.basic));
                }
                break;
            case R.id.btn_play_shuffle:
                if(MusicApplication.getInstance().getServiceInterface().toggleShuffle()){
                    btn_shuffle.setColorFilter(getResources().getColor(R.color.colorAccent));
                }else{
                    btn_shuffle.setColorFilter(getResources().getColor(R.color.basic));
                }
                break;
            case R.id.btn_playlist:
                switchFragment() ;
                break;
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
            if(isFragmentPlay){
                ((MusicPlayerFragment) getFragmentManager().findFragmentByTag("MusicPlayer")).stopThread();
                ((MusicPlayerFragment) getFragmentManager().findFragmentByTag("MusicPlayer")).updateUI();
            }else{
                ((MusicListFragment) getFragmentManager().findFragmentByTag("MusicPlaylist")).musicChange();
                Music item = MusicApplication.getInstance().getServiceInterface().getMusicItem();

                Glide.with(MusicPlayerActivity.this)
                        .load(item.getCover())
                        .into(btn_playlist);

                btn_playlist.setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT >= 21) {
                    btn_playlist.setClipToOutline(true);
                }
            }
        }
    };

    private void updateUI() {
        if (MusicApplication.getInstance().getServiceInterface().isPlaying()) {
            btn_play_pause.setImageResource(R.drawable.ic_pause_button);
        } else {
            btn_play_pause.setImageResource(R.drawable.ic_play);
        }
    }

    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PREPARED);
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MusicApplication.MusicPlayerFragmentAlive ==true){
            ((MusicPlayerFragment) getFragmentManager().findFragmentByTag("MusicPlayer")).play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(MusicApplication.MusicPlayerFragmentAlive ==true){
            ((MusicPlayerFragment) getFragmentManager().findFragmentByTag("MusicPlayer")).pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

}
