package com.songwritter.gaminho.songwritter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.beans.SongLyrics;

public class NewText extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_text);
    }

    public void saveLyrics(View view){

        String title = ((EditText) findViewById(R.id.etTitle)).getText().toString();
        String content = ((EditText) findViewById(R.id.etContent)).getText().toString();
        SongLyrics songLyrics = new SongLyrics(title, content, "me");
        Log.i("SongLyrics", songLyrics.toString());
    }
}
