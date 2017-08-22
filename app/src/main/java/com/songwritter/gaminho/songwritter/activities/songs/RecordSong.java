package com.songwritter.gaminho.songwritter.activities.songs;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.Utils;
import com.songwritter.gaminho.songwritter.adapters.RecordAdapter;
import com.songwritter.gaminho.songwritter.beans.MemoRecord;
import com.songwritter.gaminho.songwritter.customviews.CustomAlertDialogBuilder;
import com.songwritter.gaminho.songwritter.customviews.RecordButton;
import com.songwritter.gaminho.songwritter.interfaces.SongInteractionListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.songwritter.gaminho.songwritter.Utils.LOG;

public class RecordSong extends Fragment implements RecordButton.OnRecordingListener {

    @Nullable
    private
    SongInteractionListener mListener;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private ListView mLVRecords;
    private TextView mTVRecordingTimer;
    private TextView mTVNoRecord;

    private RecordButton mRecordButton;

    private List<MemoRecord> mRecordings;
    private RecordAdapter mAdapter;

    private Timer mTimer;
    private final TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            assert mTVRecordingTimer != null;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTVRecordingTimer.setText(Utils.milliSecondsToTimer((System.currentTimeMillis() - recordingStartTS), false));
                }
            });
        }
    };
    private long recordingStartTS;
    private static final long UPDATE_TIMER_DELAY = 10;


    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    public RecordSong() {
    }

    public static RecordSong newInstance() {
        return new RecordSong();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<MemoRecord> recordings = mListener.getSongLyrics().getMemoRecords();
        mRecordings = recordings != null ? recordings : new ArrayList<MemoRecord>();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.song_record, container, false);

        mRecordButton = (RecordButton) view.findViewById(R.id.recorder);
        mRecordButton.registerMediaRecorderListener(this);

        mTVNoRecord = (TextView) view.findViewById(R.id.no_record);

        mTVRecordingTimer = (TextView) view.findViewById(R.id.recording_timer);
        mTVRecordingTimer.setVisibility(View.GONE);

        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        mLVRecords = (ListView) view.findViewById(R.id.records_list);

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(isSelecting()){
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_delete).setTitle(String.format(Locale.FRANCE, "Supprimer (%d)", mAdapter.getSelectedCount()));
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_add).setVisible(false);
        }
        else{
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_add).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_delete)
            removeRecordDialog().show();

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        mRecordButton.registerMediaRecorderListener(this);

        if (!mRecordings.isEmpty()) {
            mTVNoRecord.setVisibility(View.GONE);
            mTVRecordingTimer.setVisibility(View.GONE);
            mLVRecords = setUpListView(mLVRecords);
        } else {
            mTVNoRecord.setVisibility(View.VISIBLE);
            mLVRecords.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mRecordButton.stopListening();
        if(isPlaying()) {
            mAdapter.stopPlaying();
        }
        try{
            mTimer.cancel();
            mTimer.purge();
            LOG("onStop: cancel Timer");
        } catch (Exception e){
            LOG("Timer already cancel!");
        }
        mTimer = null;
        if(mAdapter != null){
            mRecordButton.unregisterMediaRecorderListener();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SongInteractionListener) {
            mListener = (SongInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SongInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted )
            getActivity().finish();
    }


    // Interface

    @Override
    public void stopRecording(File record) {
        addMemoDialog(record).show();
        mTimer.cancel();
        mTimer.purge();
    }

    @Override
    public void startRecording() {
        mTVRecordingTimer.setVisibility(View.VISIBLE);
        recordingStartTS = System.currentTimeMillis();
        mTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                assert mTVRecordingTimer != null;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTVRecordingTimer.setText(Utils.milliSecondsToTimer((System.currentTimeMillis() - recordingStartTS), false));
                    }
                });
            }
        };
        mTimer.schedule(timerTask, 0, UPDATE_TIMER_DELAY);
    }


    // Utils

    public boolean isSelecting(){
        return mAdapter != null && mAdapter.isSelecting();
    }

    public boolean isPlaying(){
        return mAdapter != null && mAdapter.isPlaying();
    }

    private ListView setUpListView(ListView listView){
        mAdapter = new RecordAdapter(getActivity(), R.layout.adapter_beats, mRecordings);
        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                LOG("Click: " + isPlaying());
                if (!isSelecting()) {

                    File file = new File(mRecordings.get(position).getPath());
                    if (file.exists()) {
                        mAdapter.playSongAtPosition(position);
                    }
                    else {
                        Toast.makeText(getActivity(), getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    onListItemSelect(position);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent,
                                           View view, int position, long id) {
                onListItemSelect(position);
                return true;
            }
        });

        return listView;
    }

    private void onListItemSelect(int position) {
        mAdapter.toggleSelection(position);
        mAdapter.setSelectionMod(mAdapter.getSelectedCount() > 0);
        getActivity().invalidateOptionsMenu();
    }

    // Dialog

    private CustomAlertDialogBuilder addMemoDialog(final File outputRecord){
        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(getActivity());
        builder.setTitle(getString(R.string.dialog_save_record));

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_save_record, null, false);
        builder.setView(view);

        final EditText etBeatName = (EditText) view.findViewById(R.id.record_title);
        etBeatName.setText(String.format("%s_%s", "Record", Utils.formatTS(System.currentTimeMillis(), Utils.DateFormat.FILE_TS)));

        MediaPlayer mediaPlayer = new MediaPlayer();
        long duration = 0;
        try {
            mediaPlayer.setDataSource(outputRecord.getPath());
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final long finalDuration = duration;
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                MemoRecord memoRecord = new MemoRecord(outputRecord.getAbsolutePath(),
                        etBeatName.getText().toString(), mListener.getSongLyrics().getAuthor(),
                        System.currentTimeMillis(), finalDuration);

                try{
                    memoRecord.isValid();
                    mListener.addARecord(memoRecord);
                    dialog.dismiss();
                    mTVRecordingTimer.setVisibility(View.GONE);
                } catch(Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() { // define the 'Cancel' button
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder;
    }

    private AlertDialog.Builder removeRecordDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getString(R.string.dialog_remove_record));

        alert.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.removeRecords(getSelectedRecords());
                mAdapter.removeSelection();
                mAdapter.setSelectionMod(false);
                getActivity().invalidateOptionsMenu();
                onResume();
            }
        });

        alert.setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() { // define the 'Cancel' button
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alert;
    }

    public List<MemoRecord> getSelectedRecords(){
        List<MemoRecord> list = new ArrayList<>();
        SparseBooleanArray sparseBooleanArray = mAdapter.getSelectedIds();
        for (int i = (sparseBooleanArray.size() - 1); i >= 0; i--) {
            if (sparseBooleanArray.valueAt(i)) {
                MemoRecord selectedItem = mAdapter.getItem(sparseBooleanArray.keyAt(i));
                mAdapter.remove(selectedItem);
                list.add(selectedItem);
            }
        }
        return list;
    }
}