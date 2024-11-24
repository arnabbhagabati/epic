package com.kaway.epic.util;

import static com.kaway.epic.EpicConstants.EPIC_LOG_TAG;
import static com.kaway.epic.EpicConstants.VID_KEY_0;
import static com.kaway.epic.EpicConstants.VID_KEY_1;
import static com.kaway.epic.EpicConstants.VID_KEY_2;
import static com.kaway.epic.EpicConstants.VID_KEY_3;

import android.content.Context;
import android.util.Log;

import com.kaway.epic.ytservice.VidService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VidListUtil {

    public Set<String> getNextVidSet(Context context){
        Log.i(EPIC_LOG_TAG,"getting the next set of vids");
        Set<String> op = new HashSet<>();
        Set<String> vids0 = EpicUtils.getSetInSharedPrefs(context,VID_KEY_0);

        Set<String> vids1 = EpicUtils.getSetInSharedPrefs(context,VID_KEY_1);
        Set<String> vids2 = EpicUtils.getSetInSharedPrefs(context,VID_KEY_2);
        Set<String> vids3 = EpicUtils.getSetInSharedPrefs(context,VID_KEY_3);

        if(vids0 == null || vids0.isEmpty()){
            //Set<String> vids1 = EpicUtils.getSetInSharedPrefs(context,VID_KEY_1);
            op.addAll(vids1);

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            RotateVidLists rotateVidLists = new RotateVidLists(context,1);

            executorService.submit(rotateVidLists);

        }else{
            op.addAll(vids0);
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            RotateVidLists rotateVidLists = new RotateVidLists(context,0);

            executorService.submit(rotateVidLists);
        }

        return op;
    }

    public void loadThreeVidKeys(Context context, Set<String> currVidSet){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        LoadAllVidSets loadAllVidSets = new LoadAllVidSets(context,currVidSet);
        executorService.submit(loadAllVidSets);
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
                Set<String> vids1 = EpicUtils.getSetInSharedPrefs(context,VID_KEY_1);
                Set<String> vids2 = EpicUtils.getSetInSharedPrefs(context,VID_KEY_2);
                Set<String> vids3 = EpicUtils.getSetInSharedPrefs(context,VID_KEY_3);

                EpicUtils.setSetInSharedPrefs(context,VID_KEY_0,vids1);
                EpicUtils.setSetInSharedPrefs(context,VID_KEY_1,vids2);
                EpicUtils.setSetInSharedPrefs(context,VID_KEY_2,vids3);

                EpicUtils.setSetInSharedPrefs(context,VID_KEY_3,new HashSet<>());

                if(vids3 == null || vids3.isEmpty()){
                    EpicUtils.setSetInSharedPrefs(context,VID_KEY_2,new VidService(context).getVidSet());
                }
            }else if(this.start == 1){

                Set<String> vids2 = EpicUtils.getSetInSharedPrefs(context,VID_KEY_2);
                Set<String> vids3 = EpicUtils.getSetInSharedPrefs(context,VID_KEY_3);

                EpicUtils.setSetInSharedPrefs(context,VID_KEY_0,vids2);
                EpicUtils.setSetInSharedPrefs(context,VID_KEY_1,vids3);

                EpicUtils.setSetInSharedPrefs(context,VID_KEY_2,new VidService(context).getVidSet());

            }else{
                Log.e(EPIC_LOG_TAG,"Incorrect data passed to RotateVidLists", new Exception("RotateVidLists received start other than 0 or 1"));
            }
        }
    }
}
