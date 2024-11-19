package com.kaway.epic.db;

import static com.amazonaws.regions.Regions.US_WEST_2;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class AwsDynDbConfig implements Callable<List<String>> {

    Context context;

    public AwsDynDbConfig(Context context) {
        this.context = context;
    }

    @Override
    public List<String> call() throws Exception {
        List<String> op = new ArrayList<>();
        CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider(
                 "us-west-2:cbb3044b-20dd-41cd-a93d-3152a0c57119", US_WEST_2);
        AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(US_WEST_2));
        System.out.println("Epic table ");
        System.out.println("Epic table regions"+dbClient.getRegions());
        DescribeTableResult dcr = dbClient.describeTable("funVidList");
        dbClient.listTables();
        System.out.println("Epic table funVidList");
        System.out.println(dcr);

        op = getVidListTableItem(dbClient,"id","1","funVidList");
        return op;
    }


    private List<String> getVidListTableItem(AmazonDynamoDBClient dbClient,String primaryKeyColName,String primaryKeyVal,String tableName){

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
