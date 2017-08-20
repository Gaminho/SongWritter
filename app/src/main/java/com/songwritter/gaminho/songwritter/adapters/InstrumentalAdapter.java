package com.songwritter.gaminho.songwritter.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.Utils;
import com.songwritter.gaminho.songwritter.beans.Instrumental;

import java.util.List;

public class InstrumentalAdapter extends ArrayAdapter<Instrumental> {

    Activity context;
    List<Instrumental> beats;
    private SparseBooleanArray mSelectedItemsIds;
    private boolean selectionMode = false;

    public InstrumentalAdapter(Activity context, int resId, List<Instrumental> beats) {
        super(context, resId, beats);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.beats = beats;
    }

    private class ViewHolder {

        TextView mTVTitle;
        TextView mTVAuthor;
        TextView mTVType;
        ImageView mIVShared;
        ImageView mIVPlayStatus;
        RelativeLayout mRLMusicAction;
        RelativeLayout mRLRow;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_beats, null);
            holder = new ViewHolder();
            holder.mTVTitle = (TextView) convertView.findViewById(R.id.beat_title);
            holder.mTVAuthor = (TextView) convertView.findViewById(R.id.beat_author);
            holder.mTVType = (TextView) convertView.findViewById(R.id.beat_details);

            holder.mIVShared = (ImageView) convertView.findViewById(R.id.beat_shared);
            holder.mIVPlayStatus = (ImageView) convertView.findViewById(R.id.play_pause_stop);

            holder.mRLMusicAction = (RelativeLayout) convertView.findViewById(R.id.music_action);
            holder.mRLRow = (RelativeLayout) convertView.findViewById(R.id.row);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Instrumental beat = getItem(position);
        holder.mTVTitle.setText(beat.getTitle());
        holder.mTVAuthor.setText(beat.getAuthor());
        String type = beat.getType().equals(Instrumental.Type.FACE_A) ? "Face A" : "Face B";
        holder.mTVType.setText(type);

        if(!selectionMode) {
            holder.mRLMusicAction.setVisibility(View.VISIBLE);
        }
        else{
            holder.mRLMusicAction.setVisibility(View.GONE);
        }

        int imgId;
        if (position % 2 == 0)
            imgId = R.drawable.ic_cloud_off_white_18dp;
        else
            imgId = R.drawable.ic_cloud_queue_white_18dp;

        Utils.setImageView(imgId, (ImageView) holder.mIVShared.findViewById(R.id.beat_shared));
        if(mSelectedItemsIds.get(position)) {
            holder.mRLRow.setBackgroundColor(context.getColor(R.color.colorPrimary));
            holder.mTVTitle.setTextColor(context.getColor(R.color.white0));
            holder.mTVAuthor.setTextColor(context.getColor(R.color.white0));
            holder.mTVType.setTextColor(context.getColor(R.color.white0));
        }
        else{
            holder.mRLRow.setBackgroundColor(Color.TRANSPARENT);
            holder.mTVTitle.setTextColor(context.getColor(R.color.black0));
            holder.mTVAuthor.setTextColor(context.getColor(R.color.grey));
            holder.mTVType.setTextColor(context.getColor(R.color.black0));
        }

        return convertView;
    }

    @Override
    public void add(Instrumental beat) {
        beats.add(beat);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Instrumental beat) {
        beats.remove(beat);
        notifyDataSetChanged();
    }

    public List<Instrumental> getBeats() {
        return beats;
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
}
