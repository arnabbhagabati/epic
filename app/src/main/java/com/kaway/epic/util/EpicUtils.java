package com.kaway.epic.util;

import static com.kaway.epic.EpicConstants.DEFAULT_VID_ID_SET;
import static com.kaway.epic.EpicConstants.DEFAULT_VID_ID_SET_KEY;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.kaway.epic.EpicConstants;
import com.kaway.epic.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class EpicUtils {

    public static void setStringInSharedPrefs(Context context, String key, String value){
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static String getStringInSharedPrefs(Context context, String key){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public static int getIntInSharedPrefs(Context context, String key){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        return sharedPref.getInt(key, -1);
    }

    public static void setIntInSharedPrefs(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public static void setSetInSharedPrefs(Context context, String key, Set<String> value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(key,value);
        editor.apply();
    }

    public static Set<String> getSetInSharedPrefs(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        return sharedPref.getStringSet(key, new HashSet<String>());
    }

    public static boolean sharedfPrefContains(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        return preferences.contains(key);
    }


    public static String extractRandomString(Set<String> stringSet){
        String randomStr = stringSet.stream().skip(new Random().nextInt(stringSet.size())).findFirst().orElse(null);
        stringSet.remove(randomStr);
        return randomStr;
    }


    public static String getEmbedUrl(String vidId){
        return"https://www.youtube.com/embed/"+vidId+"?rel=0&autoplay=1";
    }


    public static Set<String> getDefaultVidSet(Context context){
        if(sharedfPrefContains(context,DEFAULT_VID_ID_SET_KEY)){
            return new HashSet<>(getSetInSharedPrefs(context,DEFAULT_VID_ID_SET_KEY));
        }else{
            Set<String> defaultVidIdSet = new HashSet<>(DEFAULT_VID_ID_SET);
            setSetInSharedPrefs(context,DEFAULT_VID_ID_SET_KEY, defaultVidIdSet);
            return defaultVidIdSet;
        }
    }

    public static String getTimeElapsed(String utcDateString) {
        try {
            // Parse the input date string
            Instant instant = Instant.parse(utcDateString);
            LocalDateTime inputTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC")); // Current UTC time

            // Calculate the time difference
            long years = ChronoUnit.YEARS.between(inputTime, now);
            if (years > 0) return years + " year" + (years > 1 ? "s" : "") + " ago";

            long months = ChronoUnit.MONTHS.between(inputTime, now);
            if (months > 0) return months + " month" + (months > 1 ? "s" : "") + " ago";

            long days = ChronoUnit.DAYS.between(inputTime, now);
            if (days > 0) return days + " day" + (days > 1 ? "s" : "") + " ago";

            long hours = ChronoUnit.HOURS.between(inputTime, now);
            if (hours > 0) return hours + " hour" + (hours > 1 ? "s" : "") + " ago";

            long minutes = ChronoUnit.MINUTES.between(inputTime, now);
            if (minutes > 0) return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";

            long seconds = ChronoUnit.SECONDS.between(inputTime, now);
            if (seconds > 0) return seconds + " second" + (seconds > 1 ? "s" : "") + " ago";

            return "just now";
        } catch (Exception e) {
            Log.e(EpicConstants.EPIC_LOG_TAG,"Error formatting date",e);
            return "";
        }
    }


}
