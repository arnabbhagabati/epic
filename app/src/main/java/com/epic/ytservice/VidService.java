package com.epic.ytservice;

import static com.epic.EpicConstants.EPIC_LOG_TAG;
import static com.epic.EpicConstants.RETRIEVED_VID_SET_SET_KEY;
import static com.epic.EpicConstants.VID_ID;

import android.content.Context;
import android.util.Log;

import com.epic.db.VidDetailsDao;
import com.epic.db.VidIdSetCurDao;
import com.epic.db.VidIdSetDao;
import com.epic.util.EpicUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VidService  {

    Context context;

    public VidService(Context context) {
        this.context = context;
    }

    public Set<JSONObject> getVidSet(){
        Set<JSONObject> vidSet = new HashSet<>();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        VidIdSetCurDao vidIdSetDao = new VidIdSetCurDao("0");
        Future<List<String>> zeroSetFuture = executorService.submit(vidIdSetDao);
        List<String> zeroSet = null;
        try {
            zeroSet = zeroSetFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(EPIC_LOG_TAG,"Error retrieving even the vidList cnt", e);
        }

        if(zeroSet != null && !zeroSet.isEmpty()){

            int vidSetRowCnt = Integer.parseInt(zeroSet.get(0));
            Set<String> alreadyRetVidSetIds = EpicUtils.getSetInSharedPrefs(this.context,RETRIEVED_VID_SET_SET_KEY);
            Set<String> triedSet = new HashSet<>();

            boolean found = false;

            while(!found){
                if(triedSet.size() == vidSetRowCnt){
                    break;
                }
                Random random = new Random();
                int nextVidSetId = random.nextInt(vidSetRowCnt) + 1;
                String nextVidSetIdStr = String.valueOf(nextVidSetId);
                if(alreadyRetVidSetIds.contains(nextVidSetIdStr)){
                    triedSet.add(nextVidSetIdStr);
                }else{
                    VidIdSetDao vidIdSetDao0 = new VidIdSetDao(nextVidSetIdStr);
                    Future<List<String>> firstVidSetFuture = executorService.submit(vidIdSetDao0);

                    try {
                        List<String> vidList = firstVidSetFuture.get();
                        for(String vId : vidList){
                            JSONObject vidObj = new JSONObject();
                            vidObj.put(VID_ID,vId);
                            vidSet.add(vidObj);
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e(EPIC_LOG_TAG,"Error retrieving the vidList", e);
                    } catch (JSONException e) {
                        Log.e(EPIC_LOG_TAG,"Error creating json in getVidSet", e);
                    }

                    //EpicUtils.setListInSharedPrefs(context,VID_KEY_2,firstVidSet);
                    alreadyRetVidSetIds.add(nextVidSetIdStr);
                    EpicUtils.setSetInSharedPrefs(this.context,RETRIEVED_VID_SET_SET_KEY,alreadyRetVidSetIds);
                    found=true;
                }
            }
        }
        executorService.shutdown();
        return vidSet;
    }


    public Set<JSONObject> getVidSet(int vidSetRowCnt){

        Set<JSONObject> vidSet = new HashSet<>();
        Set<String> alreadyRetVidSetIds = EpicUtils.getSetInSharedPrefs(this.context,RETRIEVED_VID_SET_SET_KEY);
        Set<String> triedSet = new HashSet<>();

        boolean found = false;

        while(!found){
            if(triedSet.size() == vidSetRowCnt){
                break;
            }
            Random random = new Random();
            int nextVidSetId = random.nextInt(vidSetRowCnt) + 1;
            String nextVidSetIdStr = String.valueOf(nextVidSetId);
            if(alreadyRetVidSetIds.contains(nextVidSetIdStr)){
                triedSet.add(nextVidSetIdStr);
            }else{
                ExecutorService executorService = Executors.newFixedThreadPool(1);
                VidIdSetDao vidIdSetDao0 = new VidIdSetDao(nextVidSetIdStr);
                Future<List<String>> firstVidSetFuture = executorService.submit(vidIdSetDao0);

                try {
                    List<String> vidList = firstVidSetFuture.get();
                    for(String vId : vidList){
                        JSONObject vidObj = new JSONObject();
                        vidObj.put(VID_ID,vId);
                        vidSet.add(vidObj);
                    }

                } catch (ExecutionException | InterruptedException e) {
                    Log.e(EPIC_LOG_TAG,"Error retrieving the vidList set", e);
                } catch (JSONException e) {
                    Log.e(EPIC_LOG_TAG,"Error creating json while retrieving the vidList set", e);
                }

                alreadyRetVidSetIds.add(nextVidSetIdStr);
                EpicUtils.setSetInSharedPrefs(this.context,RETRIEVED_VID_SET_SET_KEY,alreadyRetVidSetIds);
                found=true;
                executorService.shutdown();
            }
        }

        return vidSet;
    }


    public JSONObject getVidData(String vidId){
        JSONObject op = new JSONObject();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        VidDetailsDao vidDetailsDao = new VidDetailsDao(vidId);
        Future<String> vidDetailsFuture = executorService.submit(vidDetailsDao);

        try {
            String vidDetails = vidDetailsFuture.get();
            op = new JSONObject(vidDetails);
        } catch (ExecutionException | InterruptedException e) {
            Log.e(EPIC_LOG_TAG,"Error retrieving the vid data ", e);
        } catch (JSONException e) {
            Log.e(EPIC_LOG_TAG,"JSONException retrieving the vid data ", e);
        }
        executorService.shutdown();
        return op;
    }

}
