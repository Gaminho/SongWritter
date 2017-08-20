package com.songwritter.gaminho.songwritter.activities.songs;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.songwritter.gaminho.songwritter.Database;
import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.Utils;
import com.songwritter.gaminho.songwritter.beans.Action;
import com.songwritter.gaminho.songwritter.beans.Instrumental;
import com.songwritter.gaminho.songwritter.beans.SongLyrics;
import com.songwritter.gaminho.songwritter.customviews.CustomAlertDialogBuilder;
import com.songwritter.gaminho.songwritter.customviews.MusicPlayer;
import com.songwritter.gaminho.songwritter.customviews.MyActionBar;
import com.songwritter.gaminho.songwritter.interfaces.SongInteractionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivitySong extends AppCompatActivity implements SongInteractionListener,
        MusicPlayer.MusicPlayerListener {

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

    private MyActionBar mActionBar;

    private int mCurrentSection = -1;

    private MusicPlayer mMusicPlayer;

    // Activity Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        mAuth = FirebaseAuth.getInstance();

        mPBSongs = (ProgressBar) findViewById(R.id.pb_songs);
        mPBSongs.setVisibility(View.GONE);

        mActionBar = setUpActionBar(R.id.actionBar);

        if(getIntent().getExtras() != null) {
            mSongKey = ((SongLyrics) getIntent().getExtras().getSerializable(SONG_LYRICS)).getId();
            mSongLyrics = (SongLyrics) getIntent().getExtras().getSerializable(SONG_LYRICS);
            mActionBar.click(SECTION_VIEW);
        }

        else if(getIntent().getAction() != null && getIntent().getAction().equals(Utils.ACTION_CREATE)){
            mSongLyrics = new SongLyrics();
            selectTab(SECTION_CREATE);
        }

        mMusicPlayer = (MusicPlayer) findViewById(R.id.music_player);
        mMusicPlayer.setPlaylist(mSongLyrics.getBeats());
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
        else if (mCurrentSection == SECTION_MUSIC){
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_add).setVisible(true);
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
//        int id = item.getItemId();
//
//        if(id == R.id.action_delete)
//            deleteSong(mSongLyrics);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mCurrentSection == SECTION_EDIT) {
            if(mCurrentFragment instanceof EditSong) {

                if (((EditSong) mCurrentFragment).needUpdate()) {
                    quitEditDialog(SECTION_VIEW).show();
                }
                else {
                    mCurrentSection = -1;
                    mActionBar.click(SECTION_VIEW);
                }
            }
        }
        else if (mCurrentSection == SECTION_MUSIC){
            if (mMusicPlayer != null && mMusicPlayer.isPlaying()) {
                backWhilePlayingDialog(SECTION_VIEW).show();
            } else {
                mCurrentSection = -1;
                mActionBar.click(SECTION_VIEW);
            }
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isPlaying())
            mMusicPlayer.stop();
    }

    // Utils

    private void selectTab(int position){

        if(mCurrentSection != position) {

            if (mCurrentSection == SECTION_EDIT) {
                if(mCurrentFragment instanceof EditSong) {
                    if (((EditSong) mCurrentFragment).needUpdate()) {
                        quitEditDialog(position).show();
                    } else {
                        mCurrentSection = -1;
                        selectTab(position);
                    }
                }
            }

            else if(mCurrentSection == SECTION_MUSIC){
                if(mMusicPlayer != null && mMusicPlayer.isPlaying())
                    backWhilePlayingDialog(position).show();
                else {
                    mCurrentSection = -1;
                    selectTab(position);
                }
            }

            else {

                mCurrentSection = position;

                Fragment fragment = null;
                switch (position) {
                    case SECTION_VIEW:
                        fragment = ViewSong.newInstance();
                        break;
                    case SECTION_EDIT:
                        fragment = EditSong.newInstance();
                        break;
                    case SECTION_MUSIC:
                        fragment = AudioSong.newInstance();
                        break;
                    case SECTION_RECORDS:
                        fragment = RecordSong.newInstance();
                        break;
                    case SECTION_SHARE:
                        fragment = ViewSong.newInstance();
                        break;
                    case SECTION_CREATE:
                        fragment = AddNewSong.newInstance();
                        break;
                }

                mCurrentFragment = fragment;
                getFragmentManager().beginTransaction()
                        .replace(R.id.song_fragment, fragment)
                        .commit();

                if(position == SECTION_CREATE){
                    mActionBar.active(false);
                }

                invalidateOptionsMenu();
            }
        }
    }

    private MyActionBar setUpActionBar(int barId){

        String[] sections = getResources().getStringArray(R.array.song_sections);

        final MyActionBar actionButton = (MyActionBar) findViewById(barId);
        List<Action> actions = new ArrayList<>();
        actions.add(new Action(R.mipmap.ic_format_align_left_white_18dp, sections[SECTION_VIEW], new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(SECTION_VIEW);
                actionButton.setSelected(SECTION_VIEW);
            }
        }));

        actions.add(new Action(R.mipmap.ic_mode_edit_white_18dp, sections[SECTION_EDIT], new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(SECTION_EDIT);
                actionButton.setSelected(SECTION_EDIT);
            }
        }));
        actions.add(new Action(R.drawable.musical_notes1600, sections[SECTION_MUSIC], new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(SECTION_MUSIC);
                actionButton.setSelected(SECTION_MUSIC);
            }
        }));
        actions.add(new Action(R.mipmap.ic_keyboard_voice_white_18dp, sections[SECTION_RECORDS], new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(SECTION_RECORDS);
                actionButton.setSelected(SECTION_RECORDS);
            }
        }));
        actions.add(new Action(R.mipmap.ic_share_white_18dp, sections[SECTION_SHARE], new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(SECTION_SHARE);
                actionButton.setSelected(SECTION_SHARE);
            }
        }));

        actionButton.setActions(actions);

        return actionButton;
    }

    private CustomAlertDialogBuilder quitEditDialog(final int position){

        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(this);

        builder.setTitle(getString(R.string.dialog_title_warning));
        builder.setMessage(getString(R.string.dialog_save_modification));

        builder.setPositiveButton(getString(R.string.dialog_quit_without_saving), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentSection = -1;
                mActionBar.click(position);
                dialog.dismiss();
            }
        });

        builder.setNeutralButton(getString(R.string.cancel), null);

        builder.setNegativeButton(getString(R.string.dialog_save_and_quit), new DialogInterface.OnClickListener() { // define the 'Cancel' button
            public void onClick(DialogInterface dialog, int which) {
                if(mCurrentFragment instanceof EditSong)
                    updateSong(((EditSong) mCurrentFragment).getSongLyricsFromUI());
                dialog.cancel();
            }
        });

        return builder;
    }

    private AlertDialog.Builder backWhilePlayingDialog(final int position){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.dialog_title_media_playing));
        alert.setMessage(getString(R.string.dialog_continue_playing));

        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentSection = -1;
                mActionBar.click(position);
            }
        });

        alert.setNeutralButton(getString(R.string.cancel), null);

        alert.setNegativeButton(getString(R.string.dialog_stop_playing), new DialogInterface.OnClickListener() { // define the 'Cancel' button
            public void onClick(DialogInterface dialog, int which) {
                mMusicPlayer.stop();
                mCurrentSection = -1;
                mActionBar.click(position);
                dialog.dismiss();
            }
        });
        return alert;
    }

    private AlertDialog.Builder deleteDialog(final SongLyrics songLyrics){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.dialog_title_warning));
        alert.setMessage(String.format(Locale.FRANCE,getString(R.string.format_delete_song), songLyrics.getTitle()));

        alert.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference ref = Database.getTable(mAuth.getCurrentUser(), Database.Table.LYRICS);
                ref = ref.child(songLyrics.getId());
                ref.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(getApplication(),  String.format(Locale.FRANCE, getString(R.string.format_has_been_deleted), songLyrics.getTitle()), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alert;
    }


    // SongInteractionListener

    @Override
    public SongLyrics getSongLyrics() {
        return mSongLyrics;
    }

    @Override
    public void updateSong(SongLyrics songLyrics) {

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
                    Toast.makeText(getApplication(), String.format(Locale.FRANCE, getString(R.string.format_has_been_updated), mSongLyrics.getTitle()), Toast.LENGTH_SHORT).show();
                    mCurrentSection = -1;
                    mActionBar.click(SECTION_VIEW);
                }
            }
        });
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
                    Toast.makeText(getApplication(), String.format(Locale.FRANCE, getString(R.string.format_has_been_saved), mSongLyrics.getTitle()), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    public void deleteSong(SongLyrics songLyrics) {
        deleteDialog(songLyrics).show();
    }

    @Override
    public void addABeat(final Instrumental beat) {
        final List<Instrumental> beats = mSongLyrics.getBeats() != null ? mSongLyrics.getBeats() : new ArrayList<Instrumental>();
        beats.add(beat);
        mSongLyrics.setBeats(beats);
        mSongLyrics.setId(null);
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
                    Toast.makeText(getApplication(), String.format(Locale.FRANCE, getString(R.string.format_beat_has_been_added),beat.getTitle()), Toast.LENGTH_SHORT).show();
                    mCurrentSection = -1;
                    selectTab(SECTION_MUSIC);
                    mMusicPlayer.setPlaylist(beats);
                }
            }
        });
    }

    @Override
    public void removeBeats(List<Instrumental> beatsToRemove) {
        List<Instrumental> newList = new ArrayList<>();

        List<String> namesToRemove = new ArrayList<>();
        for(Instrumental beat : beatsToRemove)
            namesToRemove.add(beat.getTitle());

        for(Instrumental beat : getSongLyrics().getBeats()){
            if(!namesToRemove.contains(beat.getTitle()))
                newList.add(beat);
        }

        mSongLyrics.setId(null);
        mSongLyrics.setBeats(newList);
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
                }
            }
        });

        for(Instrumental beat : beatsToRemove){
            File file = new File(beat.getPath());
            if(file.exists()) {
                file.delete();
            }
        }

    }

    // Utils

    public void playSongAtPosition(int position){
        mMusicPlayer.playMedia(position);
    }

    public void pauseSongAtPosition(int position){
        mMusicPlayer.pauseMedia(position);
    }

    public void resumeSong(){
        mMusicPlayer.resume();
    }

    public boolean isPlaying(){
        return mMusicPlayer != null && mMusicPlayer.isPlaying();
    }

    public boolean inPause(){
        return mMusicPlayer != null && mMusicPlayer.isPause();
    }

    public int getCurrentPosition(){
        assert mMusicPlayer != null;
        return mMusicPlayer.getCurrentMediaPosition();
    }

    @Override
    public void pausePressed(int mediaPosition) {
        Fragment f = getFragmentManager().findFragmentById(R.id.song_fragment);
        if (f instanceof AudioSong)
            ((AudioSong) f).updatePauseUI(mediaPosition);
    }

    @Override
    public void playPressed(int mediaPosition) {
        Fragment f = getFragmentManager().findFragmentById(R.id.song_fragment);
        if (f instanceof AudioSong)
            ((AudioSong) f).updatePlayUI(mediaPosition);
    }

}