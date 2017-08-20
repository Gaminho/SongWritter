package com.songwritter.gaminho.songwritter.interfaces;

import com.songwritter.gaminho.songwritter.beans.SongLyrics;
import com.songwritter.gaminho.songwritter.beans.Instrumental;

import java.util.List;

public interface SongInteractionListener {
    SongLyrics getSongLyrics();
    void updateSong(SongLyrics songLyrics);
    void createSong(SongLyrics songLyrics);
    void deleteSong(SongLyrics songLyrics);
    void addABeat(Instrumental instrumental);
    void removeBeats(List<Instrumental> beatsToRemove);
}
