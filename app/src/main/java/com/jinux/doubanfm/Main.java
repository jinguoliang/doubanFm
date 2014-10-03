package com.jinux.doubanfm;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.simonvt.menudrawer.MenuDrawer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Main extends Activity {

    private static final String TAG = Main.class.getSimpleName();
    private MenuDrawer mDrawer;

    View main, musiclist;
    private SongListAdapter songListAdapter;
    private Player mPlayer;
    private int mCurrentSongPosition = 0;
    private int mCurrentChanel = 1;

    private LoadChanelTask mLoadChanelTask ;

    public Utils.ToastHandler toastHandler = new Utils.ToastHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mDrawer = MenuDrawer.attach(this);
        mDrawer.setContentView(R.layout.main);
        mDrawer.setMenuView(R.layout.music_list);
        main=mDrawer.getContentContainer();
        musiclist=mDrawer.getMenuView();
        initView();

        mPlayer = new Player(this);
        (mLoadChanelTask=new LoadChanelTask()).execute(mCurrentChanel);
    }

    private Button chanelPre,chanelNext,playAndPause,pre,next;
    private TextView chanel,title;
    private ImageView pictureView;
    private ListView songList;
    private void initView() {
        chanelPre = (Button) main.findViewById(R.id.chanel_pre);
        chanelNext = (Button) main.findViewById(R.id.chanel_next);
        pre = (Button) main.findViewById(R.id.pre);
        next = (Button) main.findViewById(R.id.next);
        playAndPause = (Button) main.findViewById(R.id.play_pause);
        chanel = (TextView) main.findViewById(R.id.chanel);
        title = (TextView) main.findViewById(R.id.song_title);
        pictureView = (ImageView)main.findViewById(R.id.picture);

        //drawer list
        songList = (ListView) musiclist.findViewById(R.id.song_list);
        songListAdapter = new SongListAdapter(this);
        songList.setAdapter(songListAdapter);
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCurrentSongPosition = i;
                playSong();
            }


        });
    }

    private void playSong() {
        int position = mCurrentSongPosition;

        List<SongInfo> list = songListAdapter.getData();
        if (position >= list.size()){
            position = position % list.size();
        }
        if (position < 0) {
            position = list.size() - 1;
            if(position < 0) return;
        }
        mCurrentSongPosition = position;

        SongInfo songInfo = list.get(position);
        this.title.setText(songInfo.getTitle());
        //load picture
        new Utils.LoadPictureTask(Main.this,pictureView).execute(songInfo.getPicUrl());
        mPlayer.playSong(songInfo, new MediaPlayer.OnCompletionListener(){

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.e(TAG,"onCompletion");
                ++mCurrentSongPosition;
                playSong();
            }
        });
    }

    public void onButtonClick(View v){
        Log.e(TAG,"The button is "+((Button)v).getText());
        switch (v.getId()){
            case R.id.play_pause:
                mPlayer.togglePlay();
                break;
            case R.id.next:
                ++mCurrentSongPosition;
                playSong();
                break;
            case R.id.pre:
                --mCurrentSongPosition;
                playSong();
                break;
            case R.id.chanel_pre:
                mLoadChanelTask.cancel(true);
                (mLoadChanelTask= new LoadChanelTask()).execute(--mCurrentChanel);
                break;
            case R.id.chanel_next:
                mLoadChanelTask.cancel(true);
                (mLoadChanelTask= new LoadChanelTask()).execute(++mCurrentChanel);
                break;
        }
    }


    private String getChanel(int n) throws IOException {
        URL url=new URL("http://douban.fm/j/mine/playlist?type=1&channel=1");
        HttpURLConnection con= (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(5 * 1000);
        con.setRequestMethod("GET");
        con.setRequestProperty("Charset","UTF-8");

        con.setDoInput(true);
        con.connect();

        InputStreamReader bis=new InputStreamReader(con.getInputStream());
        String data=new BufferedReader(bis).readLine();
        bis.close();
        con.disconnect();
        return data;
    }

    class LoadChanelTask extends  AsyncTask<Integer, Integer ,String >{

        public LoadChanelTask(){
            Log.e(TAG,"myTask");
        }
        @Override
        protected String doInBackground(Integer... ints) {
            Log.e(TAG,"start:");
            String jsonString= null;
            try {
                jsonString = getChanel(ints[0]);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"err");
                Utils.showToast("The network is not link",toastHandler);
            }
            Log.e(TAG,"end");
            return jsonString;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            super.onPostExecute(jsonStr);
            if (jsonStr == null) return ;
            chanel.setText(getResources().getString(R.string.chanel_fmt,mCurrentChanel));
            Log.e(TAG, "the json is :\n" + jsonStr);
            List<SongInfo> list = Utils.getListFromJsonStr(jsonStr);
            songListAdapter.setData(list);
            mCurrentChanel = 0;
            playSong();
        }
    }


    /*class LoadSongTask extends AsyncTask<URL,Integer,FileDescriptor>{

        @Override
        protected FileDescriptor doInBackground(URL... urls) {
            Bitmap bitmap = null;
            Log.e(TAG, "loading picture");
            try {
                URLConnection conn=urls[0].openConnection();
                conn.setDoInput(true);
                conn.connect();


            } catch (IOException e) {
                e.printStackTrace();
                Utils.showToast(Main.this,"Failed to download picture");
            }
            Log.e(TAG, "load picture finish!!" + bitmap);

            return bitmap;
        }

        @Override
        protected void onPostExecute(FileDescriptor bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap == null) return;

            pictureView.setImageBitmap(bitmap);
        }
    }*/




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mPlayer.release();
    }
}
