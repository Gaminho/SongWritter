package com.songwritter.gaminho.songwritter.interfaces;

import com.songwritter.gaminho.songwritter.beans.Instrumental;
import com.songwritter.gaminho.songwritter.beans.SongLyrics;

public interface SongInteractionListener {
    SongLyrics getSongLyrics();
    void updateSong(SongLyrics songLyrics);
    void createSong(SongLyrics songLyrics);
    void deleteSong(SongLyrics songLyrics);
    void addABeat(Instrumental beat);
}
