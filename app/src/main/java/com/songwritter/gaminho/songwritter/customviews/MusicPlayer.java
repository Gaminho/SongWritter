package com.songwritter.gaminho.songwritter.customviews;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.Utils;
import com.songwritter.gaminho.songwritter.beans.Instrumental;
import com.songwritter.gaminho.songwritter.interfaces.MediaInteractionListener;

import java.io.IOException;
import java.util.List;

import static com.songwritter.gaminho.songwritter.Utils.LOG;

public class MusicPlayer extends LinearLayout implements
        MediaInteractionListener, View.OnTouchListener {

    //TODO: Handle repeat
    //TODO: Handle random
    //TODO: Animations

    /*
    // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((mListBeats.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else{
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (mListBeats.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
     */

    private View mRootView;

    private TextView mTVMediaTitle;

    private TextView mTVMediaAuthor;

    private TextView mTVMediaDuration;

    private TextView mTVCurrentPosition;

    private ImageView mIVPlayPause;

    private SeekBar mProgress;

    private MediaPlayer mPlayer;

    private final Handler mHandler = new Handler();

    private List<Instrumental> mMedias;

    private int currentMediaPosition;

    public MusicPlayer(Context context) {
        super(context);
        init(context);
    }

    public MusicPlayer(Context context, AttributeSet attrs) /**/{
        super(context, attrs);

        //TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyActionBar, 0, 0);
        init(context);
    }

    private void init(Context context) {
        LOG("Init MusicPlayer");
        mRootView = inflate(context, R.layout.media_player, this);

        mTVMediaTitle = ((TextView) getRootView().findViewById(R.id.media_title));
        mTVMediaAuthor = ((TextView) getRootView().findViewById(R.id.media_author));
        mTVMediaDuration = ((TextView) getRootView().findViewById(R.id.media_total_duration));
        mTVCurrentPosition = ((TextView) getRootView().findViewById(R.id.media_current_duration));

        mIVPlayPause = ((ImageView) getRootView().findViewById(R.id.btn_play));
        mIVPlayPause.setOnTouchListener(this);

        mProgress = ((SeekBar) getRootView().findViewById(R.id.songProgressBar));
        mProgress.setOnSeekBarChangeListener(this);
        mProgress.setPadding(0,0,0,0);

        getRootView().findViewById(R.id.btn_next).setOnTouchListener(this);
        getRootView().findViewById(R.id.btn_previous).setOnTouchListener(this);

        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
    }

    public void setPlaylist(List<Instrumental> medias){
        this.mMedias = medias;
    }

    private int getPlaylistSize(){
        return this.mMedias.size();
    }

    public int getCurrentMediaPosition(){
        return currentMediaPosition;
    }

    public boolean isPlaying(){
        return this.mPlayer != null && this.mPlayer.isPlaying();
    }

    public void playMedia(int mediaPosition){
        if(mMedias!= null && !mMedias.isEmpty()) {
            if (mediaPosition < getPlaylistSize() && mediaPosition >= 0) {
                currentMediaPosition = mediaPosition;
                play(mMedias.get(mediaPosition));
            }
        }
        else{
            Toast.makeText(getContext(), getContext().getString(R.string.no_playlist), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void play(Instrumental beat) {

        mPlayer.reset();
        try {
            mPlayer.setDataSource(beat.getPath());
            mPlayer.prepare();
        } catch (IOException e) {
            Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
            return;
        }

        int mediaDuration = mPlayer.getDuration();

        if(mRootView.getVisibility() != View.VISIBLE)
            mRootView.setVisibility(View.VISIBLE);

        mTVMediaTitle.setText(beat.getTitle());
        mTVMediaAuthor.setText(getContext().getString(R.string.example_beat_author));
        Utils.setImageView(R.drawable.ic_pause_white_18dp, mIVPlayPause);

        mProgress.setProgress(0);
        mProgress.setMax(mediaDuration);

        mTVMediaDuration.setText(Utils.milliSecondsToTimer(mediaDuration));

        mPlayer.start();

        updateProgressBar();
    }

    @Override
    public void pause() {
        mPlayer.pause();
        Utils.setImageView(R.drawable.ic_play_arrow_white_18dp, mIVPlayPause);
    }

    @Override
    public void resume() {
        mPlayer.start();
        Utils.setImageView(R.drawable.ic_pause_white_18dp, mIVPlayPause);
    }

    @Override
    public void stop() {
        mPlayer.stop();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void next() {
        if(currentMediaPosition < (mMedias.size() - 1)){
            playMedia(currentMediaPosition + 1);
        }else{
            playMedia(0);
        }
    }

    @Override
    public void previous() {
        if(currentMediaPosition > 0){
            playMedia(currentMediaPosition - 1);
        } else {
            playMedia(mMedias.size() - 1);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        Utils.setImageView(R.drawable.ic_play_arrow_white_18dp, mIVPlayPause);
        mProgress.setProgress(0);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.post(mUpdateTimeTask);
        mPlayer.seekTo(seekBar.getProgress());

    }

    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 1000);
    }

    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            mTVCurrentPosition.setText(Utils.milliSecondsToTimer(mPlayer.getCurrentPosition()));
            mProgress.setProgress(mPlayer.getCurrentPosition());
            mHandler.postDelayed(this, 200);
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(null == mPlayer)
            mPlayer = new MediaPlayer();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ((ImageView) view).setColorFilter(getContext().getColor(R.color.colorAccent));
                view.invalidate();
                break;

            case MotionEvent.ACTION_UP:

                ((ImageView) view).setColorFilter(getContext().getColor(R.color.white0));
                view.invalidate();

                if (view.getId() == R.id.btn_play) {
                    if (mPlayer.isPlaying()){
                        Utils.setImageView(R.drawable.ic_pause_white_18dp, mIVPlayPause);
                        this.pause();
                    }
                    else {
                        Utils.setImageView(R.drawable.ic_play_arrow_white_18dp, mIVPlayPause);
                        this.resume();
                    }
                }

                else if (view.getId() == R.id.btn_next){
                    this.next();
                }

                else if (view.getId() == R.id.btn_previous){
                    this.previous();
                }

                break;
        }

        return true;
    }
}
