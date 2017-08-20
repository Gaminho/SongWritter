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
import java.util.Locale;


public class AudioSong extends Fragment {

    // Intent Result
    private final static int GET_AUDIO = 99;

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

        final TextView tvLyrics = (TextView) view.findViewById(R.id.song_audio);
        ListView mLVBeats = (ListView) view.findViewById(R.id.beats_list);

        if (!mListBeats.isEmpty()) {
            tvLyrics.setText(String.format(Locale.FRANCE, getString(R.string.format_songs_beats_count), mListBeats.size()));
            mLVBeats.setAdapter(new InstrumentalAdapter(getActivity(), mListBeats));
            mLVBeats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if(new File(mListBeats.get(position).getPath()).exists()) {
                        if(getActivity() instanceof ActivitySong)
                            ((ActivitySong) getActivity()).playSongAtPosition(position);
                    }
                    else{
                        Toast.makeText(getActivity(), getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            tvLyrics.setText(getString(R.string.no_beats));
            tvLyrics.setTextColor(getContext().getColor(R.color.red500));
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

        final int FACE_A = 1;

        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Instrumental.Type typeBeat = rgTypeBeat.getCheckedRadioButtonId() == FACE_A ? Instrumental.Type.FACE_A : Instrumental.Type.FACE_B;
                Instrumental instrumental = new Instrumental(file.getPath(), etBeatName.getText().toString(), typeBeat);
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
            Toast.makeText(getActivity(), "Path can not be null!",Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(instrumental.getTitle() == null || instrumental.getTitle().isEmpty()){
            Toast.makeText(getActivity(), "Title can not be null!",Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(instrumental.getType() == null){
            Toast.makeText(getActivity(), "Type can not be null!",Toast.LENGTH_SHORT).show();
            return false;
        }

        else
            return true;
    }

}