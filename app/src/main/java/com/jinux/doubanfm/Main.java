package com.jinux.doubanfm;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
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
        songListAdapter = new SongListAdapter();
        songList.setAdapter(songListAdapter);
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Main.this.mCurrentSongPosition = i;
                playSong(songListAdapter.getData().get(i));
            }


        });
    }

    private void playSong(SongInfo songInfo) {
        this.title.setText(songInfo.getTitle());
        //load picture
        new Utils.LoadPictureTask(Main.this,pictureView).execute(songInfo.getPicUrl());
        mPlayer.playSong(songInfo, new MediaPlayer.OnCompletionListener(){

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playSong(songListAdapter.getData().get(++mCurrentSongPosition));
            }
        });
    }

    public void onButtonClick(View v){
        Log.e(TAG,"The button is "+((Button)v).getText());
        switch (v.getId()){
            case R.id.play_pause:
                break;
            case R.id.next:
                playSong(songListAdapter.getData().get(++mCurrentSongPosition));
                break;
            case R.id.pre:
                playSong(songListAdapter.getData().get(--mCurrentSongPosition));
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
                Utils.showToast(Main.this,"The network is not link");
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
            playSong(list.get(0));
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


    private class SongListAdapter extends BaseAdapter {

        private List<SongInfo> data= new ArrayList<SongInfo>();

        public SongListAdapter() {

        }

        public List<SongInfo> getData() {
            return data;
        }

        public void setData(List<SongInfo> songList){
            data.clear();
            data.addAll(songList);

            notifyDataSetChanged();
            notifyDataSetInvalidated();

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.e(TAG, "getView");

            ViewHolder holder;
            if (view == null){
                view = getLayoutInflater().inflate(R.layout.song_list_item,null);
                holder=new ViewHolder();
                holder.title = (TextView) view.findViewById(R.id.title_item);
                holder.artist = (TextView) view.findViewById(R.id.artist_item);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }

            holder.title.setText(data.get(i).getTitle());
            holder.artist.setText(data.get(i).getArtist());
            return view;
        }


        private class ViewHolder {
            TextView title;
            TextView artist;
        }
    }
}
