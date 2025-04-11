package com.kaway.epic.db;

import static com.kaway.epic.EpicConstants.VID_DATA_TABLE_NAME;
import static com.kaway.epic.EpicConstants.VID_DATA_TABLE_PK_COL;
import static com.kaway.epic.EpicConstants.VID_DATA_TABLE_VID_DATA_COL;

import android.util.Log;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchGetItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchGetItemResult;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import com.kaway.epic.EpicConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class BatchVidDetailsDao implements Callable<Map<String,JSONObject>> {

    Set<String> vidIds;

    public BatchVidDetailsDao(Set<String> vidIds) {
        this.vidIds = vidIds;
    }
    String tblSuffix = "3152a0c57119";

    @Override
    public Map<String,JSONObject> call() throws Exception {
        AmazonDynamoDBClient dbClient = new AwsDynDbConfig().getDBClient(tblSuffix);
        return getVidListTableItem(dbClient,VID_DATA_TABLE_PK_COL,vidIds,VID_DATA_TABLE_NAME);
    }

    public Map<String,JSONObject> getVidListTableItem(AmazonDynamoDBClient dbClient, String primaryKeyColName, Set<String> vidIds, String tableName){


        List<Map<String, AttributeValue>> keysList = new ArrayList<>();
        Map<String,JSONObject> op = new HashMap<>();

        for(String vidId : vidIds){
            Map<String, AttributeValue> key = new HashMap<>();
            key.put(primaryKeyColName, new AttributeValue().withS(vidId));
            keysList.add(key);
        }

        Map<String, KeysAndAttributes> requestItems = new HashMap<>();
        requestItems.put(tableName, new KeysAndAttributes().withKeys(keysList));


        BatchGetItemRequest request = new BatchGetItemRequest().withRequestItems(requestItems);
        BatchGetItemResult result = dbClient.batchGetItem(request);

        // Print the retrieved items
        result.getResponses().get(tableName).forEach(item -> {
            String vidId = item.get("vidId").getS();
            String vidData = item.get("videoData").getS();
            try {
                JSONObject vidDataObj = new JSONObject(vidData);
                op.put(vidId,vidDataObj);
            } catch (JSONException e) {
                Log.e(EpicConstants.EPIC_LOG_TAG, "BatchVidDetailsDao econtered JSONException" , e);
            }
        });


        return op;
    }
}
