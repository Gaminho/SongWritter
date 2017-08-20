package com.songwritter.gaminho.songwritter.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.songwritter.gaminho.songwritter.R;
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
        Drawable imgDrawable;

        ((TextView) row.findViewById(R.id.beat_title)).setText(beat.getTitle());
        String type = beat.getType().equals(Instrumental.Type.FACE_A) ? "Face A" : "Face B";
        ((TextView) row.findViewById(R.id.beat_details)).setText(type);

        if(position % 2 == 0)
            imgDrawable = mContext.getDrawable(R.drawable.ic_cloud_off_white_18dp);
        else
            imgDrawable = mContext.getDrawable(R.drawable.ic_cloud_queue_white_18dp);

        ((ImageView) row.findViewById(R.id.beat_shared)).setImageDrawable(imgDrawable);

//        row.findViewById(R.id.play_pause_stop).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(mContext, "Play music!", Toast.LENGTH_SHORT).show();
//                if (mContext instanceof Activity)
//                    ((Activity) mContext).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ImageView) row.findViewById(R.id.beat_shared)).setImageDrawable(mContext.getDrawable(R.drawable.ic_cloud_off_white_18dp));
//                        }
//                    });
//            }
//        });

        return row;
    }

}
