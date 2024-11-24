package com.kaway.epic.util;

import static com.kaway.epic.EpicConstants.DEFAULT_VID_ID_SET;
import static com.kaway.epic.EpicConstants.DEFAULT_VID_ID_SET_KEY;
import static com.kaway.epic.EpicConstants.MIN_VID_LIST_SIZE_FOR_FETCH;
import static com.kaway.epic.EpicConstants.VID_KEY_0;
import static com.kaway.epic.EpicConstants.VID_KEY_1;
import static com.kaway.epic.EpicConstants.VID_KEY_2;
import static com.kaway.epic.EpicConstants.VID_KEY_3;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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


}
