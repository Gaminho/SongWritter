package com.songwritter.gaminho.songwritter.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.songwritter.gaminho.songwritter.C;
import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.beans.SongLyrics;

public class NewText extends AppCompatActivity {

    private String UI_MOD;
    private static SongLyrics mSongLyrics;
    public static final String SONG_LYRICS = "lyrics";

    // Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_text);

        UI_MOD = getIntent().getAction() != null ? getIntent().getAction() : C.ACTION_CREATE;

        if(!UI_MOD.equals(C.ACTION_CREATE) && getIntent().getExtras() != null) {
            mSongLyrics = (SongLyrics) getIntent().getExtras().getSerializable(SONG_LYRICS);
        }

        handleMod(UI_MOD);

        Log.e("Cycle", "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Cycle", "onStart");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e("Cycle", "onCreateOptionsMenu");
        if(UI_MOD.equals(C.ACTION_VIEW))
            getMenuInflater().inflate(R.menu.songs, menu);
        else
            getMenuInflater().inflate(R.menu.songs, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.e("Cycle", "onPrepareOptionsMenu");
        if(UI_MOD.equals(C.ACTION_VIEW)){
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(true);
        }
        else{
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(true);
            menu.findItem(R.id.action_edit).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_save){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference(C.DB_TABLE_SONG).push();
            ref.setValue(getSongLyrics(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e("SAVE",databaseError.getMessage());
                        finish();
                    } else {
                        Log.e("SAVE","Data saved successfully.");
                    }
                }
            });
        }

        else if (id == R.id.action_delete){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference(C.DB_TABLE_SONG).child(mSongLyrics.getId());
            ref.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Log.e("Deletion", mSongLyrics.getId() + " has been removed!");
                    finish();
                }
            });
        }

        else if (id == R.id.action_edit){
            UI_MOD = C.ACTION_EDIT;
            invalidateOptionsMenu();
            handleMod(UI_MOD);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!UI_MOD.equals(C.ACTION_EDIT))
            super.onBackPressed();
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Back");
            alert.setMessage("Quit without saving ?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
//                    dialog.dismiss();
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.show();
        }
    }

    // Utils

    public SongLyrics getSongLyrics(){

        String title = ((EditText) findViewById(R.id.etTitle)).getText().toString();
        String content = ((EditText) findViewById(R.id.etContent)).getText().toString();
        String author = "me";
        long creation = System.currentTimeMillis();
        long lastUpdate = System.currentTimeMillis();
        return new SongLyrics(title, content, author, creation, lastUpdate);
    }


    private void handleMod(String mod){

        EditText edTitle = (EditText) findViewById(R.id.etTitle);
        EditText edContent = (EditText) findViewById(R.id.etContent);

        if(mod.equals(C.ACTION_VIEW)){
            edTitle.setEnabled(false);
            edContent.setEnabled(false);
        }
        else{
            edTitle.setEnabled(true);
            edContent.setEnabled(true);
        }

        if(!mod.equals(C.ACTION_CREATE) && mSongLyrics != null){
            edTitle.setText(mSongLyrics.getTitle());
            edContent.setText(mSongLyrics.getContent());
        }
        else{
            edTitle.setText("");
            edContent.setText("");
        }
    }
}
