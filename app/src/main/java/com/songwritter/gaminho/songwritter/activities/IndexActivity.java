package com.songwritter.gaminho.songwritter.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.songwritter.gaminho.songwritter.R;

public class IndexActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //Views
    private NavigationView navigationView;

    //Authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    updateHeaderView(R.layout.drawer_header_connected, "Connecté en tant que " + user.getDisplayName());

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
                    updateHeaderView(R.layout.drawer_header_not_connected, "Non connecté");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFragment(FragmentGeneral.newInstance(), "Hey oh");
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        String label = "";

        switch (id) {
            case R.id.nav_grl:
                loadFragment(FragmentGeneral.newInstance(1), getString(R.string.nav_grl));
                return true;
            case R.id.nav_text:
                fragment = FragmentGeneral.newInstance(1);
                label = getString(R.string.nav_texts);
                break;
            case R.id.nav_project:
                fragment = FragmentGeneral.newInstance(2);
                label = getString(R.string.nav_projects);
                break;
            case R.id.nav_settings:
                fragment = FragmentGeneral.newInstance(3);
                label = getString(R.string.nav_settings);
                break;
            case R.id.nav_friends:
                fragment = FragmentGeneral.newInstance(4);
                label = getString(R.string.nav_friends);
                break;
            case R.id.nav_msg:
                fragment = FragmentGeneral.newInstance(5);
                label = getString(R.string.nav_msg);
                break;
        }

        Toast.makeText(getApplicationContext(), "'" + label + "' n'est pas disponible !", Toast.LENGTH_SHORT).show();
        return false;
    }


        /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.index, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */


    // Managing UI

    private void loadFragment(Fragment fragment, String label) {

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
    }

    private void updateHeaderView(int layout, String headerText) {
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(layout, null, false);
        ((TextView) view.findViewById(R.id.connectedAs)).setText(headerText);

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            Log.e("USER", user.toString());
            ((ImageView) view.findViewById(R.id.userPix)).setImageURI(user.getPhotoUrl());
            ((TextView) view.findViewById(R.id.connectedAs)).setText("Connecté en tant que " + user.getDisplayName());
        }



        if(layout == R.layout.drawer_header_not_connected) {
            view.findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("Click", "SignIn");
                    mAuth.signInWithEmailAndPassword("goldenglawi@gmail.com", "Rhomer91")
                            .addOnCompleteListener(IndexActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.e("Click", "signInWithEmail:onComplete:" + task.isSuccessful());

                                    if (!task.isSuccessful()) {
                                        Log.e("Click", "signInWithEmail:failed", task.getException());
                                    }

                                }
                            });
                }
            });
        }
        else{
            view.findViewById(R.id.sign_out).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("Click", "SignOut");
                    mAuth.signOut();
                }
            });
        }

        navigationView.addHeaderView(view);
    }

}
