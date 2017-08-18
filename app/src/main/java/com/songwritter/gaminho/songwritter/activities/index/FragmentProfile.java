package com.songwritter.gaminho.songwritter.activities.index;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songwritter.gaminho.songwritter.R;
import com.songwritter.gaminho.songwritter.Utils;
import com.songwritter.gaminho.songwritter.interfaces.UserInteractionListener;
import com.squareup.picasso.Picasso;

import java.io.File;


public class FragmentProfile extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private UserInteractionListener mListener;

    // View
    private ImageView mIVUserAvatar;

    private SharedPreferences mSharedPreferences;

    public FragmentProfile() {
    }

    public static FragmentProfile newInstance() {
        return new FragmentProfile();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getActivity().getSharedPreferences(Utils.DEFAULT_PREFERENCES, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView mTVUserName = (TextView) view.findViewById(R.id.user_name);
        mTVUserName.setText(mListener.getUser().getDisplayName());
        ((TextView) view.findViewById(R.id.user_mail)).setText(mListener.getUser().getEmail());
        mIVUserAvatar = (ImageView) view.findViewById(R.id.user_avatar);
        mIVUserAvatar.setOnClickListener(this);

        Uri photoUrl = mListener.getUserImg() != null ? mListener.getUserImg() : null;
        Picasso.with(getContext()).load(photoUrl).placeholder(R.mipmap.ic_account_circle_white_48dp).into(mIVUserAvatar);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserInteractionListener) {
            mListener = (UserInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UserInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(selectedImage, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, Utils.RESULT_PIC_CROP);

        }

        if (requestCode == Utils.RESULT_PIC_CROP && resultCode == getActivity().RESULT_OK && null != data) {
            Bundle extras = data.getExtras();
            final Bitmap selectedBitmap = extras.getParcelable("data");
            final Bitmap userPix = Utils.getRoundedCroppedBitmap(selectedBitmap);
            String filename = "profile.png";
            File file = Utils.convertBitmapToFile(userPix, filename, getActivity());
            mListener.updateProfilePicture(file);
        }
    }

    // Interface

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.user_avatar:
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Utils.RESULT_LOAD_IMAGE);
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(sharedPreferences == mSharedPreferences){
            if(key.equals(Utils.PREF_USER_PROFILE_URI)){
                String uri = mSharedPreferences.getString(Utils.PREF_USER_PROFILE_URI, null);
                if(mIVUserAvatar != null && getActivity() != null && uri != null) {
                    Picasso.with(getContext()).load(Uri.parse(uri)).placeholder(R.mipmap.ic_account_circle_white_48dp).into(mIVUserAvatar);
                }
            }
        }
    }
}
