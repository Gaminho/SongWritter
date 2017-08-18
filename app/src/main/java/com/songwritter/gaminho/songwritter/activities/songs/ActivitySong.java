package com.songwritter.gaminho.songwritter.activities.songs;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.songwritter.gaminho.songwritter.Database;
import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.Utils;
import com.songwritter.gaminho.songwritter.beans.SongLyrics;
import com.songwritter.gaminho.songwritter.interfaces.SongInteractionListener;

import java.util.HashMap;
import java.util.Map;

import static com.songwritter.gaminho.songwritter.Utils.LOG;

public class ActivitySong extends AppCompatActivity implements View.OnClickListener,
        SongInteractionListener {

    private static final int SECTION_VIEW = 0;
    private static final int SECTION_EDIT = 1;
    private static final int SECTION_MUSIC = 2;
    private static final int SECTION_RECORDS = 3;
    private static final int SECTION_SHARE = 4;
    private static final int SECTION_CREATE = -10;

    private FirebaseAuth mAuth;
    private static SongLyrics mSongLyrics;
    public static final String SONG_LYRICS = "lyrics";
    private static String mSongKey;
    private Fragment mCurrentFragment;
    private ProgressBar mPBSongs;

    private int mCurrentSection = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        mAuth = FirebaseAuth.getInstance();

        if(getIntent().getExtras() != null) {
            mSongKey = ((SongLyrics) getIntent().getExtras().getSerializable(SONG_LYRICS)).getId();
            mSongLyrics = (SongLyrics) getIntent().getExtras().getSerializable(SONG_LYRICS);
            selectTab(SECTION_VIEW);

            findViewById(R.id.top_songs).findViewById(R.id.top1).setOnClickListener(this);
            findViewById(R.id.top_songs).findViewById(R.id.top2).setOnClickListener(this);
            findViewById(R.id.top_songs).findViewById(R.id.top3).setOnClickListener(this);
            findViewById(R.id.top_songs).findViewById(R.id.top4).setOnClickListener(this);
            findViewById(R.id.top_songs).findViewById(R.id.top5).setOnClickListener(this);
        }

        else if(getIntent().getAction() != null && getIntent().getAction().equals(Utils.ACTION_CREATE)){
            mSongLyrics = new SongLyrics();
            selectTab(SECTION_CREATE);
        }

        mPBSongs = (ProgressBar) findViewById(R.id.pb_songs);
        mPBSongs.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.top1:
                selectTab(SECTION_VIEW);
                break;
            case R.id.top2:
                selectTab(SECTION_EDIT);
                break;
            case R.id.top3:
                selectTab(SECTION_MUSIC);
                break;
            case R.id.top4:
                selectTab(SECTION_RECORDS);
                break;
            case R.id.top5:
                selectTab(SECTION_SHARE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(mCurrentSection == SECTION_EDIT) {
            if(mCurrentFragment instanceof EditSong)
                if(needUpdate(((EditSong) mCurrentFragment).getSongLyricsFromUI())) {
                    quitEditDialog(SECTION_VIEW).show();
                }
                else {
                    mCurrentSection = -1;
                    selectTab(SECTION_VIEW);
                }
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.songs, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mCurrentSection == SECTION_EDIT || mCurrentSection == SECTION_CREATE){
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(true);
            menu.findItem(R.id.action_add).setVisible(false);
        }
        else if (mCurrentSection == SECTION_SHARE){
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_add).setVisible(false);
        }
        else{
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_add).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_delete)
            deleteSong(mSongLyrics);

        return super.onOptionsItemSelected(item);
    }

    // Utils

    private void selectTab(final int position){

        if(mCurrentSection != position) {

            if (mCurrentSection == SECTION_EDIT) {
                if(mCurrentFragment instanceof EditSong)
                    if(needUpdate(((EditSong) mCurrentFragment).getSongLyricsFromUI())) {
                        quitEditDialog(position).show();
                    }
                    else {
                        mCurrentSection = -1;
                        selectTab(position);
                    }
            }

            else {
                mCurrentSection = position;

                ((ImageView) findViewById(R.id.top_songs).findViewById(R.id.icon1)).setColorFilter(getColor(R.color.white0));
                ((ImageView) findViewById(R.id.top_songs).findViewById(R.id.icon2)).setColorFilter(getColor(R.color.white0));
                ((ImageView) findViewById(R.id.top_songs).findViewById(R.id.icon3)).setColorFilter(getColor(R.color.white0));
                ((ImageView) findViewById(R.id.top_songs).findViewById(R.id.icon4)).setColorFilter(getColor(R.color.white0));
                ((ImageView) findViewById(R.id.top_songs).findViewById(R.id.icon5)).setColorFilter(getColor(R.color.white0));

                findViewById(R.id.top_songs).findViewById(R.id.under1).setVisibility(View.INVISIBLE);
                findViewById(R.id.top_songs).findViewById(R.id.under2).setVisibility(View.INVISIBLE);
                findViewById(R.id.top_songs).findViewById(R.id.under3).setVisibility(View.INVISIBLE);
                findViewById(R.id.top_songs).findViewById(R.id.under4).setVisibility(View.INVISIBLE);
                findViewById(R.id.top_songs).findViewById(R.id.under5).setVisibility(View.INVISIBLE);

                ((TextView) findViewById(R.id.top_songs).findViewById(R.id.text1)).setTextColor(getColor(R.color.white0));
                ((TextView) findViewById(R.id.top_songs).findViewById(R.id.text1)).setTypeface(null, Typeface.NORMAL);
                ((TextView) findViewById(R.id.top_songs).findViewById(R.id.text2)).setTextColor(getColor(R.color.white0));
                ((TextView) findViewById(R.id.top_songs).findViewById(R.id.text2)).setTypeface(null, Typeface.NORMAL);
                ((TextView) findViewById(R.id.top_songs).findViewById(R.id.text3)).setTextColor(getColor(R.color.white0));
                ((TextView) findViewById(R.id.top_songs).findViewById(R.id.text3)).setTypeface(null, Typeface.NORMAL);
                ((TextView) findViewById(R.id.top_songs).findViewById(R.id.text4)).setTextColor(getColor(R.color.white0));
                ((TextView) findViewById(R.id.top_songs).findViewById(R.id.text4)).setTypeface(null, Typeface.NORMAL);
                ((TextView) findViewById(R.id.top_songs).findViewById(R.id.text5)).setTextColor(getColor(R.color.white0));
                ((TextView) findViewById(R.id.top_songs).findViewById(R.id.text5)).setTypeface(null, Typeface.NORMAL);

                int pixId = -1;
                int txtId = -1;
                int underId = -1;
                Fragment fragment = null;
                switch (position) {
                    case SECTION_VIEW:
                        pixId = R.id.icon1;
                        txtId = R.id.text1;
                        underId = R.id.under1;
                        fragment = ViewSong.newInstance();
                        break;
                    case SECTION_EDIT:
                        pixId = R.id.icon2;
                        txtId = R.id.text2;
                        underId = R.id.under2;
                        fragment = EditSong.newInstance();
                        break;
                    case SECTION_MUSIC:
                        pixId = R.id.icon3;
                        txtId = R.id.text3;
                        underId = R.id.under3;
                        fragment = ViewSong.newInstance();
                        break;
                    case SECTION_RECORDS:
                        pixId = R.id.icon4;
                        txtId = R.id.text4;
                        underId = R.id.under4;
                        fragment = ViewSong.newInstance();
                        break;
                    case SECTION_SHARE:
                        pixId = R.id.icon5;
                        txtId = R.id.text5;
                        underId = R.id.under5;
                        fragment = ViewSong.newInstance();
                        break;
                    case SECTION_CREATE:
                        LOG("SECTION_CREATE");
                        fragment = AddNewSong.newInstance();
                        break;
                }

                mCurrentFragment = fragment;
                getFragmentManager().beginTransaction()
                        .replace(R.id.song_fragment, fragment)
                        .commit();

                if(position != SECTION_CREATE) {
                    ((ImageView) findViewById(R.id.top_songs).findViewById(pixId)).setColorFilter(getColor(R.color.colorAccent));
                    findViewById(R.id.top_songs).findViewById(underId).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.top_songs).findViewById(txtId)).setTextColor(getColor(R.color.colorAccent));
                    ((TextView) findViewById(R.id.top_songs).findViewById(txtId)).setTypeface(null, Typeface.BOLD);
                }
                else{
                    findViewById(R.id.top_songs).setEnabled(false);
                }
                invalidateOptionsMenu();
            }
        }
    }

    private boolean needUpdate(SongLyrics songLyrics){
        return !songLyrics.getTitle().equals(mSongLyrics.getTitle())
                || !songLyrics.getContent().equals(mSongLyrics.getContent());
    }

    @Override
    public SongLyrics getSongLyrics() {
        return mSongLyrics;
    }

    @Override
    public void updateSong(SongLyrics songLyrics) {

        if(needUpdate(songLyrics)) {

            mPBSongs.setVisibility(View.VISIBLE);

            mSongLyrics.setId(null);
            mSongLyrics.setContent(songLyrics.getContent());
            mSongLyrics.setTitle(songLyrics.getTitle());
            mSongLyrics.setLastUpdate(System.currentTimeMillis());

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(mSongKey, mSongLyrics);
            DatabaseReference ref = Database.getTable(mAuth.getCurrentUser(), Database.Table.LYRICS);
            ref.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    mPBSongs.setVisibility(View.GONE);
                    if (databaseError != null) {
                        Toast.makeText(getApplication(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplication(), mSongLyrics.getTitle()+" has been updated!", Toast.LENGTH_SHORT).show();
                        mCurrentSection = -1;
                        selectTab(SECTION_VIEW);
                    }
                }
            });
        }

        else
            Toast.makeText(getApplication(), "Aucune modification", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void createSong(final SongLyrics songLyrics) {
        songLyrics.setAuthor(mAuth.getCurrentUser().getDisplayName());
        DatabaseReference ref = Database.getTable(mAuth.getCurrentUser(), Database.Table.LYRICS).push();
        ref.setValue(songLyrics, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(getApplication(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), songLyrics.getTitle()+" has been saved!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    public void deleteSong(SongLyrics songLyrics) {
        deleteDialog(songLyrics).show();
    }

    private AlertDialog.Builder quitEditDialog(final int position){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Quit ?");
        alert.setMessage("You are about to quit edit mode.\nIf lyrics have been edited, the changes will not be saved. Would you like to exit edit mode ?");

        alert.setPositiveButton("Quit without saving", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCurrentSection = -1;
                selectTab(position);
            }
        });

        alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() { // define the 'Cancel' button
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alert.setNegativeButton("Save and quit", new DialogInterface.OnClickListener() { // define the 'Cancel' button
            public void onClick(DialogInterface dialog, int which) {
                if(mCurrentFragment instanceof EditSong)
                    updateSong(((EditSong) mCurrentFragment).getSongLyricsFromUI());
                dialog.cancel();
            }
        });
        return alert;
    }

    private AlertDialog.Builder deleteDialog(final SongLyrics songLyrics){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete ?");
        alert.setMessage("Delete '"+songLyrics.getTitle()+"' from your lyrics ?");

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference ref = Database.getTable(mAuth.getCurrentUser(), Database.Table.LYRICS);
                ref = ref.child(songLyrics.getId());
                ref.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(getApplication(), "'"+songLyrics.getTitle()+"' has been deleted !", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alert;
    }
}
