package com.kaway.epic.util;

import static com.kaway.epic.EpicConstants.*;

import android.content.Context;
import android.util.Log;

import com.kaway.epic.ytservice.VidService;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VidListUtil {

    public Set<JSONObject> getNextVidSet(Context context){
        String lastVidSetKey = "";
        if(EpicUtils.sharedfPrefContains(context,LAST_VID_SET_KEY_IDX)){
            lastVidSetKey = EpicUtils.getStringInSharedPrefs(context,LAST_VID_SET_KEY_IDX);
        }else{
            EpicUtils.setStringInSharedPrefs(context,LAST_VID_SET_KEY_IDX,VID_KEY_0);
        }

        Set<JSONObject> vids0 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_0);
        Set<JSONObject> vids11 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_1);
        Set<JSONObject> vids22 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_2);
        Set<JSONObject> vids3 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_3);

        Set<JSONObject> op = new HashSet<>();

        int triesCount = 0;

        while(op.isEmpty()){
            if(triesCount>4){
                //all the sets are empty
                break;
            }
            String nextVidSetKey = getNextVidSetKey(lastVidSetKey);
            op.addAll(EpicUtils.getJSONSetInSharedPrefs(context,nextVidSetKey));

            if(op.isEmpty()){
                loadVidSet(context,nextVidSetKey);
            }
            lastVidSetKey = nextVidSetKey;
            triesCount++;
        }
        EpicUtils.setStringInSharedPrefs(context,LAST_VID_SET_KEY_IDX,lastVidSetKey);

        return op;
    }

    private String getNextVidSetKey(String prevVidSetKey){
        String nextVidSetKey = "";
        switch(prevVidSetKey){
            case VID_KEY_0:
                nextVidSetKey =VID_KEY_1;
                break;
            case VID_KEY_1:
                nextVidSetKey = VID_KEY_2;
                break;
            case VID_KEY_2:
                nextVidSetKey = VID_KEY_3;
                break;
            case VID_KEY_3:
                nextVidSetKey = VID_KEY_0;
                break;
            default:
                nextVidSetKey = VID_KEY_0;
                break;
        }
        return nextVidSetKey;
    }

    private void loadVidSet(Context context,String sharedPrefVidSetKey){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(() -> {
                try {
                    EpicUtils.setJSONSetInSharedPrefs(context,sharedPrefVidSetKey,new VidService(context).getVidSet());
                } catch (Exception e) {
                    Log.e(EPIC_LOG_TAG, "loadVidSet - Cloud not vidSet ", e);
                }
            });
        executorService.shutdown();
    }

    public void loadVidSetKeys(Context context, Set<JSONObject> currVidSet){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        LoadVidSets loadVidSets = new LoadVidSets(context,currVidSet);
        executorService.submit(loadVidSets);
        executorService.shutdown();
    }
}
