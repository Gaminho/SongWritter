package com.songwritter.gaminho.songwritter;

import java.util.Calendar;
import java.util.Locale;

public class Utils {

    public static String URL_YOUTUBE_MP3 = "http://www.youtubeinmp3.com"; // /download/get/?i=kG9rhGAdR3OxG9Sz3Y8aBmJ1CqSc0gk6&e=73&v=mm8S1lwzrGA"


    public static final String ACTION_VIEW = "view";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_CREATE = "create";

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

}
