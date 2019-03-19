package com.example.music.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.model.Music;
import com.example.music.network.RetrofitClientInstance;
import com.example.music.network.interfaces.Mp3List;
import com.example.music.etc.BroadcastActions;
import com.example.music.service.MusicApplication;
import com.example.music.view.adapter.MusicAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private MusicAdapter adapter;
    private RecyclerView recyclerView;
    ProgressDialog progressDialog;
    private ImageView iv_cover;
    private TextView tv_title;
    private ImageButton btn_playpause;
    private ImageButton btn_rewind;
    private ImageButton btn_forward;

    private LinearLayout lin_miniplayer;
    private LinearLayout lin_selectmusic;
    private TextView tv_music_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        lin_miniplayer = findViewById(R.id.lin_miniplayer);
        lin_selectmusic = findViewById(R.id.lin_selectmusic);

        findViewById(R.id.btn_main_play_music).setOnClickListener(this);
        findViewById(R.id.btn_main_add_list).setOnClickListener(this);

        tv_music_count = findViewById(R.id.tv_count);

        iv_cover =findViewById(R.id.iv_main_coverimg);
        tv_title =findViewById(R.id.tv_main_title);
        btn_playpause =findViewById(R.id.btn_main_play_pause);

        btn_playpause.setOnClickListener(this);
        findViewById(R.id.lin_miniplayer).setOnClickListener(this);
        btn_forward=findViewById(R.id.btn_main_forward);
        btn_forward.setColorFilter(getResources().getColor(R.color.white));
        btn_forward.setOnClickListener(this);
        btn_rewind=findViewById(R.id.btn_main_rewind);
        btn_rewind.setColorFilter(getResources().getColor(R.color.white));
        btn_rewind.setOnClickListener(this);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        /*Create handle for the RetrofitInstance interface*/
        Mp3List service = RetrofitClientInstance.getRetrofitInstance().create(Mp3List.class);
        Call<List<Music>> call = service.getAllMusic();
        call.enqueue(new Callback<List<Music>>() {
            @Override
            public void onResponse(Call<List<Music>> call, Response<List<Music>> response) {
                progressDialog.dismiss();
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<Music>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });

        registerBroadcast();
        updateUI();
    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(List<Music> musicList) {
        recyclerView = findViewById(R.id.recyclerview_playlist);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new MusicAdapter(this,musicList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_miniplayer:
                Intent intent = new Intent(getApplicationContext(),MusicPlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_main_rewind:
                // 이전곡으로 이동
                MusicApplication.getInstance().getServiceInterface().rewind();
                break;
            case R.id.btn_main_play_pause:
                // 재생 또는 일시정지
                MusicApplication.getInstance().getServiceInterface().togglePlay();
                break;
            case R.id.btn_main_forward:
                // 다음곡으로 이동
                MusicApplication.getInstance().getServiceInterface().forward();
                break;
            case R.id.btn_main_play_music:
                ArrayList<Music> playmusics = adapter.getItems();
                MusicApplication.getInstance().getServiceInterface().setPlayList(playmusics,true); // 재생목록등록

                selectMusic(0);
                adapter.flush();
                break;
            case R.id.btn_main_add_list:
                ArrayList<Music> addmusics = adapter.getItems();
                MusicApplication.getInstance().getServiceInterface().setPlayList(addmusics,false); // 재생목록등록

                selectMusic(0);
                adapter.flush();
                break;
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    private void updateUI() {
        if (MusicApplication.getInstance().getServiceInterface().isPlaying()) {
            btn_playpause.setImageResource(R.drawable.ic_pause_button);
            btn_playpause.setColorFilter(getResources().getColor(R.color.white));
        } else {
            btn_playpause.setImageResource(R.drawable.ic_play);
            btn_playpause.setColorFilter(getResources().getColor(R.color.white));
        }
        Music musicItem = MusicApplication.getInstance().getServiceInterface().getMusicItem();
        if (musicItem != null) {
            Glide
                    .with(this)
                    .load(musicItem.getCover())
                    .centerCrop()
                    .placeholder(R.drawable.default_album)
                    .into(iv_cover);
            tv_title.setText(musicItem.getTitle());
        } else {
            iv_cover.setImageResource(R.drawable.default_album);
            tv_title.setText("재생중인 음악이 없습니다.");
        }
    }

    public void selectMusic(int count){
        Log.d("선택된 아이템 : ",count+"개");
        if(count==0){
            lin_selectmusic.setVisibility(View.GONE);
            lin_miniplayer.setVisibility(View.VISIBLE);
        }else{
            lin_miniplayer.setVisibility(View.GONE);
            lin_selectmusic.setVisibility(View.VISIBLE);
            tv_music_count.setText(count+"");
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }
}