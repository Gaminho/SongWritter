package com.songwritter.gaminho.songwritter.activities.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.songwritter.gaminho.songwritter.C;
import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.activities.SongActivity;
import com.songwritter.gaminho.songwritter.beans.SongLyrics;

import java.util.List;


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
    public Object getItem(int i) {
        return mLyrics.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.adapter_songslyrics, viewGroup, false);

        final SongLyrics songLyrics = mLyrics.get(i);

        ((TextView) row.findViewById(R.id.song_title)).setText(songLyrics.getTitle());
        ((TextView) row.findViewById(R.id.song_preview)).setText(songLyrics.getContent());

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(SongActivity.SONG_LYRICS, songLyrics);
                Intent intent = new Intent(mContext, SongActivity.class);
                intent.setAction(C.ACTION_VIEW);
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
