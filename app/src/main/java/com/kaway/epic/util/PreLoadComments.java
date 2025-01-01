package com.kaway.epic.util;

import static com.kaway.epic.EpicConstants.COMMENTS_DATA;
import static com.kaway.epic.EpicConstants.VID_DATA;
import static com.kaway.epic.EpicConstants.VID_ID;
import static com.kaway.epic.EpicConstants.VID_KEY_0;
import static com.kaway.epic.EpicConstants.VID_KEY_1;
import static com.kaway.epic.EpicConstants.VID_KEY_2;
import static com.kaway.epic.EpicConstants.VID_KEY_3;
import static com.kaway.epic.EpicConstants.VID_TITLE_KEY;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.kaway.epic.EpicConstants;
import com.kaway.epic.ytservice.VidService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class PreLoadComments extends Worker {

    WorkerParameters params;
    Context context;

    public PreLoadComments( @NonNull Context context,
                            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        this.params = params;
    }

    @Override
    public Result doWork() {

        Set<String> vidSetKeys = new HashSet<>(Set.of(VID_KEY_0,VID_KEY_1,VID_KEY_2,VID_KEY_3));

        for(String vidSetKey : vidSetKeys){
            Set<JSONObject> vidObjectSet = EpicUtils.getJSONSetInSharedPrefs(context,vidSetKey);

            for(JSONObject vidObject : vidObjectSet) {
                if (!vidObject.has(VID_DATA) && vidObject.has(VID_ID)) {
                    try {
                        String vidId = vidObject.getString(VID_ID);
                        JSONObject vidDataObj = new VidService(context).getVidData(vidId);
                        String vidTitle = "";
                        if (vidDataObj.has(VID_TITLE_KEY)) {
                            vidTitle = vidDataObj.getString(VID_TITLE_KEY);
                        }

                        JSONObject viddataObj = new JSONObject();
                        viddataObj.put(VID_TITLE_KEY, vidTitle);

                        if(vidDataObj.has("Comments")){
                            JSONArray commentsArray = vidDataObj.getJSONArray("Comments");
                            viddataObj.put(COMMENTS_DATA, commentsArray);
                        }

                        vidObject.put(VID_DATA, viddataObj);

                    } catch (JSONException e) {
                        Log.e(EpicConstants.EPIC_LOG_TAG, "PreLoadComments Cloud not load comments for vid" , e);
                    }
                }
            }

            EpicUtils.setJSONSetInSharedPrefs(context, vidSetKey, vidObjectSet);
        }

        Log.i(EpicConstants.EPIC_LOG_TAG, "PreLoadComments completed successfully");
        return Result.success();
    }
}
