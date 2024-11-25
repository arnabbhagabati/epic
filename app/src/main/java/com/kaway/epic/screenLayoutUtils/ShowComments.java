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
import com.kaway.epic.util.EpicUtils;
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
        List<Comment> comments = new ArrayList<>();
        JSONObject vidDataObj = new VidService(context).getVidData(vidId);
        try {
            JSONArray commentsArray = vidDataObj.getJSONArray("Comments");

            for(int i=0;i<commentsArray.length();i++){
                try {
                    JSONObject comment = new JSONObject(String.valueOf(commentsArray.get(i)));
                    String author = comment.getString("author");
                    String commentText = comment.getString("commentText");
                    JSONArray replies = comment.getJSONArray("replies");
                    String profileIconUrl = comment.getString("authorProfileImgUrl");
                    String commentDate = EpicUtils.getTimeElapsed(comment.getString("commentDate"));
                    List<Reply> repliesList = new ArrayList<>();
                    for(int j=0;j<replies.length();j++){
                        JSONObject reply = new JSONObject(replies.getString(j));
                        String replyText = reply.getString("replyText");
                        String replyAuth = reply.getString("author");
                        String replierIcon = reply.getString("authorProfileImgUrl");
                        String replyDate = EpicUtils.getTimeElapsed(reply.getString("commentDate"));
                        repliesList.add(new Reply(j,replyAuth,replyText,replierIcon,replyDate));
                    }
                    comments.add(new Comment(i,author,commentText,repliesList,profileIconUrl,commentDate));
                } catch (JSONException e) {
                    Log.e(EpicConstants.EPIC_LOG_TAG,"error parsing vidData comment",e);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        InitialCommentAdapter adapter = new InitialCommentAdapter(context, comments);

        recyclerView.setAdapter(adapter);

    }

}
