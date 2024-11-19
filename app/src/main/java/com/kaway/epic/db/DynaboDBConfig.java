package com.kaway.epic.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.kaway.epic.ytservice.VidService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynaboDBConfig {
    private String tail = "TDTHR73E7";
    private String appTail = "JQFW8LQ6uP0KP2+q2";
    private static final Logger LOG = LoggerFactory.getLogger(DynaboDBConfig.class);

   /* public DynamoDbClient getDynamoDbClient(Context context,String str){
        DynamoDbClient ddb = null;
        try {
            SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            ;
            String x = "AKIATA" + preferences.getString("YouTube_Tag", "youtube") + tail;
            String app = str + preferences.getString("Video_Id", "youtube") + appTail;

            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(x, app);
            StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCreds);
            Region region = Region.US_WEST_2;
            ddb = DynamoDbClient.builder()
                    .region(region)
                    .credentialsProvider(credentialsProvider)
                    .build();
        }catch(Throwable t){
            LOG.error(t.getMessage(),t);
            t.printStackTrace();
        }

        return ddb;
    }*/
}
