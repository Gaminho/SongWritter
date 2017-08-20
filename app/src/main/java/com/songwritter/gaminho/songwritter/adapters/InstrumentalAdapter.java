package com.songwritter.gaminho.songwritter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.Utils;
import com.songwritter.gaminho.songwritter.beans.Instrumental;

import java.util.List;


public class InstrumentalAdapter extends BaseAdapter {


    private Context mContext;
    private List<Instrumental> mBeats;

    public InstrumentalAdapter(Context context, List<Instrumental> mBeats) {
        this.mContext = context;
        this.mBeats = mBeats;
    }


    @Override
    public int getCount() {
        return mBeats.size();
    }

    @Override
    public Object getItem(int position) {
        return mBeats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(R.layout.adapter_beats, viewGroup, false);

        final Instrumental beat = mBeats.get(position);
        int imgId;

        ((TextView) row.findViewById(R.id.beat_title)).setText(beat.getTitle());
        ((TextView) row.findViewById(R.id.beat_author)).setText(beat.getAuthor());
        String type = beat.getType().equals(Instrumental.Type.FACE_A) ? "Face A" : "Face B";
        ((TextView) row.findViewById(R.id.beat_details)).setText(type);

        if(position % 2 == 0)
            imgId = R.drawable.ic_cloud_off_white_18dp;
        else
            imgId = R.drawable.ic_cloud_queue_white_18dp;

        Utils.setImageView(imgId, (ImageView) row.findViewById(R.id.beat_shared));

        return row;
    }
}
