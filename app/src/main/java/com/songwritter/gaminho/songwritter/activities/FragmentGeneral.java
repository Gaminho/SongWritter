package com.songwritter.gaminho.songwritter.activities;

import android.app.DownloadManager;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.songwritter.gaminho.songwritter.R;

import java.io.File;
import java.io.IOException;

import static android.content.ContentValues.TAG;


public class FragmentGeneral extends Fragment {

    // BUNDLE
    public static final String INDEX = "pupil_id";

    int mIndex;
    String url ="http:////www.youtubeinmp3.com/fetch/?video=https://www.youtube.com/watch?v=mm8S1lwzrGA";
    private StorageReference mStorageRef;


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

        mStorageRef = FirebaseStorage.getInstance().getReference();

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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

//                Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
                Uri file = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getResources().getResourcePackageName(R.drawable.img_music)
                        + '/' + getResources().getResourceTypeName(R.drawable.img_music) + '/' + getResources().getResourceEntryName(R.drawable.img_music) );
                StorageReference riversRef = mStorageRef.child("images/music.jpg");
                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Log.e("success", downloadUrl.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.e("failure", "Exception: " + exception);
                            }
                        });
            }
        });

        view.findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent i = new Intent();
                i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
                startActivity(i);*/

                File localFile = null;
                try {
                    localFile = File.createTempFile("images", "jpg");
                } catch (IOException e) {
                    Log.e("failure", "Exception: " + e);
                }
                StorageReference riversRef = mStorageRef.child("images/music.jpg");
                riversRef.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.e("success", "ok");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("failure", "Exception: " + exception);
                    }
                });


            }
        });


        view.findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "signin");

               /* mAuth.signInWithEmailAndPassword("goldenglawi@gmail.com", "Rhomer91")
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.e(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.e(TAG, "signInWithEmail:failed", task.getException());
                                }

                            }
                        });*/
            }
        });

        view.findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "create");

//                mAuth.createUserWithEmailAndPassword("goldenglawi@gmail.com", "Rhomer91")
//                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                Log.e(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
//
//                                // If sign in fails, display a message to the user. If sign in succeeds
//                                // the auth state listener will be notified and logic to handle the
//                                // signed in user can be handled in the listener.
//                                if (!task.isSuccessful()) {
//                                    Log.e(TAG, "fail creation");
//                                }
//                            }
//                        });
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
