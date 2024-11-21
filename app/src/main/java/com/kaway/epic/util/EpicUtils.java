package com.kaway.epic.util;

import static com.kaway.epic.EpicConstants.DEFAULT_VID_ID;
import static com.kaway.epic.EpicConstants.DEFAULT_YT;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class EpicUtils {

    private void setStringInSharedPrefs(Context context, String key, String value){
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
}
