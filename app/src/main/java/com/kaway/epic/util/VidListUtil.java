package com.kaway.epic.util;

import static com.kaway.epic.EpicConstants.*;

import android.content.Context;
import android.util.Log;

import com.kaway.epic.androidcomponents.InitialCommentAdapter;
import com.kaway.epic.beans.Comment;
import com.kaway.epic.db.VidIdSetDao;
import com.kaway.epic.ytservice.VidService;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    public void loadThreeVidKeys(Context context, Set<JSONObject> currVidSet){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        LoadAllVidSets loadAllVidSets = new LoadAllVidSets(context,currVidSet);
        executorService.submit(loadAllVidSets);
        executorService.shutdown();
    }

    public static void resetRetrievedVidSet(Context context,int upto){
        Set<String> newRetrievedVidSet = new HashSet<>();
        for(int i=1;i<=upto;i++){
            newRetrievedVidSet.add(String.valueOf(i));
        }
        EpicUtils.setSetInSharedPrefs(context,RETRIEVED_VID_SET_SET_KEY,newRetrievedVidSet);
    }

    /*
    public Set<JSONObject> getNextVidSet(Context context,Set<JSONObject> currVidSet){
        Log.i(EPIC_LOG_TAG,"getting the next set of vids");
        Set<JSONObject> op = new HashSet<>();
        Set<JSONObject> vids0 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_0);
        Set<JSONObject> vids11 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_1);
        Set<JSONObject> vids22 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_2);

        if(vids0 == null || vids0.isEmpty()){
            Set<JSONObject> vids1 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_1);
            op.addAll(vids1);

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            RotateVidLists rotateVidLists = new RotateVidLists(context,1);

            executorService.submit(rotateVidLists);
            executorService.shutdown();

        }else{
            op = vids0;
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            RotateVidLists rotateVidLists = new RotateVidLists(context,0);

            executorService.submit(rotateVidLists);
            executorService.shutdown();
        }

        if(op == null || op.isEmpty()){
            op.addAll(EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_2));
            if(op == null || op.isEmpty()){
                loadThreeVidKeys(context,currVidSet);
            }else{
                loadThreeVidKeys(context);
            }

        }

        return op;
    }

    public void loadThreeVidKeys(Context context){
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        VidIdSetDao vidIdSetDao = new VidIdSetDao("0");
        Future<List<String>> zeroSetFuture = executorService.submit(vidIdSetDao);
        List<String> zeroSet = null;
        try {
            zeroSet = zeroSetFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(EPIC_LOG_TAG,"Error retrieving even the vidList cnt", e);
        }

        if(zeroSet != null && !zeroSet.isEmpty()) {
            int vidSetRowCnt = Integer.parseInt(zeroSet.get(0));
            executorService.execute(() -> {
                try {
                    VidService vidService = new VidService(context);
                    EpicUtils.setJSONSetInSharedPrefs(context, VID_KEY_0, vidService.getVidSet(vidSetRowCnt));
                    EpicUtils.setJSONSetInSharedPrefs(context, VID_KEY_1, vidService.getVidSet(vidSetRowCnt));
                    EpicUtils.setJSONSetInSharedPrefs(context, VID_KEY_2, vidService.getVidSet(vidSetRowCnt));
                } catch (Exception e) {
                    Log.e(EPIC_LOG_TAG, "Cloud not loadThreeVidKeys ", e);
                }
            });

        }
    }


    private class RotateVidLists implements Runnable{

        Context context;
        int start;

        public RotateVidLists(Context context,int start) {
            this.context = context;
            this.start = start;
        }

        @Override
        public void run() {
            if(this.start == 0){
                Set<JSONObject> vids1 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_1);
                Set<JSONObject> vids2 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_2);
                Set<JSONObject> vids3 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_3);

                EpicUtils.setJSONSetInSharedPrefs(context,VID_KEY_0,vids1);
                EpicUtils.setJSONSetInSharedPrefs(context,VID_KEY_1,vids2);
                EpicUtils.setJSONSetInSharedPrefs(context,VID_KEY_2,vids3);

                EpicUtils.setJSONSetInSharedPrefs(context,VID_KEY_3,new HashSet<>());

                if(vids3 == null || vids3.isEmpty()){
                    EpicUtils.setJSONSetInSharedPrefs(context,VID_KEY_2,new VidService(context).getVidSet());
                }
            }else if(this.start == 1){

                Set<JSONObject> vids2 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_2);
                Set<JSONObject> vids3 = EpicUtils.getJSONSetInSharedPrefs(context,VID_KEY_3);

                EpicUtils.setJSONSetInSharedPrefs(context,VID_KEY_0,vids2);
                EpicUtils.setJSONSetInSharedPrefs(context,VID_KEY_1,vids3);

                EpicUtils.setJSONSetInSharedPrefs(context,VID_KEY_2,new VidService(context).getVidSet());

            }else{
                Log.e(EPIC_LOG_TAG,"Incorrect data passed to RotateVidLists", new Exception("RotateVidLists received start other than 0 or 1"));
            }
        }
    }
    */
}
