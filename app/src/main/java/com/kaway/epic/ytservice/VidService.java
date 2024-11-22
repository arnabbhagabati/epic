package com.kaway.epic.ytservice;

import static com.kaway.epic.EpicConstants.EPIC_LOG_TAG;
import static com.kaway.epic.EpicConstants.FIRST_VIDSET_ID_KEY;
import static com.kaway.epic.EpicConstants.FUN_VID_ID_LIST_TABLE_NAME;
import static com.kaway.epic.EpicConstants.RETRIEVED_VID_SET_SET_KEY;
import static com.kaway.epic.EpicConstants.VID_ID_LIST_TABLE_PK_COL;
import static com.kaway.epic.EpicConstants.VID_KEY_0;
import static com.kaway.epic.EpicConstants.VID_KEY_1;
import static com.kaway.epic.EpicConstants.VID_KEY_2;

import android.content.Context;
import android.util.Log;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.kaway.epic.db.AwsDynDbConfig;
import com.kaway.epic.db.DynDbDao;
import com.kaway.epic.db.EpicDbDao;
import com.kaway.epic.util.EpicUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VidService  {

    Context context;

    public VidService(Context context) {
        this.context = context;
    }

   /* public List<String> getVidIDs(){
        int firstVidSetId = EpicUtils.getIntInSharedPrefs(this.context,FIRST_VIDSET_ID_KEY);

        if(firstVidSetId == -1){

            ExecutorService executorService = Executors.newFixedThreadPool(2);
            DynDbDao dynDbDao = new DynDbDao("0");
            Future<List<String>> zeroSetFuture = executorService.submit(dynDbDao);
            List<String> zeroSet = null;
            try {
                zeroSet = zeroSetFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(EPIC_LOG_TAG,"Error retrieving even the vidList cnt", e);
            }

            if(zeroSet != null && !zeroSet.isEmpty()){
                int vidSetRowCnt = Integer.parseInt(zeroSet.get(0));
                Random random = new Random();
                int newFirstVidSetId = random.nextInt(vidSetRowCnt) + 1;
                String newFirstVidSetIdStr = String.valueOf(newFirstVidSetId);

                DynDbDao dynDbDao0 = new DynDbDao(newFirstVidSetIdStr);
                Future<List<String>> firstVidSetFuture = executorService.submit(dynDbDao0);
                List<String> firstVidSet = null;
                try {
                    firstVidSet = firstVidSetFuture.get();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(EPIC_LOG_TAG,"Error retrieving even the vidList cnt", e);
                }

                EpicUtils.setListInSharedPrefs(context,VID_KEY_0,firstVidSet);
                Set<String> retrievedVidSetSet = new HashSet<>();
                retrievedVidSetSet.add(newFirstVidSetIdStr);
                EpicUtils.setSetInSharedPrefs(this.context,RETRIEVED_VID_SET_SET_KEY,retrievedVidSetSet);
            }
        }
    }*/


    public Set<String> getVidSet(){
        Set<String> vidSet = new HashSet<>();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        DynDbDao dynDbDao = new DynDbDao("0");
        Future<List<String>> zeroSetFuture = executorService.submit(dynDbDao);
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
                    DynDbDao dynDbDao0 = new DynDbDao(nextVidSetIdStr);
                    Future<List<String>> firstVidSetFuture = executorService.submit(dynDbDao0);

                    try {
                        List<String> vidList = firstVidSetFuture.get();
                        vidSet.addAll(vidList);
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e(EPIC_LOG_TAG,"Error retrieving even the vidList cnt", e);
                    }

                    //EpicUtils.setListInSharedPrefs(context,VID_KEY_2,firstVidSet);
                    alreadyRetVidSetIds.add(nextVidSetIdStr);
                    EpicUtils.setSetInSharedPrefs(this.context,RETRIEVED_VID_SET_SET_KEY,alreadyRetVidSetIds);
                    found=true;
                }
            }
        }

        return vidSet;
    }


    public Set<String> getVidSet(int vidSetRowCnt){

        Set<String> vidSet = new HashSet<>();

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
                DynDbDao dynDbDao0 = new DynDbDao(nextVidSetIdStr);
                Future<List<String>> firstVidSetFuture = executorService.submit(dynDbDao0);

                try {
                    List<String> vidList = firstVidSetFuture.get();
                    vidSet.addAll(vidList);
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(EPIC_LOG_TAG,"Error retrieving even the vidList cnt", e);
                }

                //EpicUtils.setListInSharedPrefs(context,VID_KEY_2,firstVidSet);
                alreadyRetVidSetIds.add(nextVidSetIdStr);
                EpicUtils.setSetInSharedPrefs(this.context,RETRIEVED_VID_SET_SET_KEY,alreadyRetVidSetIds);
                found=true;
            }
        }


        return vidSet;
    }

/*
    private void loadFirstVidSets(int n, int newFirstVidSetId){
        List<Integer> integerList = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            integerList.add(i);
        }
        // Shuffle the contents of the list
        Collections.shuffle(integerList);
        boolean fetched = false;
        int idx =0;

        while(!fetched){
            int vidSetId = integerList.get(idx);
            if(vidSetId == newFirstVidSetId){
                continue;
            }else{
                ExecutorService executorService = Executors.newFixedThreadPool(2);
                DynDbDao dynDbDao1 = new DynDbDao(String.valueOf(newFirstVidSetId));
                Future<List<String>> secVidSetFuture = executorService.submit(dynDbDao1);
                List<String> secVidSet = null;
                try {
                    secVidSet = secVidSetFuture.get();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(EPIC_LOG_TAG,"Error retrieving even the vidList cnt", e);
                }

                EpicUtils.setListInSharedPrefs(context,VID_KEY_1,secVidSet);
                fetched = true;
            }
            idx++;
        }

        EpicUtils.setIntInSharedPrefs(this.context,FIRST_VIDSET_ID_KEY,newFirstVidSetId);

    }



    public List<String> getVidIDs(Context context){
        List<String> funVidIds = new ArrayList<>();
        JSONArray op = new JSONArray();


        ExecutorService executorService = Executors.newFixedThreadPool(2);
        AwsDynDbConfig awsDynDbConfig = new AwsDynDbConfig(context);

        Future<List<String>> funFuture = executorService.submit(awsDynDbConfig);

        try {
            funVidIds = funFuture.get();
            EpicDbDao epicDbDao = new EpicDbDao(context);
            epicDbDao.insertVidList(1L,funVidIds);
            op= epicDbDao.getViIdList(1L);
            Log.i(EPIC_LOG_TAG,op.toString());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        return funVidIds;
    }*/


}
