package com.example.music.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.music.R;
import com.example.music.model.Music;
import com.example.music.service.MusicApplication;
import com.example.music.view.adapter.MusicAdapter;
import com.example.music.view.adapter.MyPlayListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    MyPlayListAdapter adapter;
    ArrayList<Music> playlist;

    public MusicListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_playlist,container, false);
        playlist = MusicApplication.getInstance().getServiceInterface().getPlaylist();
        generateDataList(playlist);

        return view;
    }

    private void generateDataList(List<Music> musicList) {
        recyclerView = view.findViewById(R.id.recyclerview_myplaylist);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter = new MyPlayListAdapter(getActivity(),musicList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void musicChange(){
        adapter.flush();
    }

}