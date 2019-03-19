package com.example.music.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
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

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private Context context;
    private List<Music> musicList;
    private ArrayList<Music> chooseList = new ArrayList<>();

    public MusicAdapter( Context context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
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

        Glide
                .with(context)
                .load(musicList.get(position).getCover())
                .centerCrop()
                .placeholder(R.drawable.default_album)
                .into(holder.coverImage);

        holder.playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("클릭",musicList.get(position).getTitle()+"");
                chooseList.add(musicList.get(position));
                MusicApplication.getInstance().getServiceInterface().setPlayList(chooseList,true); // 재생목록등록
                chooseList.clear();
            }
        });

        if(musicList.get(position).isCheck()){
            holder.musiclayout.setBackgroundResource(R.color.mistyrose);
        }else{
            holder.musiclayout.setBackgroundColor(Color.argb(0,0,0,0));
        }

        holder.musiclayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chooseList.contains(musicList.get(position))){
                    chooseList.add(musicList.get(position));
                    holder.musiclayout.setBackgroundResource(R.color.mistyrose);
                    musicList.get(position).setCheck(true);
                }else{
                    chooseList.remove(musicList.get(position));
                    holder.musiclayout.setBackgroundColor(Color.argb(0,0,0,0));
                    musicList.get(position).setCheck(false);
                }
                ((MainActivity)context).selectMusic(chooseList.size());
            }
        });
    }

    public ArrayList<Music> getItems(){
        return chooseList;
    }

    public void flush(){
        chooseList.clear();
        for(int i=0;i< musicList.size();i++){
            musicList.get(i).setCheck(false);
            notifyDataSetChanged();
        }
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

            playbtn = mView.findViewById(R.id.btn_music_play);
        }
    }
}
