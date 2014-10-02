package com.jinux.doubanfm;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by jinux on 14-10-2.
 */
public class Player {

    private static final String TAG = Player.class.getSimpleName();
    MediaPlayer mp=new MediaPlayer();
    Context mContext;
    public Player(Context c){
        this.mContext=c;

    }
    public void playSong(SongInfo songInfo,MediaPlayer.OnCompletionListener onComplete) {
        //if not
        //TODO
        if (songInfo == null){
            return;
        }

        mp.reset();
        //load song and play
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Log.d(TAG, "the song url = " + songInfo.getSongUrl().toString());
            mp.setDataSource(songInfo.getSongUrl().toString());
        } catch (IOException e) {
            e.printStackTrace();
            Utils.showToast(mContext,"MediaPlayer setDataSource URL error");
        }
        mp.prepareAsync();
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG,"the song will play");
                mp.start();
            }
        });
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                Log.e(TAG, "play error");
                return false;
            }
        });
        mp.setOnCompletionListener(onComplete);

    }
}
