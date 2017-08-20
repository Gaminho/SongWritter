package com.songwritter.gaminho.songwritter.beans;

import android.view.View;

public class Action {

    private final int iconId;
    private final String label;
    private final View.OnClickListener listener;

    public Action(int iconId, String label, View.OnClickListener listener) {
        this.iconId = iconId;
        this.label = label;
        this.listener = listener;
    }

    public int getIconId() {
        return iconId;
    }

    public String getLabel() {
        return label;
    }

    public View.OnClickListener getListener() {
        return listener;
    }
}
