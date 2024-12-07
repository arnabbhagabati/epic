package com.kaway.epic.util;

import static com.kaway.epic.EpicConstants.DEFAULT_VID_ID_SET;
import static com.kaway.epic.EpicConstants.DEFAULT_VID_ID_SET_KEY;
import static com.kaway.epic.EpicConstants.EPIC_LOG_TAG;
import static com.kaway.epic.EpicConstants.VID_ID;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static void setJSONSetInSharedPrefs(Context context, String key, Set<JSONObject> value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> jsonSetStr = new HashSet<>(value.stream().map(o -> o.toString()).collect(Collectors.toSet()));
        editor.putStringSet(key,jsonSetStr);
        editor.apply();
    }

    public static Set<JSONObject> getJSONSetInSharedPrefs(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        Set<String> jsonSetStr = sharedPref.getStringSet(key, new HashSet<String>());
        Set<JSONObject> vidJsonSet = jsonSetStr.stream().map(o -> {
            JSONObject op = null;
            try {
                op = new JSONObject(o);
            } catch (JSONException e) {
                Log.e(EPIC_LOG_TAG,"Error creating json while retrieving the vidList from sharedPrefs", e);
            }
            return op;
        }).collect(Collectors.toSet());

        return vidJsonSet;
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

    public static JSONObject extractRandomJson(Set<JSONObject> stringSet){
        JSONObject randomJson = stringSet.stream().skip(new Random().nextInt(stringSet.size())).findFirst().orElse(null);
        stringSet.remove(randomJson);
        return randomJson;
    }


    public static String getEmbedUrl(String vidId){
        return"https://www.youtube.com/embed/"+vidId+"?rel=0&autoplay=1";
    }


    public static Set<JSONObject> getDefaultVidSet(Context context){
        if(sharedfPrefContains(context,DEFAULT_VID_ID_SET_KEY)){
            return new HashSet<>(getJSONSetInSharedPrefs(context,DEFAULT_VID_ID_SET_KEY));
        }else{
            Set<JSONObject> vidIdSet = new HashSet<>();
            for(String vId : DEFAULT_VID_ID_SET){
                try {
                    vidIdSet.add(new JSONObject().put(VID_ID,vId));
                } catch (JSONException e) {
                    Log.e(EPIC_LOG_TAG,"Error creating json in getDefaultVidSet", e);
                }
            }
            setJSONSetInSharedPrefs(context,DEFAULT_VID_ID_SET_KEY, vidIdSet);
            return vidIdSet;
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


    public static String formatNumberToCompact(long number) {
        if (number < 1000) {
            return String.valueOf(number); // No conversion needed
        } else if (number < 1_000_000) {
            return String.format("%.1fK", number / 1000.0);
        } else if (number < 1_000_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else {
            return String.format("%.1fB", number / 1_000_000_000.0);
        }
    }



}
