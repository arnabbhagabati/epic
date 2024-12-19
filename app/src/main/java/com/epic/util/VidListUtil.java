package com.epic.util;

import static com.epic.EpicConstants.*;

import android.content.Context;
import android.util.Log;

import com.epic.ytservice.VidService;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Random;
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
            if(triesCount>5){
                //all the sets are empty
                break;
            }
            String nextVidSetKey = getNextVidSetKey(lastVidSetKey);
            op.addAll(EpicUtils.getJSONSetInSharedPrefs(context,nextVidSetKey));

            if(op.isEmpty() || op.size()<200){
                loadVidSet(context,nextVidSetKey,op);
            }
            lastVidSetKey = nextVidSetKey;
            triesCount++;
        }
        EpicUtils.setStringInSharedPrefs(context,LAST_VID_SET_KEY_IDX,lastVidSetKey);

        return op;
    }

    private String getNextVidSetKey(String prevVidSetKey){
        String nextVidSetKey = "";
        Set<String> vidSetKeys = new HashSet<>(Set.of(VID_KEY_0,VID_KEY_1,VID_KEY_2,VID_KEY_3));
        vidSetKeys.remove(prevVidSetKey);
        nextVidSetKey= vidSetKeys.stream().skip(new Random().nextInt(vidSetKeys.size())).findFirst().orElse(null);

        return nextVidSetKey;
    }

    private void loadVidSet(Context context, String sharedPrefVidSetKey, Set<JSONObject> op){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(() -> {
                try {
                    op.addAll(new VidService(context).getVidSet());
                    EpicUtils.setJSONSetInSharedPrefs(context,sharedPrefVidSetKey,op);
                } catch (Exception e) {
                    Log.e(EPIC_LOG_TAG, "loadVidSet - Cloud not vidSet ", e);
                }
            });
        executorService.shutdown();
    }

    public void loadInstallVidSetKeys(Context context, Set<JSONObject> currVidSet){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        LoadInstallVidSets loadVidSets = new LoadInstallVidSets(context,currVidSet);
        executorService.submit(loadVidSets);
        executorService.shutdown();
    }
}
