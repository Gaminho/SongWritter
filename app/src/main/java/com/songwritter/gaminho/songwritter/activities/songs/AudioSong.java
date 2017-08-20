package com.songwritter.gaminho.songwritter.activities.songs;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.Utils;
import com.songwritter.gaminho.songwritter.adapters.InstrumentalAdapter;
import com.songwritter.gaminho.songwritter.beans.Instrumental;
import com.songwritter.gaminho.songwritter.customviews.CustomAlertDialogBuilder;
import com.songwritter.gaminho.songwritter.interfaces.SongInteractionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.songwritter.gaminho.songwritter.Utils.LOG;


public class AudioSong extends Fragment {

    // Intent Result
    private final static int GET_AUDIO = 99;

    private ListView mLVBeats;

    @Nullable
    private
    SongInteractionListener mListener;

    private List<Instrumental> mListBeats;

    @NonNull
    public static AudioSong newInstance() {
        return new AudioSong();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Instrumental> beats = mListener.getSongLyrics().getBeats();
        mListBeats = beats != null ? beats : new ArrayList<Instrumental>();
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.song_audio, container, false);

        final TextView tvLyrics = (TextView) view.findViewById(R.id.no_beat);
        mLVBeats = (ListView) view.findViewById(R.id.beats_list);

        if (!mListBeats.isEmpty()) {
            tvLyrics.setVisibility(View.GONE);
            mLVBeats = setUpListView(mLVBeats);
        } else {
            tvLyrics.setVisibility(View.VISIBLE);
            mLVBeats.setVisibility(View.GONE);
            tvLyrics.setText(getString(R.string.no_beats));
        }

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            addBeatDialog().show();
        }

        return super.onOptionsItemSelected(item);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_AUDIO) {

            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                File file = Utils.retrieveFileFromUri(uri, getActivity());

                if(file == null){
                    Intent i = new Intent();
                    i.setType("audio/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(i, GET_AUDIO);
                }
                else{
                    if (file.exists()){
                        fillInfoBeatDialog(file).show();
                    }
                }
            }
        }
    }

    // Set up view
    private ListView setUpListView(ListView listView){
        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(new InstrumentalAdapter(getActivity(), mListBeats));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if(new File(mListBeats.get(position).getPath()).exists()) {
                        if(getActivity() instanceof ActivitySong) {
                            ActivitySong activity = (ActivitySong) getActivity();
                            if (activity.getCurrentPosition() != position) {
                                activity.playSongAtPosition(position);
                            } else {
                                if (!activity.isPlaying()) {
                                    if (activity.inPause())
                                        activity.resumeSong();
                                    else
                                        activity.playSongAtPosition(position);
                                } else {
                                    activity.pauseSongAtPosition(position);
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(getActivity(), getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        return listView;
    }


    //Dialog

    private AlertDialog.Builder addBeatDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getString(R.string.btn_add_beat));

        alert.setPositiveButton(getString(R.string.from_youtube), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), getString(R.string.from_youtube),Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNeutralButton(getString(R.string.from_local_storage), new DialogInterface.OnClickListener() { // define the 'Cancel' button
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent();
                i.setType("audio/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, GET_AUDIO);
            }
        });

        return alert;
    }

    private CustomAlertDialogBuilder fillInfoBeatDialog(final File file){
        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(getActivity());
        builder.setTitle(getString(R.string.dialog_title_save_beat));

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_info_beat, null, false);
        builder.setView(view);

        final RadioGroup rgTypeBeat = (RadioGroup) view.findViewById(R.id.rdgroup);

        final EditText etBeatName = (EditText) view.findViewById(R.id.beat_title);
        etBeatName.setText(file.getName());

        final EditText etBeatAuthor = (EditText) view.findViewById(R.id.beat_author);

        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Instrumental.Type typeBeat = null;
                if (rgTypeBeat.getCheckedRadioButtonId() == R.id.rb_face_a) {
                    typeBeat = Instrumental.Type.FACE_A;
                }
                else if (rgTypeBeat.getCheckedRadioButtonId() == R.id.rb_face_b){
                    typeBeat = Instrumental.Type.FACE_B;
                }

                Instrumental instrumental = new Instrumental(etBeatName.getText().toString(),
                        file.getPath(), typeBeat, etBeatAuthor.getText().toString());
                if(isInstrumentalValid(instrumental)) {
                    mListener.addABeat(instrumental);
                    dialog.dismiss();
                }
            }
        });

        builder.setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder;
    }

    private boolean isInstrumentalValid(Instrumental instrumental){
        if(instrumental.getPath() == null || instrumental.getPath().isEmpty()){
            Toast.makeText(getActivity(), getString(R.string.null_path), Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(instrumental.getTitle() == null || instrumental.getTitle().isEmpty()){
            Toast.makeText(getActivity(), getString(R.string.null_title), Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(instrumental.getAuthor() == null || instrumental.getAuthor().isEmpty()){
            Toast.makeText(getActivity(), getString(R.string.null_author), Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(instrumental.getType() == null){
            Toast.makeText(getActivity(), getString(R.string.null_type), Toast.LENGTH_SHORT).show();
            return false;
        }

        else
            return true;
    }

    public void updatePauseUI(int mediaPosition){
        assert  mLVBeats != null;
        ImageView imV = (ImageView) mLVBeats.getChildAt(mediaPosition).findViewById(R.id.play_pause_stop);
        Utils.setImageView(R.drawable.ic_play_arrow_white_18dp, imV);
    }

    public void updatePlayUI(int mediaPosition){
        assert  mLVBeats != null;
        ImageView imV;
        int resId;
        LOG("Child: " + mLVBeats.getChildCount());
        for(int i = 0 ; i < mLVBeats.getChildCount() ; i++){
            imV = (ImageView) mLVBeats.getChildAt(i).findViewById(R.id.play_pause_stop);
            if(i != mediaPosition){
                resId = R.drawable.ic_play_arrow_white_18dp;
            }
            else{
                resId = R.drawable.ic_pause_white_18dp;
                LOG("That's the position!");
            }
            Utils.setImageView(resId, imV);
        }
    }
}