package com.songwritter.gaminho.songwritter.interfaces;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public interface UserInteractionListener {
    FirebaseUser getUser();
    Uri getUserImg();
    void updateProfilePicture(File profilePicture);
    void updatePhotoUri(Uri photoUri);
}
