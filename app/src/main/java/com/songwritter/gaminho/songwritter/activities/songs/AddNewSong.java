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

public class AddNewSong extends Fragment {

    SongInteractionListener mListener;

    EditText mETContent, mETTitle;

    public AddNewSong() {
    }

    public static AddNewSong newInstance() {
        return new AddNewSong();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_save){
            if(isValid(getSongLyricsFromUI()))
                mListener.createSong(getSongLyricsFromUI());
        }

        return super.onOptionsItemSelected(item);
    }

    // Utils

    public SongLyrics getSongLyricsFromUI(){
        String title = mETTitle.getText().toString().trim();
        String content = mETContent.getText().toString().trim();
        long creation = System.currentTimeMillis();
        long lastUpdate = System.currentTimeMillis();
        return new SongLyrics(title, content, creation, lastUpdate);
    }

    public boolean isValid(SongLyrics songLyrics){

        if(songLyrics.getTitle() == null || songLyrics.getTitle().isEmpty()) {
            Toast.makeText(getActivity(), "Title can not be null!", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(songLyrics.getContent() == null || songLyrics.getContent().isEmpty()) {
            Toast.makeText(getActivity(), "Lyrics can not be null!", Toast.LENGTH_SHORT).show();
            return false;
        }

        else
            return true;
    }
}
