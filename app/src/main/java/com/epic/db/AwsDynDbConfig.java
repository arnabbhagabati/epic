package com.epic.db;

import static com.amazonaws.regions.Regions.US_WEST_2;


import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;


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
