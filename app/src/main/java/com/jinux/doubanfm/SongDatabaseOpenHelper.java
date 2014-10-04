package com.jinux.doubanfm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by jinux on 14-10-4.
 */
public class SongDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SongDatabase.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String LONG_TYPE = " INT8";
    private static final String COMMA_SEP = ",";
    private static final java.lang.String SQL_CREATE_ENTRIES = "CREATE TABLE " + SongContract.LikeSongEntry.TABLE_NAME + " (" +
            SongContract.LikeSongEntry._ID + " INTEGER PRIMARY KEY," +
            SongContract.LikeSongEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
            SongContract.LikeSongEntry.COLUMN_NAME_ALBUM + TEXT_TYPE + COMMA_SEP +
            SongContract.LikeSongEntry.COLUMN_NAME_ARTIST + TEXT_TYPE + COMMA_SEP +
            SongContract.LikeSongEntry.COLUMN_NAME_PUBLIC_TIME + TEXT_TYPE + COMMA_SEP +
            SongContract.LikeSongEntry.COLUMN_NAME_PIC_URL + TEXT_TYPE + COMMA_SEP +
            SongContract.LikeSongEntry.COLUMN_NAME_SONG_URL + TEXT_TYPE + COMMA_SEP +
            SongContract.LikeSongEntry.COLUMN_NAME_RATE + INT_TYPE + COMMA_SEP +
            SongContract.LikeSongEntry.COLUMN_NAME_LENTH + INT_TYPE + COMMA_SEP +
            SongContract.LikeSongEntry.COLUMN_NAME_LIKE_DATE + LONG_TYPE +
            " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SongContract.LikeSongEntry.TABLE_NAME;
    private static final String TAG = SongDatabaseOpenHelper.class.getSimpleName();

    public SongDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        Log.e(TAG, "onUpgrade");
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    private long insert(String table, ContentValues values){
        SQLiteDatabase database = this.getWritableDatabase();
        long newRowId = database.insert(table, null, values);
        database.close();

        return newRowId;
    }

    private Cursor query(String table, String selection, String args[], String sortOrder){
        SQLiteDatabase database = this.getReadableDatabase();

        return database.query(table, SongContract.LikeSongEntry.PROJECTION,selection,args,null,null, sortOrder);
    }

    public long insertLikeSong(SongInfo song){
        ContentValues values = new ContentValues();
        values.put(SongContract.LikeSongEntry.COLUMN_NAME_TITLE, song.getTitle());
        values.put(SongContract.LikeSongEntry.COLUMN_NAME_ALBUM, song.getAlbum());
        values.put(SongContract.LikeSongEntry.COLUMN_NAME_ARTIST, song.getArtist());
        values.put(SongContract.LikeSongEntry.COLUMN_NAME_PUBLIC_TIME, song.getPublicTime());
        values.put(SongContract.LikeSongEntry.COLUMN_NAME_SONG_URL, song.getSongUrl().toString());
        values.put(SongContract.LikeSongEntry.COLUMN_NAME_PIC_URL, song.getPicUrl().toString());
        values.put(SongContract.LikeSongEntry.COLUMN_NAME_LENTH, song.getLength());
        values.put(SongContract.LikeSongEntry.COLUMN_NAME_RATE, song.getRate());
        values.put(SongContract.LikeSongEntry.COLUMN_NAME_LIKE_DATE, new Date().getTime());

        return insert(SongContract.LikeSongEntry.TABLE_NAME, values);
    }

    public Cursor getLikeSongs(){
        String sortOrder = SongContract.LikeSongEntry.COLUMN_NAME_LIKE_DATE + " DESC";
        return query(SongContract.LikeSongEntry.TABLE_NAME, null, null, sortOrder);
    }

    public SongInfo getLikeSong(String title, String artist){
        String selection = SongContract.LikeSongEntry.COLUMN_NAME_TITLE + " as ?" +
                SongContract.LikeSongEntry.COLUMN_NAME_ARTIST + " as ?";
        String []args = new String []{title, artist};
        Cursor c = query(SongContract.LikeSongEntry.TABLE_NAME, selection,args, null);
        c.moveToFirst();
        SongInfo song = new SongInfo();
        song.setTitle(title);
        song.setArtist(artist);
        song.setAlbum(c.getString(c.getColumnIndex(SongContract.LikeSongEntry.COLUMN_NAME_ALBUM)));
        song.setPublicTime(c.getString(c.getColumnIndex(SongContract.LikeSongEntry.COLUMN_NAME_PUBLIC_TIME)));
        try {
            song.setSongUrl(new URL(c.getString(c.getColumnIndex(SongContract.LikeSongEntry.COLUMN_NAME_SONG_URL))));
            song.setPicUrl(new URL(c.getString(c.getColumnIndex(SongContract.LikeSongEntry.COLUMN_NAME_PIC_URL))));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        song.setRate(c.getInt(c.getColumnIndex(SongContract.LikeSongEntry.COLUMN_NAME_RATE)));
        song.setLength(c.getInt(c.getColumnIndex(SongContract.LikeSongEntry.COLUMN_NAME_LENTH)));
        song.setLinkDate(new Date(c.getString(c.getColumnIndex(SongContract.LikeSongEntry.COLUMN_NAME_LIKE_DATE))));
        return song;
    }


}
