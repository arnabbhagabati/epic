package com.kaway.epic.screenLayoutUtils;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kaway.epic.EpicConstants;
import com.kaway.epic.beans.Comment;
import com.kaway.epic.beans.Reply;
import com.kaway.epic.util.EpicUtils;
import com.kaway.epic.ytservice.VidService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LoadComments implements Callable<List<Comment>> {

    Context context;
    RecyclerView recyclerView;
    String vidId;

    public LoadComments(Context context, RecyclerView recyclerView, String vidId) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.vidId = vidId;
    }

    public List<Comment> showInitialComments(){

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        List<Comment> comments = new ArrayList<>();
        JSONObject vidDataObj = new VidService(context).getVidData(vidId);
        try {
            JSONArray commentsArray = vidDataObj.getJSONArray("Comments");

            for(int i=0;i<commentsArray.length();i++){
                try {
                    JSONObject comment = new JSONObject(String.valueOf(commentsArray.get(i)));
                    String author = comment.getString("author").substring(1);
                    String commentText = comment.getString("commentText");
                    JSONArray replies = comment.getJSONArray("replies");
                    String profileIconUrl = comment.getString("authorProfileImgUrl");
                    String commentDate = "  "+EpicUtils.getTimeElapsed(comment.getString("commentDate"));
                    String likes = EpicUtils.formatNumberToCompact(Long.parseLong(comment.getString("likes")));
                    List<Reply> repliesList = new ArrayList<>();
                    for(int j=0;j<replies.length();j++){
                        JSONObject reply = new JSONObject(replies.getString(j));
                        String replyText = reply.getString("replyText");
                        String replyAuth = reply.getString("author").substring(1);
                        String replierIcon = reply.getString("authorProfileImgUrl");
                        String replyDate = "  "+EpicUtils.getTimeElapsed(reply.getString("commentDate"));
                        String replyLikes = EpicUtils.formatNumberToCompact(Long.parseLong(reply.getString("likes")));
                        repliesList.add(new Reply(j,replyAuth,replyText,replierIcon,replyDate,replyLikes));
                    }
                    comments.add(new Comment(i,author,commentText,repliesList,profileIconUrl,commentDate,likes));
                } catch (JSONException e) {
                    Log.e(EpicConstants.EPIC_LOG_TAG,"error parsing vidData comment",e);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return comments;
    }

    @Override
    public List<Comment> call() throws Exception {
        return showInitialComments();
    }
}
