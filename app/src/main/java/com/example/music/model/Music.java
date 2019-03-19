package com.example.music.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Music implements Parcelable {
    @SerializedName("sno")
    private Integer sno;
    @SerializedName("title")
    private String title;
    @SerializedName("artist")
    private String artist;
    @SerializedName("cover")
    private String cover;
    @SerializedName("album")
    private String album;
    @SerializedName("length")
    private Integer length;
    @SerializedName("genre")
    private String genre;
    @SerializedName("link")
    private String link;
    boolean check = false;
    boolean play = false;

    public Music (Integer sno, String title, String artist, String cover, String album, Integer length, String genre,String link) {
        this.sno = sno;
        this.title = title;
        this.artist = artist;
        this.cover = cover;
        this.album = album;
        this.length = length;
        this.genre = genre;
        this.link = link;
    }

    public Music(Parcel in) {
        readFromParcel(in);
    }

    public Integer getSno() {
        return sno;
    }

    public void setSno(Integer sno) {
        this.sno = sno;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isPlay() {
        return play;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(sno);
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(cover);
        parcel.writeString(album);
        parcel.writeInt(length);
        parcel.writeString(genre);
        parcel.writeString(link);
    }

    private void readFromParcel(Parcel in){
        sno = in.readInt();
        title = in.readString();
        artist = in.readString();
        cover = in.readString();
        album = in.readString();
        artist = in.readString();
        length = in.readInt();
        genre = in.readString();
        link = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
}
