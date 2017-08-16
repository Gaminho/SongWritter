package com.songwritter.gaminho.songwritter.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.Utils;

import java.util.Locale;

public class IndexActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FragmentSongs.OnFragmentInteractionListener,
        FragmentProfile.OnProfileInteractionListener,
        FirebaseAuth.AuthStateListener {

    //Sections
    private static final int SECTION_GNRL = 0;
    private static final int SECTION_PROFILE = 1;
    private static final int SECTION_LYRICS = 2;
    private static final int SECTION_PROJECTS = 3;
    private static final int SECTION_SETTINGS = 4;
    private static final int SECTION_CONTACTS = 5;
    private static final int SECTION_MSG = 6;

    //Views
    private NavigationView navigationView;

    //Authentication
    private FirebaseAuth mAuth;

    //Preferences
    private SharedPreferences mSharedPreferences;

    // Instance
    private int mCurrentSection;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        mSharedPreferences = getSharedPreferences(Utils.DEFAULT_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadSectionView(mCurrentSection);
        mAuth.addAuthStateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Section", "Current: " +  mCurrentSection);
        loadSectionView(mCurrentSection);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nav_profile:
                id = SECTION_PROFILE;
                break;
            case R.id.nav_grl:
                id = SECTION_GNRL;
                break;
            case R.id.nav_text:
                id = SECTION_LYRICS;
                break;
            case R.id.nav_project:
                id = SECTION_PROJECTS;
                break;
            case R.id.nav_settings:
                id = SECTION_SETTINGS;
                break;
            case R.id.nav_friends:
                id = SECTION_CONTACTS;
                break;
            case R.id.nav_msg:
                id = SECTION_MSG;
                break;
        }

        loadSectionView(id);


        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        else if(id == R.id.action_add_lyrics){
            startActivity(new Intent(this, SongActivity.class).setAction(Utils.ACTION_CREATE));
        }

        return super.onOptionsItemSelected(item);
    }


    // Managing UI

    private void loadSectionView(int position) {

        Fragment fragment = null;
        String label = "";

        switch (position) {
            case SECTION_GNRL:
                fragment = FragmentGeneral.newInstance(1);
                label = getString(R.string.nav_grl);
                break;
            case SECTION_LYRICS:
                fragment = FragmentSongs.newInstance();
                label = getString(R.string.nav_texts);
                break;
            case SECTION_PROFILE:
                fragment = FragmentProfile.newInstance();
                label = getString(R.string.nav_profile);
                break;
            case SECTION_PROJECTS:
                label = getString(R.string.nav_projects);
                break;
            case SECTION_SETTINGS:
                label = getString(R.string.nav_settings);
                break;
            case SECTION_CONTACTS:
                label = getString(R.string.nav_friends);
                break;
            case SECTION_MSG:
                label = getString(R.string.nav_msg);
                break;
        }

        if(position != SECTION_LYRICS && position != SECTION_GNRL && position != SECTION_PROFILE) {
            Toast.makeText(getApplicationContext(), "'" + label + "' n'est pas disponible !", Toast.LENGTH_SHORT).show();
            mCurrentSection = SECTION_GNRL;
        }
        else {

            FragmentManager fragmentManager = getFragmentManager();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .commit();

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(label);
            setSupportActionBar(toolbar);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            mCurrentSection = position;
        }
    }

    private void updateHeaderView(final boolean connected) {
        ImageView signPix = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.sign_pix);
        ImageView usrPix = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_pix);
        TextView headerMsg = (TextView) navigationView.getHeaderView(0).findViewById(R.id.connectedAs);
        int cardViewColor;
        Drawable cardViewPix;
        usrPix.setImageDrawable(getDrawable(R.drawable.android));

        if (connected) {
            FirebaseUser user = mAuth.getCurrentUser();
            cardViewColor = getColor(R.color.red500);
            cardViewPix = getDrawable(R.drawable.signout);
            //TODO: update img view
            if (user.getDisplayName() != null)
                headerMsg.setText(String.format(Locale.FRANCE, getString(R.string.state_connected_as), user.getDisplayName()));
            else
                headerMsg.setText(getString(R.string.state_connected));

        } else {
            headerMsg.setText(getString(R.string.state_not_connected));
            cardViewColor = getColor(R.color.green500);
            cardViewPix = getDrawable(R.drawable.signin);
        }

        // Handle Menu Sections
        for (int i = SECTION_PROFILE; i < navigationView.getMenu().size(); i++){
            navigationView.getMenu().getItem(i).setVisible(connected);
        }

        // CardView
        ((CardView) navigationView.getHeaderView(0).findViewById(R.id.sign_out)).setCardBackgroundColor(cardViewColor);
        signPix.setImageDrawable(cardViewPix);

        navigationView.getHeaderView(0).findViewById(R.id.sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //User is connected
                if (connected) {
                    mAuth.signOut();
                }

                // User is not connected
                else {

                    final AlertDialog dialog = connectionDialog("Authentication").show();

                    dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    dialog.findViewById(R.id.create_account).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();

                            final AlertDialog dialog = creationDialog("Sign in", "2").show();

                            dialog.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final String userMail = ((EditText) dialog.findViewById(R.id.user_mail)).getText().toString().trim();
                                    final String userPass = ((EditText) dialog.findViewById(R.id.user_pass)).getText().toString();
                                    handleFirebaseAction(Utils.FirebaseAction.SIGN_IN, dialog, userMail, userPass);
                                }
                            });

                            dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });

                    dialog.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String userMail = ((EditText) dialog.findViewById(R.id.user_mail)).getText().toString().trim();
                            final String userPass = ((EditText) dialog.findViewById(R.id.user_pass)).getText().toString();
                            handleFirebaseAction(Utils.FirebaseAction.LOG_IN, dialog, userMail, userPass);
                        }
                    });
                }
            }
        });
    }

    // Interface

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.e("ntd", "Nothing to do!");
    }

    @Override
    public FirebaseUser getUser() {
        if (mAuth == null)
            return null;
        else
            return mAuth.getCurrentUser();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            updateHeaderView(true);
            //                   updateHeaderView(R.layout.drawer_header, "Connecté en tant que " + user.getDisplayName());

//                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                            .setDisplayName("Gaminho")
//                            .setPhotoUri(Uri.parse("findicons.com/files/icons/1072/face_avatars/300/a04.png"))
//                            .build();
//
//                    user.updateProfile(profileUpdates)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Log.e("Update", "User profile updated.");
//                                    }
//                                }
//                            });



        } else {
            updateHeaderView(false);
//                    updateHeaderView(R.layout.drawer_header, "Non connecté");
        }
    }

    // Dialog

    private AlertDialog.Builder connectionDialog(String title){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_connection, null, false);
        alert.setView(view);

        final EditText etUsrMail = (EditText) view.findViewById(R.id.user_mail);
        final EditText etUsrPass = (EditText) view.findViewById(R.id.user_pass);
        final Button bLogIn = (Button) view.findViewById(R.id.login);

        TextWatcher etWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etUsrMail.getText().length() == 0 || etUsrPass.getText().length() == 0){
                    bLogIn.setEnabled(false);
                }
                else{
                    bLogIn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };

        etUsrMail.addTextChangedListener(etWatcher);
        etUsrPass.addTextChangedListener(etWatcher);

        String prefMail = mSharedPreferences.getString(Utils.PREF_USER_MAIL, "");

        if(!prefMail.isEmpty()) {
            etUsrMail.setText(prefMail);
            bLogIn.setEnabled(true);
        }

        String prefPass = mSharedPreferences.getString(Utils.PREF_USER_PASS, "");

        if(!prefPass.isEmpty()) {
            etUsrPass.setText(prefPass);
            bLogIn.setEnabled(true);
        }

        return alert;
    }

    private AlertDialog.Builder creationDialog(String title, String mod){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_connection, null, false);
        alert.setView(view);

        final EditText etUsrMail = (EditText) view.findViewById(R.id.user_mail);
        final EditText etUsrPass = (EditText) view.findViewById(R.id.user_pass);
        final Button bLogIn = (Button) view.findViewById(R.id.login);

        TextWatcher etWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etUsrMail.getText().length() == 0 || etUsrPass.getText().length() == 0){
                    bLogIn.setEnabled(false);
                }
                else{
                    bLogIn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };

        etUsrMail.addTextChangedListener(etWatcher);
        etUsrPass.addTextChangedListener(etWatcher);

        if(mod.equals("0")) {
            view.findViewById(R.id.create_account).setVisibility(View.VISIBLE);

            String prefMail = mSharedPreferences.getString(Utils.PREF_USER_MAIL, "");
            bLogIn.setText(getText(R.string.btn_log_in));
            if (!prefMail.isEmpty()) {
                etUsrMail.setText(prefMail);
                bLogIn.setEnabled(true);
            }

            String prefPass = mSharedPreferences.getString(Utils.PREF_USER_PASS, "");

            if (!prefPass.isEmpty()) {
                etUsrPass.setText(prefPass);
                bLogIn.setEnabled(true);
            }
        }
        else{
            view.findViewById(R.id.create_account).setVisibility(View.GONE);
            bLogIn.setText(getText(R.string.btn_sign_in));
        }

        return alert;
    }

    // Utils

    private void handleFirebaseAction(Utils.FirebaseAction action, final AlertDialog dialog, final String userMail, final String userPass){

        switch(action){
            case LOG_IN:
                handleProgress(dialog, View.VISIBLE);
                mAuth.signInWithEmailAndPassword(userMail, userPass)
                        .addOnCompleteListener(IndexActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    saveUserCredentials(dialog,userMail,userPass);
                                    dialog.dismiss();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case SIGN_IN:
                handleProgress(dialog, View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(userMail, userPass)
                        .addOnCompleteListener(IndexActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    saveUserCredentials(dialog,userMail,userPass);
                                    dialog.dismiss();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                handleProgress(dialog, View.INVISIBLE);
                                updateHeaderView(task.isSuccessful());
                            }
                        });
                break;
        }
    }

    private void handleProgress(AlertDialog dialog, int visibility){
        dialog.findViewById(R.id.pb_connection).setVisibility(visibility);
        dialog.findViewById(R.id.login).setEnabled(visibility != View.VISIBLE);
        dialog.findViewById(R.id.cancel).setEnabled(visibility != View.VISIBLE);
    }

    private void saveUserCredentials(AlertDialog dialog, String userMail, String userPass){
        CheckBox remindUsr = (CheckBox) dialog.findViewById(R.id.user_remind);
        if(remindUsr.isChecked()){
            mSharedPreferences.edit().putString(Utils.PREF_USER_MAIL, userMail).putString(Utils.PREF_USER_PASS, userPass).apply();
        }
    }

}
