package com.songwritter.gaminho.songwritter.customviews;

import android.app.Fragment;
import android.content.Context;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.view.View;

import java.io.File;
import java.io.IOException;

import static com.songwritter.gaminho.songwritter.Utils.LOG;

public class RecordingButton extends android.support.v7.widget.AppCompatButton {

    MediaRecorder mRecorder;
    boolean mStartRecording = true;
    OnRecordingListener mListener;
    static String mFileName;

    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            onRecord(mStartRecording);
            if (mStartRecording) {
                setText("Stop recording");
            } else {
                setText("Start recording");
            }
            mStartRecording = !mStartRecording;
        }
    };

    public RecordingButton(Context context) {
        super(context);
        setText("Start recording");
        setOnClickListener(clicker);
        mFileName =  context.getExternalCacheDir().getAbsolutePath();
        mFileName += "/temp.3gp";
    }

    public RecordingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setText("Start recording");
        setOnClickListener(clicker);
        mFileName =  context.getExternalCacheDir().getAbsolutePath();
        mFileName += "/temp.3gp";
    }

    public void registerMediaRecorder(Fragment context){
        if (context instanceof OnRecordingListener) {
            mListener = (OnRecordingListener) context;
            LOG("Resgistered!");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecordingInterface");
        }
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
    }

    private void stopRecording() {
        mRecorder.stop();
        LOG("stopRecording()");
        mRecorder.release();
        mRecorder = null;
        File file = new File(mFileName);
        if(mListener != null && file.exists()) {
            mListener.stopRecording(new File(mFileName));
        }
    }

    public interface OnRecordingListener{
        void stopRecording(File record);
    }

    public void stopListening(){
        assert mListener != null;
        mListener = null;
    }

}
