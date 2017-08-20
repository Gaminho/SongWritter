package com.songwritter.gaminho.songwritter.interfaces;

import android.media.MediaPlayer;
import android.widget.SeekBar;

import com.songwritter.gaminho.songwritter.beans.Instrumental;

public interface MediaInteractionListener extends MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    void play(Instrumental beat);
    void pause();
    void resume();
    void stop();
    void next();
    void previous();
}
