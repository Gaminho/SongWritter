package com.songwritter.gaminho.songwritter.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.Utils;
import com.songwritter.gaminho.songwritter.beans.Instrumental;
import com.songwritter.gaminho.songwritter.beans.MemoRecord;

import java.util.List;

import static com.songwritter.gaminho.songwritter.Utils.LOG;

public class RecordAdapter extends ArrayAdapter<MemoRecord> {

    Activity context;
    List<MemoRecord> records;
    private SparseBooleanArray mSelectedItemsIds;
    private boolean selectionMode = false;

    public RecordAdapter(Activity context, int resId, List<MemoRecord> beats) {
        super(context, resId, beats);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.records = beats;
    }

    private class ViewHolder {

        TextView mTVTitle;
        TextView mTVDuration;
        TextView mTVCreation;
        RelativeLayout mRLRow;
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
            holder.mTVDuration = (TextView) convertView.findViewById(R.id.record_duration);

            holder.mRLRow = (RelativeLayout) convertView.findViewById(R.id.row);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MemoRecord record = getItem(position);
        holder.mTVTitle.setText(record.getTitle());
        holder.mTVCreation.setText(Utils.formatTS(record.getCreation(), Utils.DateFormat.FULL));
        holder.mTVDuration.setText(Utils.milliSecondsToTimer(record.getDuration()));

        if(!selectionMode) {
            LOG("Not Selection Mode");
        }
        else{
            LOG("Selection Mode");
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
}
