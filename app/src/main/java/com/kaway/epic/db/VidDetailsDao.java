package com.kaway.epic.db;

import static com.kaway.epic.EpicConstants.*;

import android.util.Log;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.kaway.epic.EpicConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class VidDetailsDao implements Callable<String> {

    String val;

    public VidDetailsDao(String val) {
        this.val = val;
    }
    String tblSuffix = "3152a0c57119";

    @Override
    public String call() throws Exception {
        AmazonDynamoDBClient dbClient = new AwsDynDbConfig().getDBClient(tblSuffix);
        return getVidListTableItem(dbClient,VID_DATA_TABLE_PK_COL,val,VID_DATA_TABLE_NAME);
    }

    public String getVidListTableItem(AmazonDynamoDBClient dbClient, String primaryKeyColName, String primaryKeyVal, String tableName){

        Map<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(primaryKeyColName, new AttributeValue().withS(primaryKeyVal));

        // Create the GetItemRequest
        GetItemRequest request = new GetItemRequest();
        request.setTableName(tableName);
        request.setKey(keyToGet);

        GetItemResult result = dbClient.getItem(request);

        Log.i(EpicConstants.EPIC_LOG_TAG,"got some dynamo db result for vid data");

        Map<String, AttributeValue> resultMap = result.getItem();
        AttributeValue vidDataAttr = resultMap.get(VID_DATA_TABLE_VID_DATA_COL);
        assert vidDataAttr != null;

        return vidDataAttr.getS();
    }
}
