package com.kaway.epic.util;

import static com.kaway.epic.EpicConstants.EPIC_LOG_TAG;
import static com.kaway.epic.EpicConstants.VID_KEY_0;
import static com.kaway.epic.EpicConstants.VID_KEY_1;
import static com.kaway.epic.EpicConstants.VID_KEY_2;

import android.content.Context;
import android.util.Log;

import com.kaway.epic.db.DynDbDao;
import com.kaway.epic.ytservice.VidService;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoadAllVidSets implements Runnable {

    Context context;

    public LoadAllVidSets(Context context) {
        this.context = context;
    }

    @Override
    public void run() {

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        DynDbDao dynDbDao = new DynDbDao("0");
        Future<List<String>> zeroSetFuture = executorService.submit(dynDbDao);
        List<String> zeroSet = null;
        try {
            zeroSet = zeroSetFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(EPIC_LOG_TAG,"Error retrieving even the vidList cnt", e);
        }

        if(zeroSet != null && !zeroSet.isEmpty()) {
            int vidSetRowCnt = Integer.parseInt(zeroSet.get(0));
            VidService vidService = new VidService(context);


            EpicUtils.setSetInSharedPrefs(context, VID_KEY_0, vidService.getVidSet(vidSetRowCnt));
            EpicUtils.setSetInSharedPrefs(context, VID_KEY_1, vidService.getVidSet(vidSetRowCnt));
            EpicUtils.setSetInSharedPrefs(context, VID_KEY_2, vidService.getVidSet(vidSetRowCnt));


        }
    }
}
