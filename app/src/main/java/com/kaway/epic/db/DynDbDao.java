package com.kaway.epic.db;

import static com.kaway.epic.EpicConstants.FUN_VID_ID_LIST_TABLE_NAME;
import static com.kaway.epic.EpicConstants.VID_ID_LIST_TABLE_PK_COL;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class DynDbDao implements Callable<List<String>> {

    String val;

    public DynDbDao(String val) {
        this.val = val;
    }

    @Override
    public List<String> call() throws Exception {
        AmazonDynamoDBClient dbClient = new AwsDynDbConfig().getDBClient();
        return getVidListTableItem(dbClient,VID_ID_LIST_TABLE_PK_COL,val,FUN_VID_ID_LIST_TABLE_NAME);
    }

    public List<String> getVidListTableItem(AmazonDynamoDBClient dbClient, String primaryKeyColName, String primaryKeyVal, String tableName){

        Map<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(primaryKeyColName, new AttributeValue().withN(primaryKeyVal));

        // Create the GetItemRequest
        GetItemRequest request = new GetItemRequest();
        request.setTableName(tableName);
        request.setKey(keyToGet);

        GetItemResult result = dbClient.getItem(request);

        System.out.println("got some dynamo db result");

        Map<String, AttributeValue> resultMap = result.getItem();
        AttributeValue vidIdsAttr = resultMap.get("vidSet");
        List<String> vidIds = vidIdsAttr.getSS();

        return vidIds;
    }
}
