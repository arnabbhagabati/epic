package com.kaway.epic.ytservice;

import android.os.AsyncTask;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;


public class YTComments {

    private static final String DEVELOPER_KEY = "AIzaSyCj2csNua3EbkajBXlhfCAImkrAldOoFss";

    private static final String APPLICATION_NAME = "API code samples";
    //private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    Logger logger = Logger.getLogger(this.getClass().getName());


    /*
    private CommentThreadListResponse executeCommentsAPI(ExecutorService executor) throws ExecutionException, InterruptedException {
        Future<CommentThreadListResponse> future = executor.submit(new Callable<CommentThreadListResponse>() {
            @Override
            public CommentThreadListResponse call() throws Exception {
                NetHttpTransport httpTransport = new com.google.api.client.http.javanet.NetHttpTransport();
                YouTube youtubeService = new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                        .setApplicationName(APPLICATION_NAME)
                        .build();
                YouTube.CommentThreads.List request = youtubeService.commentThreads()
                        .list("id,replies,snippet");
                CommentThreadListResponse response = request.setKey(DEVELOPER_KEY)
                        .setVideoId("e3yEg15PcGQ")
                        .execute();


                return response;
            }
        });

        System.out.println("Comments Thread Response is++");
        System.out.println(future.get());
        return future.get();
    }

    private String performNetworkOperation() {
        // Simulate a network operation (e.g., fetching data from API)
        try {
            Thread.sleep(2000);  // Simulate network delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Network Operation Result";
    }


    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = new com.google.api.client.http.javanet.NetHttpTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    public void getComments(String videoId) {
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            CommentThreadListResponse commentThreadListResponse = executeCommentsAPI(executorService);
            List<CommentThread> commentThreads = commentThreadListResponse.getItems();


        }catch(Exception e){
            System.out.println("couldnt retrieve comments");
            logger.log(Level.SEVERE, "An exception occurred: ", e);
        }
    }
    */
}
