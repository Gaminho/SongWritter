package com.songwritter.gaminho.songwritter.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.songwritter.gaminho.songwritter.C;
import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.beans.SongLyrics;

import java.util.HashMap;
import java.util.Map;

public class SongActivity extends AppCompatActivity {

    private String UI_MOD;
    private static SongLyrics mSongLyrics;
    public static final String SONG_LYRICS = "lyrics";

    //View
    private EditText etContent, etTitle;

    // Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        UI_MOD = getIntent().getAction() != null ? getIntent().getAction() : C.ACTION_CREATE;

        if(!UI_MOD.equals(C.ACTION_CREATE) && getIntent().getExtras() != null) {
            mSongLyrics = (SongLyrics) getIntent().getExtras().getSerializable(SONG_LYRICS);
        }

        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent = (EditText) findViewById(R.id.etContent);

        handleMod(UI_MOD);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.songs, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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

//            if(UI_MOD.equals(C.ACTION_EDIT)){
//
//            }

            if(UI_MOD.equals(C.ACTION_CREATE)) {
                DatabaseReference ref = database.getReference(C.DB_TABLE_SONG).push();
                Log.e("Save", getSongLyrics().toString());
                ref.setValue(getSongLyrics(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e("SAVE", databaseError.getMessage());
                            Toast.makeText(getApplication(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication(), getSongLyrics().getTitle()+" has been saved!", Toast.LENGTH_SHORT).show();
                            Log.e("SAVE", "Data saved successfully.");
                            finish();
                        }
                    }
                });
            }

            else if (UI_MOD.equals(C.ACTION_EDIT)){
                DatabaseReference ref = database.getReference(C.DB_TABLE_SONG);
                String key = mSongLyrics.getId();
                mSongLyrics.setId(null);
                mSongLyrics.setContent(getSongLyrics().getContent());
                mSongLyrics.setTitle(getSongLyrics().getTitle());
                mSongLyrics.setLastUpdate(System.currentTimeMillis());

                Log.e("UPDATE", mSongLyrics.toString());
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(key, mSongLyrics);
                ref.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e("UPDATE", databaseError.getMessage());
                            Toast.makeText(getApplication(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication(), mSongLyrics.getTitle()+" has been updated!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        }

        else if (id == R.id.action_delete){

            AlertDialog.Builder alert = showDialog("Delete", "Remove '"+mSongLyrics.getTitle()+"' from your lyrics ?");
            alert.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.show();
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
        if(!UI_MOD.equals(C.ACTION_EDIT)) {
            super.onBackPressed();
        }

        else{
            AlertDialog.Builder alert = showDialog("Back", "Quit without saving ?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
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

        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
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

    private AlertDialog.Builder showDialog(String title, String content){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage(content);
        return alert;
    }

}
