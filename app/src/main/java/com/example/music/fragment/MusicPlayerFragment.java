package com.example.music.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.model.Music;
import com.example.music.service.MusicApplication;

public class MusicPlayerFragment extends Fragment {
    UIThread U;
    UIHandler u;
    String state;


    View view;
    ImageView iv_cover;
    TextView tv_title;
    TextView tv_artist;
    TextView tv_curtime, tv_totaltime;
    SeekBar musicseek;

    int curseek,duration;

    Boolean isPlaying;

    public MusicPlayerFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MusicApplication.MusicPlayerFragmentAlive = true;
        view = inflater.inflate(R.layout.fragment_player,container, false);

        u = new UIHandler();

        iv_cover = view.findViewById(R.id.iv_play_coverimg);
        tv_title = view.findViewById(R.id.tv_play_title);
        tv_artist = view.findViewById(R.id.tv_play_artist);
        musicseek = view.findViewById(R.id.seekbar_musicprogress);
        musicseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int curseek = musicseek.getProgress();
                tv_curtime.setText(getTime(curseek));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isPlaying = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int curseek = musicseek.getProgress();
                MusicApplication.getInstance().getServiceInterface().setMusicseek(curseek);
                isPlaying = true;
            }
        });
        tv_curtime = view.findViewById(R.id.tv_play_cur);
        tv_totaltime = view.findViewById(R.id.tv_play_duration);

        updateUI();

        return view;
    }


    public void updateUI() {
        Music item = MusicApplication.getInstance().getServiceInterface().getMusicItem(); // 재생중인 음악 받기
        if(item!=null){
            curseek = MusicApplication.getInstance().getServiceInterface().getCurseek();
            duration = MusicApplication.getInstance().getServiceInterface().getDuration();
            isPlaying = MusicApplication.getInstance().getServiceInterface().isPlaying();

            Glide
                    .with(getActivity())
                    .asBitmap()
                    .load(item.getCover())
                    .into(iv_cover);

            tv_title.setText(item.getTitle());
            tv_artist.setText(item.getArtist());
            musicseek.setMax(duration);
            musicseek.setProgress(curseek);

            tv_curtime.setText(getTime(curseek));
            tv_totaltime.setText(getTime(duration));

            startThread();
        }
    }

    private class UIThread extends Thread{
        Message msg;
        boolean loop = true;

        public void run() {
            try {
                while (loop) {
                    Thread.sleep(1);
                    if(Thread.interrupted()){ //인터럽트가 들어오면 루프를 탈출합니다.
                        loop = false;
                        break;
                    }
                    msg = u.obtainMessage();
                    msg.arg1 = 1;
                    u.sendMessage(msg);
                }
            } catch (InterruptedException e) {//sleep 상태에서 인터럽트가 들어오면 exception 발생
                // TODO Auto-generated catch block
                loop = false;
            }
        }
    }

    private class UIHandler extends Handler {
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    if(state.equals("DeActive")) //Fragment가 숨겨진 상태일 때
                        break;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            curseek=MusicApplication.getInstance().getServiceInterface().getCurseek();
                            musicseek.setProgress(curseek);
                            tv_curtime.setText(getTime(curseek));
                        }
                    });
            }
        }
    }


    /*class MusicThread implements Runnable {
        boolean stopped = false;
        int curseek;

        public void stop(){
            this.stopped=true;
        }

        @Override
        public void run() { // 쓰레드가 시작되면 콜백되는 메서드
            // 씨크바 막대기 조금씩 움직이기 (노래 끝날 때까지 반복)
            Log.d("Music 쓰레드","시작");

            while(!stopped){
                while(isPlaying&&curseek<duration) {
                    curseek=MusicApplication.getInstance().getServiceInterface().getCurseek();
                    musicseek.setProgress(curseek);
                    if(getActivity()!=null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_curtime.setText(getTime(curseek));
                            }
                        });
                    }
                    break;
                }
            }
            Log.d("Music 쓰레드","종료");
        }
    }*/

    public void startThread(){
        state = "Active";
        U = new UIThread();
        U.start();
    }

    public void stopThread(){
        state = "DeActive";
        U.interrupt();
    }

    public void togglePlay(){
        Log.d("Fragment 쓰레드 toggle","");
        isPlaying = !isPlaying;
    }

    public void pause(){
        Log.d("Fragment 쓰레드 toggle","");
        isPlaying = false;
    }
    public void play(){
        Log.d("Fragment 쓰레드 toggle","");
        isPlaying = true;
    }


    public String getTime(int progress){
        int m=progress/60000;
        int s=(progress%60000)/1000;
        if(s<10){
            return m+":0"+s;
        }
        return m+":"+s;
    }

    @Override
    public void onPause() {
        super.onPause();
        MusicApplication.MusicPlayerFragmentAlive =false;
        isPlaying=false;
        stopThread();
    }

    @Override
    public void onResume() {
        super.onResume();
        state = "Active";
    }
}
