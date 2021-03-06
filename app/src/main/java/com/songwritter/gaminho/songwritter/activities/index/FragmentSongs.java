package com.songwritter.gaminho.songwritter.activities.index;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.songwritter.gaminho.songwritter.Database;
import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.adapters.SongLyricsAdapter;
import com.songwritter.gaminho.songwritter.beans.SongLyrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FragmentSongs extends Fragment {

    List<SongLyrics> mSongs;
    ProgressBar pbSongs;

    public FragmentSongs() {
    }

    public static FragmentSongs newInstance() {
        return new FragmentSongs();
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
        final TextView tvSongs = (TextView) view.findViewById(R.id.loading_bar).findViewById(R.id.songs_count);
        pbSongs = (ProgressBar) view.findViewById(R.id.loading_bar).findViewById(R.id.pb_songs);
        pbSongs.setVisibility(View.VISIBLE);
        tvSongs.setText(getString(R.string.loading_songs));

        DatabaseReference myRef = Database.getTable(FirebaseAuth.getInstance().getCurrentUser(), Database.Table.LYRICS);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mSongs.clear();
                for (DataSnapshot songSnapshot : dataSnapshot.getChildren()) {
                    SongLyrics songLyrics = songSnapshot.getValue(SongLyrics.class);
                    songLyrics.setId(songSnapshot.getKey());
                    mSongs.add(songLyrics);
                }

                lvSongs.setAdapter(new SongLyricsAdapter(getActivity(), mSongs));
                if(getActivity() != null)
                    tvSongs.setText(String.format(Locale.FRANCE, getString(R.string.format_songs_count), mSongs.size()));
                pbSongs.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SONGS", "EROR: " + databaseError.getMessage());
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.songs, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }
}
