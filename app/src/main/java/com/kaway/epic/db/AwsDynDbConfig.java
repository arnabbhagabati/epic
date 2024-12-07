package com.kaway.epic.db;

import static com.amazonaws.regions.Regions.US_WEST_2;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.kaway.epic.EpicConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class AwsDynDbConfig {

   String tableName = "us-west-2";
   public AmazonDynamoDBClient getDBClient(String tableSuffix){
        CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider(
                tableName+":cbb3044b-20dd-41cd-a93d-"+tableSuffix, US_WEST_2);

        AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(US_WEST_2));

        return dbClient;
    }

}
