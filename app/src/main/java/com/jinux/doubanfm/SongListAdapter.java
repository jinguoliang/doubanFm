package com.jinux.doubanfm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinux on 14-10-2.
 */
public class SongListAdapter extends BaseAdapter {

    private static final String TAG = SongListAdapter.class.getSimpleName();
    private final Context mContext;
    private List<SongInfo> data= new ArrayList<SongInfo>();

    public SongListAdapter(Context c) {
        this.mContext=c;
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
            view = LayoutInflater.from(mContext).inflate(R.layout.song_list_item, null);
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