package com.songwritter.gaminho.songwritter.beans;


import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

public class User {

    String id;
    String displayName;
    Uri photoUrl;
    String mail;

    public User(FirebaseUser user) {
        this.id = user.getUid();
        this.displayName = user.getDisplayName();
        this.photoUrl = user.getPhotoUrl();
        this.mail = user.getEmail();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
