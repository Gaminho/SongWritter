package com.songwritter.gaminho.songwritter.activities.songs;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.beans.SongLyrics;
import com.songwritter.gaminho.songwritter.interfaces.SongInteractionListener;


public class EditSong extends Fragment {

    private SongInteractionListener mListener;

    private EditText mETContent;
    private EditText mETTitle;

    public EditSong() {
    }

    public static EditSong newInstance() {
        return new EditSong();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.song_add_edit, container, false);
        mETContent = (EditText) view.findViewById(R.id.song_lyrics);
        mETContent.setText(mListener.getSongLyrics().getContent());

        mETTitle= (EditText) view.findViewById(R.id.song_title);
        mETTitle.setText(mListener.getSongLyrics().getTitle());

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SongInteractionListener) {
            mListener = (SongInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SongInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean needUpdate(){
        return !getSongLyricsFromUI().getTitle().equals(mListener.getSongLyrics().getTitle())
                || !getSongLyricsFromUI().getContent().equals(mListener.getSongLyrics().getContent());
    }

    // Utils

    public SongLyrics getSongLyricsFromUI(){
        SongLyrics songLyrics = new SongLyrics();
        songLyrics.setTitle(mETTitle.getText().toString().trim());
        songLyrics.setContent(mETContent.getText().toString().trim());
        return songLyrics;
    }
}
