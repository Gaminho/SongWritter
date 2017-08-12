package com.songwritter.gaminho.songwritter.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.songwritter.gaminho.songwritter.R;

public class FragmentGeneral extends Fragment {

    // BUNDLE
    public static final String INDEX = "pupil_id";

    int mIndex;

    // Views
    protected Spinner mSpinnerPupil;


    // Fragment life cycle
    public static FragmentGeneral newInstance() {
        return new FragmentGeneral();
    }
    public static FragmentGeneral newInstance(int id) {
        FragmentGeneral fragmentSuivi = new FragmentGeneral();
        Bundle args = new Bundle();
        args.putInt(INDEX, id);
        fragmentSuivi.setArguments(args);
        return fragmentSuivi;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().getInt(INDEX,-1) != -1)
            mIndex = getArguments().getInt(INDEX, -1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gnrl, container, false);
        ((TextView)view.findViewById(R.id.titelelele)).setText("En cours : " + mIndex);
        return view;
    }


    // Utils

    public void getAllViews(View v){
    }

    public void fillViews(){

    }

}
