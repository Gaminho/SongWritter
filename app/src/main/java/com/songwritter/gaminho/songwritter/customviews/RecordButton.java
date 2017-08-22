package com.songwritter.gaminho.songwritter.customviews;

import android.app.Fragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import com.songwritter.gaminho.songwritter.R;

import java.io.File;
import java.io.IOException;

import static com.songwritter.gaminho.songwritter.Utils.LOG;

public class RecordButton extends FloatingActionButton {

    MediaRecorder mRecorder;
    boolean mStartRecording = true;
    OnRecordingListener mListener;
    static String mFileName;

    private Drawable stdImg;
    private Drawable recordImg;
    private int tintStd;
    private int tintRecord;
    private int bkgColor;
    private int bkgStateColor;


    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            onRecord(mStartRecording);
            if (mStartRecording) {
                setImageDrawable(recordImg);
                setColorFilter(tintRecord);
            } else {
                setImageDrawable(stdImg);
                setColorFilter(tintStd);
            }
            mStartRecording = !mStartRecording;
        }
    };

    public RecordButton(Context context) {
        super(context);
        init(context);
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RecordButton,0,0);
        stdImg = getContext().getDrawable(R.drawable.ic_mic_white_18dp);
        recordImg = getContext().getDrawable(R.drawable.ic_stop_white_18dp);

        try {
            if(a.getDrawable(R.styleable.RecordButton_icon_default) != null)
                stdImg = a.getDrawable(R.styleable.RecordButton_icon_default);
            if(a.getDrawable(R.styleable.RecordButton_icon_recording) != null)
                recordImg = a.getDrawable(R.styleable.RecordButton_icon_recording);

            tintStd = a.getInt(R.styleable.RecordButton_tint_default, getContext().getColor(R.color.white0));
            tintRecord = a.getInt(R.styleable.RecordButton_tint_recording, getContext().getColor(R.color.white0));
            bkgColor = a.getInt(R.styleable.RecordButton_btn_color, getContext().getColor(R.color.colorPrimary));
            bkgStateColor = a.getInt(R.styleable.RecordButton_pressed_color, getContext().getColor(R.color.colorPrimaryDarkest));
        } finally {
            a.recycle();
        }

        init(context);
    }

    public void registerMediaRecorderListener(Fragment context){
        if (context instanceof OnRecordingListener) {
            mListener = (OnRecordingListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecordingInterface");
        }
    }

    public void unregisterMediaRecorderListener(){
        mListener = null;
        mStartRecording = true;
        setImageDrawable(stdImg);
        setColorFilter(tintStd);
        if(mRecorder!= null)
            mRecorder.release();
        mRecorder = null;
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            LOG("prepare() failed: " + e);
        }

        mRecorder.start();
        if(mListener != null)
            mListener.startRecording();
    }

    private void stopRecording() {
        mRecorder.stop();
        LOG("stopRecording()");
        mRecorder.release();
        mRecorder = null;
        File file = new File(mFileName);
        if(mListener != null && file.exists()) {
            mListener.stopRecording(file);
        }
    }

    public interface OnRecordingListener{
        void stopRecording(File record);
        void startRecording();
    }

    public void stopListening(){
        assert mListener != null;
        mListener = null;
        if(mStartRecording)
            mStartRecording = false;
    }

    private void init(Context context){

        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed}, // Pressed
                        new int[]{}, // Normal state
                },
                new int[] {
                        bkgStateColor,
                        bkgColor,
                }
        );

        setBackgroundTintList(myColorStateList);
        setImageDrawable(stdImg);
        setColorFilter(tintStd);
        setOnClickListener(clicker);
        mFileName =  context.getExternalCacheDir().getAbsolutePath();
        mFileName += "/temp.3gp";
    }

}
