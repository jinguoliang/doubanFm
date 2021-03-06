package com.jinux.doubanfm;

import java.net.URL;
import java.util.Date;

/**
 * Created by jinux on 14-10-2.
 */
public class SongInfo {
    private String title;
    private String album;
    private String artist;
    private String publicTime;
    private URL songUrl;
    private URL picUrl;
    private int length;
    private  int rate;
    private Date linkDate;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public URL getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(URL songUrl) {
        this.songUrl = songUrl;
    }

    public URL getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(URL picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublicTime(String publicTime) {
        this.publicTime  = publicTime;
    }

    public String getPublicTime() {
        return publicTime;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public Date getLinkDate() {
        return linkDate;
    }

    public void setLinkDate(Date linkDate) {
        this.linkDate = linkDate;
    }

    @Override
    public String toString() {
        return "SongInfo{" +
                "title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", publicTime='" + publicTime + '\'' +
                ", songUrl=" + songUrl +
                ", picUrl=" + picUrl +
                ", length=" + length +
                '}';
    }
}
