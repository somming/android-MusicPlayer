package com.example.music.network.interfaces;

import com.example.music.model.Music;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Mp3List {
    @GET("/getmp3.php")
    Call<List<Music>> getAllMusic();
}