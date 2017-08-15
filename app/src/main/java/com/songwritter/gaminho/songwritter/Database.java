package com.songwritter.gaminho.songwritter;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class Database {

    public enum Table {
        MESSAGES, LYRICS, USER
    }
    public static final String DB_TABLE_MESSAGES = "messages";
    public static final String DB_TABLE_LYRICS = "songs";
    public static final String DB_TABLE_USERS = "users";

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
}
