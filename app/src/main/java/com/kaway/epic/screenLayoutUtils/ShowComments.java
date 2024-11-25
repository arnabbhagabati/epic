package com.kaway.epic.screenLayoutUtils;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kaway.epic.EpicConstants;
import com.kaway.epic.androidcomponents.FullCommentAdapter;
import com.kaway.epic.androidcomponents.InitialCommentAdapter;
import com.kaway.epic.beans.Comment;
import com.kaway.epic.beans.Reply;
import com.kaway.epic.ytservice.VidService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowComments {

    Context context;

    public ShowComments(Context context){
        this.context = context;
    }

    public void showInitialComments(RecyclerView recyclerView,String vidId){

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        JSONArray vidDataArray = new VidService(context).getVidData(vidId);
        List<Comment> comments = new ArrayList<>();

        for(int i=0;i<vidDataArray.length();i++){
            try {
                JSONObject comment = new JSONObject(String.valueOf(vidDataArray.get(i)));
                String author = comment.getString("author");
                String commentText = comment.getString("commentText");
                JSONArray replies = comment.getJSONArray("replies");
                List<Reply> repliesList = new ArrayList<>();
                for(int j=0;j<replies.length();j++){
                    JSONObject reply = new JSONObject(replies.getString(j));
                    String replyText = reply.getString("replyText");
                    String replyAuth = reply.getString("author");
                    repliesList.add(new Reply(j,replyAuth,replyText));
                }
                comments.add(new Comment(i,author,commentText,repliesList));
            } catch (JSONException e) {
                Log.e(EpicConstants.EPIC_LOG_TAG,"error parsing vidData comment",e);
            }
        }

        InitialCommentAdapter adapter = new InitialCommentAdapter(context, comments);

        recyclerView.setAdapter(adapter);

    }

    public void showAllComments(RecyclerView recyclerView){

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        List<Reply> replies1 = new ArrayList<>();
        replies1.add(new Reply(1, "User A", "This is a reply."));
        replies1.add(new Reply(2, "User B", "View More Replies"));

        List<Reply> replies2 = new ArrayList<>();
        replies2.add(new Reply(3, "User C", "Yet another reply."));

        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1, "Commenter 1", "This is the first comment.", replies1));
        comments.add(new Comment(2, "Commenter 2", "This is the second comment.This is a comment text that can span multiple lines.This is a comment text that can span multiple lines.", replies2));

        //Set up the adapter
        FullCommentAdapter adapter = new FullCommentAdapter(context, comments);

        recyclerView.setAdapter(adapter);

    }
}
