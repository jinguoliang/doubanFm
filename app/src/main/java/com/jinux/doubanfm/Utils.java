package com.jinux.doubanfm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinux on 14-10-2.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static List<SongInfo> getListFromJsonStr(String str) {
        JSONTokener jsonParser = new JSONTokener(str);
        ArrayList<SongInfo> list= null;

        try {
            SongInfo info = null;
            JSONObject jo = (JSONObject) jsonParser.nextValue();
            JSONArray songList = jo.getJSONArray("song");
             list = new ArrayList<SongInfo>(songList.length());
            for (int i = 0; i < songList.length(); i++) {
                JSONObject o = songList.getJSONObject(i);
                info = new SongInfo();
                info.setTitle(o.getString("title"));
                info.setAlbum(o.getString("albumtitle"));
                info.setArtist(o.getString("artist"));
                info.setLength(o.getInt("length"));
                try {
                    info.setPublicTime(o.getString("public_time"));
                } catch (JSONException e) {

                }
                info.setPicUrl(new URL(o.getString("picture")));
                info.setSongUrl(new URL(o.getString("url")));
                Log.e(TAG, info.toString());
                list.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            Log.e(TAG, "URL error");
            e.printStackTrace();
        }
        return list;
    }

    static class ToastHandler extends Handler {
        private final Context mContext;

        public ToastHandler(Context c){
            this.mContext = c;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Toast.makeText(mContext,(String)msg.obj,Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };
    static void showToast(String s, ToastHandler th){
        Message m = Message.obtain();
        m.obj=s;
        th.sendMessage(m);
    }


    static class LoadPictureTask extends AsyncTask<URL,Object,Bitmap> {

        private final Context mContext;
        private final ImageView mView;

        public LoadPictureTask(Context c, ImageView iv){
            this.mContext=c;
            this.mView=iv;

        }
        @Override
        protected Bitmap doInBackground(URL... urls) {
            Bitmap bitmap = null;
            Log.e(TAG, "loading picture");
            try {
                URLConnection conn=urls[0].openConnection();
                conn.setDoInput(true);
                conn.connect();

                bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"load picture error");
            }
            Log.e(TAG, "load picture finish!!" + bitmap);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap == null) return;

            mView.setImageBitmap(bitmap);
        }
    }

}
