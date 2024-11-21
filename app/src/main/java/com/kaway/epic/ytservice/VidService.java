package com.kaway.epic.ytservice;

import android.content.Context;

import com.kaway.epic.db.AwsDynDbConfig;
import com.kaway.epic.db.EpicDbDao;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VidService {

    private static final Logger LOG = LoggerFactory.getLogger(VidService.class);

    //ToDo : Store this in a DB first
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
            LOG.info(op.toString());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        return funVidIds;
    }
}
