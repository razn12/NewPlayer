package com.example.raazn.newplayer;

import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.logging.Filter;

/**
 * Created by raazn on 02-Jun-16.
 */
public class CustomAdapter extends BaseAdapter {
    String[] files;
    private Cursor c;
    private LayoutInflater inflater;


    public CustomAdapter(MainActivity mactivity, Cursor data) {
        c = data;
        inflater = LayoutInflater.from(mactivity);
        files = getAllMusicFiles();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return c.getCount();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub


        View v=inflater.inflate(R.layout.listrow, null);
        TextView tv1=(TextView)v.findViewById(R.id.songname);
        TextView tv2=(TextView)v.findViewById(R.id.artistname);

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(files[position]);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if(title!=null){
            tv1.setText(title);
        }else{
            int lastIndexofslash = files[position].lastIndexOf("/");
            String medianame = files[position].substring(lastIndexofslash+1, files[position].length());
            tv1.setText(medianame);}

        tv2.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));


        return v;

    }


    String[] getAllMusicFiles(){


        c.moveToPosition(0);

        String files[] = new String[c.getCount()];
        for(int i=0; i<c.getCount(); i++){
            c.moveToPosition(i);
            int index = c.getColumnIndex(MediaStore.Audio.Media.DATA);
            files[i] = c.getString(index);
        }

        return files;


    }


}

