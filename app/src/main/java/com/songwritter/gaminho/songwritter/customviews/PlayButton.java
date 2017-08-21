package com.songwritter.gaminho.songwritter.customviews;

import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import com.songwritter.gaminho.songwritter.R;

import java.io.File;
import java.io.IOException;

import static com.songwritter.gaminho.songwritter.Utils.LOG;

public class PlayButton extends FloatingActionButton implements MediaPlayer.OnCompletionListener {

    private MediaPlayer mPlayer;
    boolean isPlaying = false;
    private OnPlayingListener mListener;
    private String mMediaName;

    private OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            if (!isPlaying) {
                setImageDrawable(getContext().getDrawable(R.drawable.ic_stop_white_18dp));
                startPlaying();
            } else {
                setImageDrawable(getContext().getDrawable(R.drawable.ic_play_arrow_white_18dp));
                isPlaying = false;
                stopPlaying();
            }
        }
    };

    public PlayButton(Context context) {
        super(context);
        init();
    }

    public PlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void startPlaying() {
        if (mMediaName != null) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(this);
            try {
                mPlayer.setDataSource(mMediaName);
                mPlayer.prepare();
                mPlayer.start();
                isPlaying = true;
                if(mListener != null) {
                    mListener.setPlaying(true);
                    mListener.startPlaying();
                }
            } catch (IOException e) {
                LOG("prepare() failed: " + e);
            }
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer.setOnCompletionListener(null);
        isPlaying = false;
        mPlayer = null;
        if (mListener != null) {
            mListener.stopPlaying();
            mListener.setPlaying(false);
        }
    }

    private void init() {
        setImageDrawable(getContext().getDrawable(R.drawable.ic_play_arrow_white_18dp));
        setOnClickListener(clicker);
        mMediaName = null;
        setEnabled(false);
    }

    public void detachListener() {
        assert mListener != null;
        mListener = null;
    }

    public void registerMediaPlayerListener(Fragment context) {
        if (context instanceof OnPlayingListener) {
            mListener = (OnPlayingListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlayingListener");
        }
    }

    public boolean setMedia(String filename) {
        File media = new File(filename);
        if (media.exists()) {
            mMediaName = filename;
        } else {
            mMediaName = null;
        }

        setEnabled(mMediaName != null);
        mListener.mediaHasBeenUpdated(mMediaName);
        return media.exists();
    }

    public MediaPlayer getPlayer(){
        return mPlayer;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        isPlaying = false;
        setImageDrawable(getContext().getDrawable(R.drawable.ic_play_arrow_white_18dp));
    }

    public interface OnPlayingListener {
        void stopPlaying();
        void startPlaying();
        void setPlaying(boolean isPlaying);
        void mediaHasBeenUpdated(String filename);
    }

}
