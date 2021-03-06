package com.songwritter.gaminho.songwritter.activities.songs;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.interfaces.SongInteractionListener;


public class ViewSong extends Fragment {

    @Nullable
    private
    SongInteractionListener mListener;

    @NonNull
    public static ViewSong newInstance() {
        return new ViewSong();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.song_view, container, false);
        final TextView tvLyrics = (TextView) view.findViewById(R.id.song_lyrics);
        tvLyrics.setText(mListener.getSongLyrics().getContent());


        final TextView tvTitle = (TextView) view.findViewById(R.id.song_title);
        tvTitle.setText(mListener.getSongLyrics().getTitle());

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

}
