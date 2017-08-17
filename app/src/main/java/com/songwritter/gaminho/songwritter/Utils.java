package com.songwritter.gaminho.songwritter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class Utils {

    public static String URL_YOUTUBE_MP3 = "http://www.youtubeinmp3.com"; // /download/get/?i=kG9rhGAdR3OxG9Sz3Y8aBmJ1CqSc0gk6&e=73&v=mm8S1lwzrGA"

    /**
     * ACTIVITY RESULTS CODES
     */
    private static String TAG = "SongWritter";
    public static void LOG(String message){
        Log.e(TAG, message);
    }

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_PIC_CROP = 2;

    //SharedPreferences
    public static final String DEFAULT_PREFERENCES = "SongWritter";
    public static final String PREF_USER_MAIL = "user_mail";
    public static final String PREF_USER_PASS = "user_pass";
    public static final String PREF_USER_PROFILE_URI = "user_photo_uri";

    public static final String ACTION_VIEW = "view";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_CREATE = "create";

    public enum FirebaseAction {
        SIGN_IN, LOG_IN
    }

    public enum DateFormat {
        FULL, DAY, HOUR
    }

    public static String formatTS(long timestamp, DateFormat format){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        String day = String.format(Locale.FRANCE, "%02d/%02d/%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        String hour = String.format(Locale.FRANCE, "%02dh%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        switch(format){
            case DAY:
                return day;
            case HOUR:
                return hour;
            case FULL:
                return String.format(Locale.FRANCE, "%s %s", day, hour);
            default:
                return null;
        }
    }

    // Image
    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

        canvas.drawRoundRect(rectF, widthLight / 2 ,heightLight / 2,paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);

        return output;
    }

    public static File convertBitmapToFile(Bitmap bitmap, String filename, Context context){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        File f = new File(context.getCacheDir(), filename);

        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return f;
        } catch (IOException e) {
            Log.e("Utils", "convertBitmapToFile exception: " + e);
        }

        return null;
    }

}
