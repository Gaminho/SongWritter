package com.songwritter.gaminho.songwritter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        FULL, DAY, HOUR, FILE_TS
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
            case FILE_TS:
                return String.format(Locale.FRANCE, "%s_%s", day, hour).replace("/", "_").replace("h","_").replace(":","_");
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

    public static File retrieveFileFromUri(Uri uri, Context context){
        String filename;
        String mimeType = context.getContentResolver().getType(uri);
        File file;
        if (mimeType == null) {
            return null;
        }
        else {
            Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            filename = returnCursor.getString(nameIndex);

            String sourcePath = context.getExternalFilesDir(null).toString();
            file = new File(sourcePath + "/beats/");

            LOG("Exist: " + file.exists());
            file = new File(file, filename);

            return copyFile(uri, file, context) ? file : null;

        }
    }

    public static void setImageView(int resId, ImageView imageView){
        Bitmap bitmap = BitmapFactory.decodeResource(imageView.getContext().getResources(), resId);
        int height = (bitmap.getHeight() * 512 / bitmap.getWidth());
        Bitmap scale = Bitmap.createScaledBitmap(bitmap, 512, height, true);
        imageView.setImageBitmap(scale);
    }

    private static boolean copyFile(Uri uri, File dest, Context context){

        if(!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
            return false;
        }

        LOG(dest.getAbsolutePath());

        try (InputStream is = context.getContentResolver().openInputStream(uri); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            is.close();
            os.close();
            return true;
        } catch (Exception e) {
            LOG("Exception (retrieveFileFromUri): " + e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    private static boolean copyFile(File src, File dest, Context context){
        return copyFile(Uri.fromFile(src), dest, context);
    }

    public static boolean moveFile(File src, File dest, Context context){
        return copyFile(src, dest, context) && src.delete();
    }

    // Format
    public static String milliSecondsToTimer(long milliseconds){

        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);

        return hours > 0 ? String.format(Locale.FRANCE, "%d:%02d:%02d", hours, minutes, seconds)
                : String.format(Locale.FRANCE, "%d:%02d", minutes, seconds);
    }

    public static int getProgressPercentage(long currentDuration, long totalDuration){
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        Double percentage =(((double)currentSeconds)/totalSeconds)*100;
        return percentage.intValue();
    }

}