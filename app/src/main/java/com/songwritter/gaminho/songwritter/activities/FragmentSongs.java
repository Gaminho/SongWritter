package com.songwritter.gaminho.songwritter.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.songwritter.gaminho.songwritter.C;
import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.activities.adapters.SongLyricsAdapter;
import com.songwritter.gaminho.songwritter.beans.SongLyrics;

import java.util.ArrayList;
import java.util.List;


public class FragmentSongs extends Fragment {

    List<SongLyrics> mSongs;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private OnFragmentInteractionListener mListener;

    public FragmentSongs() {
    }

    public static FragmentSongs newInstance(String param1, String param2) {
        FragmentSongs fragment = new FragmentSongs();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentSongs newInstance() {
        FragmentSongs fragment = new FragmentSongs();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSongs = new ArrayList<>();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        final ListView lvSongs = (ListView) view.findViewById(R.id.lv_songs);
        final TextView tvSongs = (TextView) view.findViewById(R.id.songs_count);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(C.DB_TABLE_SONG);

        // Attach a listener to read the data at our posts reference
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.e("SONGS", "DATA CHANGE: " + dataSnapshot);
                tvSongs.setText("Vous avez " + dataSnapshot.getChildrenCount() + " textes");
                mSongs.clear();
                for (DataSnapshot songSnapshot: dataSnapshot.getChildren()) {
                    SongLyrics songLyrics = songSnapshot.getValue(SongLyrics.class);
                    songLyrics.setId(songSnapshot.getKey());
                    mSongs.add(songLyrics);
                }

                lvSongs.setAdapter(new SongLyricsAdapter(getActivity(), mSongs));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SONGS", "EROR: " + databaseError.getMessage());
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.song_lyrics, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
