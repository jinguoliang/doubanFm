package com.jinux.doubanfm;

import android.provider.BaseColumns;

/**
 * Created by jinux on 14-10-4.
 */
public class SongContract {
    private  SongContract() {}

    public static abstract class LikeSongEntry implements BaseColumns{
        public static final String TABLE_NAME = "like_song";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ALBUM = "album";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_PUBLIC_TIME = "public_time";
        public static final String COLUMN_NAME_SONG_URL = "song_url";
        public static final String COLUMN_NAME_PIC_URL = "pic_url";
        public static final String COLUMN_NAME_LENTH = "length";
        public static final String COLUMN_NAME_RATE = "rate";
        public static final String COLUMN_NAME_LIKE_DATE = "like_date";

        public static final String [] PROJECTION = {
                COLUMN_NAME_TITLE,
                COLUMN_NAME_ALBUM,
                COLUMN_NAME_ARTIST,
                COLUMN_NAME_PUBLIC_TIME,
                COLUMN_NAME_SONG_URL,
                COLUMN_NAME_PIC_URL,
                COLUMN_NAME_LENTH,
                COLUMN_NAME_RATE,
                COLUMN_NAME_LIKE_DATE
        };
    }
}
