package com.epic.util;

import static com.epic.EpicConstants.EPIC_LOG_TAG;
import static com.epic.EpicConstants.VID_KEY_0;
import static com.epic.EpicConstants.VID_KEY_1;
import static com.epic.EpicConstants.VID_KEY_2;

import android.content.Context;
import android.util.Log;

import com.epic.db.VidIdSetCurDao;
import com.epic.db.VidIdSetDao;
import com.epic.ytservice.VidService;

import org.json.JSONObject;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoadInstallVidSets implements Runnable {

    Context context;
    Set<JSONObject> currVidSet;

    public LoadInstallVidSets(Context context, Set<JSONObject> currVidSet) {
        this.context = context;
        this.currVidSet = currVidSet;
    }

    @Override
    public void run() {

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        VidIdSetCurDao vidIdSetDao = new VidIdSetCurDao("0");
        Future<List<String>> zeroSetFuture = executorService.submit(vidIdSetDao);
        List<String> zeroSet = null;
        try {
            zeroSet = zeroSetFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(EPIC_LOG_TAG,"Error retrieving even the vidList cnt", e);
        }

        if(zeroSet != null && !zeroSet.isEmpty()) {
            int vidSetRowCnt = Integer.parseInt(zeroSet.get(0));
            VidService vidService = new VidService(context);

            currVidSet.addAll(vidService.getVidSet(vidSetRowCnt));

            EpicUtils.setJSONSetInSharedPrefs(context, VID_KEY_0, vidService.getVidSet(vidSetRowCnt));
            EpicUtils.setJSONSetInSharedPrefs(context, VID_KEY_1, vidService.getVidSet(vidSetRowCnt));
            EpicUtils.setJSONSetInSharedPrefs(context, VID_KEY_2, vidService.getVidSet(vidSetRowCnt));
        }

        executorService.shutdown();
    }
}
