package com.songwritter.gaminho.songwritter.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.Utils;
import com.songwritter.gaminho.songwritter.beans.Action;

import java.util.List;

public class MyActionBar extends LinearLayout {

    private View[] actions;
    private int mCount;
    private List<Action> mActions;

    public MyActionBar(Context context) {
        super(context);
        init(context);
    }

    public MyActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyActionBar, 0, 0);

        try {
            mCount = a.getInt(R.styleable.MyActionBar_counter, 2);
        } finally {
            a.recycle();
        }

        init(context);
    }

    private void init(Context context) {

        LinearLayout rootView = (LinearLayout) inflate(context, R.layout.container, this);
        actions = new View[mCount];

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;

        for (int i = 0; i < mCount; i++) {
            row = inflater.inflate(R.layout.top_action, null);
            row.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rootView.addView(row);
            actions[i] = row;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        for(int i = 0 ; i < mCount; i++){
            ViewGroup.LayoutParams params = actions[i].getLayoutParams();
            params.height = getHeight();
            params.width = getWidth()/mCount;
            actions[i].setLayoutParams(params);
        }
    }

    // Utils

    private void setAction(int position) {

        if (position < mCount) {
            Action action = mActions.get(position);
            ((TextView) actions[position].findViewById(R.id.label)).setText(action.getLabel());
            actions[position].findViewById(R.id.under).setVisibility(INVISIBLE);
            actions[position].setOnClickListener(action.getListener());
            actions[position].setBackground(getContext().getDrawable(R.drawable.top_actions_btn));
            Utils.setImageView(action.getIconId(), (ImageView) actions[position].findViewById(R.id.icon));
        }
    }

    public void setActions(List<Action> actions) {
        this.mActions = actions;
        for(int i = 0 ; i < mCount ; i ++) {
            setAction(i);
        }
    }

    public void setSelected(int position) {

        for (int i = 0; i < mCount; i++) {
            if (i == position) {
                actions[position].findViewById(R.id.under).setVisibility(VISIBLE);
                ((ImageView) actions[i].findViewById(R.id.icon)).setColorFilter(getContext().getColor(R.color.colorAccent));
                ((TextView) actions[i].findViewById(R.id.label)).setTextColor(getContext().getColor(R.color.colorAccent));
                ((TextView) actions[i].findViewById(R.id.label)).setTypeface(null, Typeface.BOLD);
            }

            else {
                actions[i].findViewById(R.id.under).setVisibility(INVISIBLE);
                ((ImageView) actions[i].findViewById(R.id.icon)).setColorFilter(getContext().getColor(R.color.white0));
                ((TextView) actions[i].findViewById(R.id.label)).setTextColor(getContext().getColor(R.color.white0));
                ((TextView) actions[i].findViewById(R.id.label)).setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    public void click(int position){
        actions[position].performClick();
    }

    public void active(boolean active){
        if(active){
            for (int i = 0; i < mCount; i++) {
                actions[i].setOnClickListener(mActions.get(i).getListener());
                actions[i].findViewById(R.id.under).setVisibility(INVISIBLE);
                actions[i].setBackground(getContext().getDrawable(R.drawable.top_actions_btn));
                ((ImageView) actions[i].findViewById(R.id.icon)).setColorFilter(getContext().getColor(R.color.white0));
                ((TextView) actions[i].findViewById(R.id.label)).setTextColor(getContext().getColor(R.color.white0));
                ((TextView) actions[i].findViewById(R.id.label)).setTypeface(null, Typeface.NORMAL);
            }
        }
        else {
            for (int i = 0; i < mCount; i++) {
                actions[i].setOnClickListener(null);
                actions[i].setBackgroundColor(getContext().getColor(R.color.colorPrimary));
                actions[i].findViewById(R.id.under).setVisibility(INVISIBLE);
                ((ImageView) actions[i].findViewById(R.id.icon)).setColorFilter(getContext().getColor(R.color.colorPrimaryDarkest));
                ((TextView) actions[i].findViewById(R.id.label)).setTextColor(getContext().getColor(R.color.colorPrimaryDarkest));
                ((TextView) actions[i].findViewById(R.id.label)).setTypeface(null, Typeface.NORMAL);
            }
        }
    }
}
