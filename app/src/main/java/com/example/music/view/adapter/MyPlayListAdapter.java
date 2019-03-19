package com.example.music.view.adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.activity.MainActivity;
import com.example.music.model.Music;
import com.example.music.service.MusicApplication;

import java.util.ArrayList;
import java.util.List;

public class MyPlayListAdapter extends RecyclerView.Adapter<MyPlayListAdapter.MusicViewHolder> {
    private Context context;
    private List<Music> musicList;
    private ArrayList<Music> chooseList = new ArrayList<>();
    private int mPosition=-1;

    public MyPlayListAdapter( Context context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
        mPosition = MusicApplication.getInstance().getServiceInterface().getmPosition();
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MusicViewHolder holder, final int position) {
        holder.musicTitle.setText(musicList.get(position).getTitle());
        holder.musicArtist.setText(musicList.get(position).getArtist());

        if(mPosition==position){
            holder.musicTitle.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.musicArtist.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.playing.setVisibility(View.VISIBLE);

        }else{
            holder.musicTitle.setTextColor(Color.parseColor("#000000"));
            holder.musicArtist.setTextColor(Color.parseColor("#808080"));
            holder.playing.setVisibility(View.GONE);
        }

        Glide
                .with(context)
                .load(musicList.get(position).getCover())
                .centerCrop()
                .placeholder(R.drawable.default_album)
                .into(holder.coverImage);


        holder.musiclayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicApplication.getInstance().getServiceInterface().play(position);
            }
        });
    }

    public ArrayList<Music> getItems(){
        return chooseList;
    }

    public void flush(){
        mPosition = MusicApplication.getInstance().getServiceInterface().getmPosition();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    class MusicViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        TextView musicTitle;
        TextView musicArtist;
        private ImageView coverImage;
        ImageView playing;
        ImageButton playbtn;
        LinearLayout musiclayout;

        MusicViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            musiclayout = mView.findViewById(R.id.view_musicitem);
            musicTitle = mView.findViewById(R.id.tv_list_title);
            musicArtist = mView.findViewById(R.id.tv_list_artist);
            coverImage = mView.findViewById(R.id.iv_list_cover);
            GradientDrawable drawable=
                    (GradientDrawable) context.getDrawable(R.drawable.custom_rounded);
            coverImage.setBackground(drawable);
            coverImage.setClipToOutline(true);

            playing = mView.findViewById(R.id.iv_playing);
            playbtn = mView.findViewById(R.id.btn_music_play);
            playbtn.setVisibility(View.GONE);
        }
    }
}

