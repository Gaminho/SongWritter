package com.songwritter.gaminho.songwritter.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.songwritter.gaminho.songwritter.Utils;
import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.activities.songs.ActivitySong;
import com.songwritter.gaminho.songwritter.beans.SongLyrics;

import java.util.List;
import java.util.Locale;


public class SongLyricsAdapter extends BaseAdapter {


    private Context mContext;
    private List<SongLyrics> mLyrics;

    public SongLyricsAdapter(Context context, List<SongLyrics> mSongs) {
        this.mContext = context;
        this.mLyrics = mSongs;
    }


    @Override
    public int getCount() {
        return mLyrics.size();
    }

    @Override
    public Object getItem(int position) {
        return mLyrics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.adapter_songs, viewGroup, false);

        final SongLyrics songLyrics = mLyrics.get(position);
        Drawable imgDrawable;

        ((TextView) row.findViewById(R.id.song_title)).setText(songLyrics.getTitle());
        ((TextView) row.findViewById(R.id.song_preview)).setText(songLyrics.getContent());
        ((TextView)row.findViewById(R.id.creation_song).findViewById(R.id.item_date)).setText(Utils.formatTS(songLyrics.getCreation(), Utils.DateFormat.DAY));
        ((TextView)row.findViewById(R.id.update_song)).setText(String.format(Locale.FRANCE, mContext.getString(R.string.format_last_update), Utils.formatTS(songLyrics.getLastUpdate(), Utils.DateFormat.FULL)));

        if(songLyrics.getAuthor().equals("szdasd"))
            imgDrawable = null;
        else
            imgDrawable = mContext.getDrawable(R.drawable.img_lyrics);

        ((ImageView)row.findViewById(R.id.img_song)).setImageDrawable(imgDrawable);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ActivitySong.SONG_LYRICS, songLyrics);
                //Intent intent = new Intent(mContext, ActivitySong.class);

                Intent intent = new Intent(mContext, ActivitySong.class);
                //intent.setAction(Utils.ACTION_VIEW);
                intent.putExtras(bundle);

                mContext.startActivity(intent);
            }
        });

        return row;
    }

    public void setListItem(List<SongLyrics> neoList) {
        mLyrics = neoList;
        notifyDataSetChanged();
    }

}
