package com.songwritter.gaminho.songwritter;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Locale;

public class Database {

    public enum Table {
        MESSAGES, LYRICS, USER
    }
    private static final String DB_TABLE_MESSAGES = "messages";
    private static final String DB_TABLE_LYRICS = "songs";
    private static final String DB_TABLE_USERS = "users";

    private static final String ROOT_TO_USER_DATA = "%s/%s";

    public static DatabaseReference getTable(FirebaseUser user, Table dataTable){
        String table = null;
        switch(dataTable){
            case LYRICS:
                table= DB_TABLE_LYRICS;
                break;
            case MESSAGES:
                table = DB_TABLE_MESSAGES;
                break;
            case USER:
                table = DB_TABLE_USERS;
                break;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String reference = String.format(Locale.FRANCE, ROOT_TO_USER_DATA, user.getDisplayName(), table);
        return database.getReference(reference);
    }

    public enum StorageFolder {
        IMAGE, SONG
    }
    public static final String STORAGE_IMAGE = "images";
    public static final String STORAGE_USERS = "users";
    public static final String STORAGE_SONG = "songs";

    private static StorageReference getStorage(StorageFolder storageFolder){
        String table = null;
        switch(storageFolder){
            case IMAGE:
                table= STORAGE_IMAGE;
                break;
            case SONG:
                table = STORAGE_SONG;
                break;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageRef.child(table);
        return ref;
    }

    public static StorageReference getUserStorage(FirebaseUser user){
        return FirebaseStorage.getInstance().getReference().child(user.getUid());
    }

    public static boolean saveUserProfileImg(FirebaseUser user, File img){
        StorageReference storageRef = getUserStorage(user).child(STORAGE_IMAGE);
        final boolean[] success = new boolean[1];
        storageRef.putFile(Uri.fromFile(img))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.e("IMG", "Upload success");
                        success[0] = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("IMG", "Upload failure: " + exception.getMessage());
                        success[0] = false;
                    }
                });
        return success[0];
    }

}
