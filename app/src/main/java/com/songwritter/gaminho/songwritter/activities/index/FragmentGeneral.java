package com.songwritter.gaminho.songwritter.activities.index;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songwritter.gaminho.songwritter.R;


public class FragmentGeneral extends Fragment {

    // BUNDLE
    private static final String INDEX = "pupil_id";

    private int mIndex;
    //String url ="http:////www.youtubeinmp3.com/fetch/?video=https://www.youtube.com/watch?v=mm8S1lwzrGA";


    // Fragment life cycle
    public static FragmentGeneral newInstance(int id) {
        FragmentGeneral fragmentGeneral = new FragmentGeneral();
        Bundle args = new Bundle();
        args.putInt(INDEX, id);
        fragmentGeneral.setArguments(args);
        return fragmentGeneral;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().getInt(INDEX,-1) != -1)
            mIndex = getArguments().getInt(INDEX, -1);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gnrl, container, false);
        ((TextView) view.findViewById(R.id.titelelele)).setText("En cours : " + mIndex);

        ((TextView) view.findViewById(R.id.top4Lyrics).findViewById(R.id.top1)).setText("1. Deus Ex Gaminho");
        ((TextView) view.findViewById(R.id.top4Lyrics).findViewById(R.id.top2)).setText("2. Vox Agora");
        ((TextView) view.findViewById(R.id.top4Lyrics).findViewById(R.id.top3)).setText("3. Loin");
        ((TextView) view.findViewById(R.id.top4Lyrics).findViewById(R.id.top4)).setText("4. Thomas Hobbes");

        ((TextView) view.findViewById(R.id.top4Projects).findViewById(R.id.top1)).setText("1. POB 2");
        ((TextView) view.findViewById(R.id.top4Projects).findViewById(R.id.top2)).setText("2. A2Z");
        ((TextView) view.findViewById(R.id.top4Projects).findViewById(R.id.top3)).setText("3.");
        ((TextView) view.findViewById(R.id.top4Projects).findViewById(R.id.top4)).setText("4.");

        view.findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "download");
            }
        });

        view.findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "show");
            }
        });

        view.findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "signin");
            }
        });

        view.findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "create");
            }
        });

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.index, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
