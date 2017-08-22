package com.songwritter.gaminho.songwritter.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.Utils;
import com.songwritter.gaminho.songwritter.beans.MemoRecord;

import java.io.IOException;
import java.util.List;

import static com.songwritter.gaminho.songwritter.Utils.LOG;

public class RecordAdapter extends ArrayAdapter<MemoRecord> {

    Activity context;
    List<MemoRecord> records;
    private SparseBooleanArray mSelectedItemsIds;
    private boolean selectionMode = false;
    private boolean playingMode;
    private int playingPosition = -1;
    private int mediaDuration = -1;
    private MediaPlayer mPlayer;
    private final Handler mHandler = new Handler();
    private SeekBar mSeekBar;
    private TextView mTVCurrentPosition;


    public RecordAdapter(Activity context, int resId, List<MemoRecord> beats) {
        super(context, resId, beats);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.records = beats;
    }

    private class ViewHolder {

        TextView mTVTitle;
        TextView mTVDuration;
        TextView mTVPosition;
        TextView mTVCreation;
        RelativeLayout mRLRow;
        SeekBar mSeekBar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_records, null);
            holder = new ViewHolder();
            holder.mTVTitle = (TextView) convertView.findViewById(R.id.record_title);
            holder.mTVCreation = (TextView) convertView.findViewById(R.id.record_date);
            holder.mTVPosition = (TextView) convertView.findViewById(R.id.record_position);
            holder.mTVDuration = (TextView) convertView.findViewById(R.id.record_duration);
            holder.mSeekBar = (SeekBar) convertView.findViewById(R.id.listen_progress);
            holder.mSeekBar.setPadding(0,0,0,0);
            holder.mRLRow = (RelativeLayout) convertView.findViewById(R.id.row);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MemoRecord record = getItem(position);
        holder.mTVTitle.setText(record.getTitle());
        holder.mTVCreation.setText(Utils.formatTS(record.getCreation(), Utils.DateFormat.FULL));
        holder.mTVDuration.setText(Utils.milliSecondsToTimer(record.getDuration(), true));

        if(!selectionMode) {
            //LOG("Not Selection Mode");
        }
        else{
            //LOG("Selection Mode");
        }

        if(!playingMode){
            holder.mSeekBar.setVisibility(View.GONE);
            holder.mTVPosition.setVisibility(View.GONE);
        }
        else{
            if(position == playingPosition){
                mSeekBar = holder.mSeekBar;
                mSeekBar.setEnabled(false);
                mTVCurrentPosition = holder.mTVPosition;
            }
            else{
                holder.mSeekBar.setProgress(0);
                holder.mSeekBar.setVisibility(View.GONE);
                holder.mTVPosition.setVisibility(View.GONE);
            }
        }

        if(mSelectedItemsIds.get(position)) {
            holder.mRLRow.setBackgroundColor(context.getColor(R.color.colorPrimary));
            holder.mTVTitle.setTextColor(context.getColor(R.color.white0));
            holder.mTVCreation.setTextColor(context.getColor(R.color.white0));
            holder.mTVDuration.setTextColor(context.getColor(R.color.white0));
        }
        else{
            holder.mRLRow.setBackgroundColor(Color.TRANSPARENT);
            holder.mTVTitle.setTextColor(context.getColor(R.color.black0));
            holder.mTVCreation.setTextColor(context.getColor(R.color.grey));
            holder.mTVDuration.setTextColor(context.getColor(R.color.black0));
        }

        return convertView;
    }

    @Override
    public void add(MemoRecord record) {
        records.add(record);
        notifyDataSetChanged();
    }

    @Override
    public void remove(MemoRecord record) {
        records.remove(record);
        notifyDataSetChanged();
    }

    public List<MemoRecord> getRecords() {
        return records;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public void setSelectionMod(boolean active){
        this.selectionMode = active;
        notifyDataSetChanged();
    }

    public boolean isSelecting(){
        return selectionMode;
    }

    public boolean isPlaying(){
        return playingMode;
    }

    public void playSongAtPosition(int position){
        mSeekBar = null;
        mTVCurrentPosition = null;

        if(isPlaying())
            mPlayer.stop();

        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playingMode = false;
                notifyDataSetChanged();
                mHandler.removeCallbacks(mUpdateTimeTask);
                mSeekBar = null;
                mTVCurrentPosition = null;
            }
        });
        try {
            mPlayer.setDataSource(records.get(position).getPath());
            mPlayer.prepare();
            mediaDuration = mPlayer.getDuration();
            mPlayer.start();
            playingMode = true;
            playingPosition = position;
            mHandler.post(mUpdateTimeTask);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlaying(){
        playingPosition = -1;
        mPlayer.stop();
        playingMode = false;
        mediaDuration = -1;
        mPlayer = null;
        mSeekBar = null;
        mHandler.removeCallbacks(mUpdateTimeTask);
        notifyDataSetChanged();
    }

    public int getCurrentListeningPosition(){
        return playingPosition;
    }

    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if(mSeekBar != null){
                mSeekBar.setVisibility(View.VISIBLE);
                mTVCurrentPosition.setVisibility(View.VISIBLE);
                mSeekBar.setMax(mediaDuration);
                mSeekBar.setProgress(mPlayer.getCurrentPosition());
                mTVCurrentPosition.setText(Utils.milliSecondsToTimer(mPlayer.getCurrentPosition(), true));
            }
            else
                notifyDataSetChanged();
            mHandler.postDelayed(mUpdateTimeTask, 200);
        }
    };
}
