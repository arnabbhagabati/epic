package com.kaway.epic.screenLayoutUtils;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kaway.epic.R;
import com.kaway.epic.androidcomponents.FullCommentAdapter;
import com.kaway.epic.androidcomponents.InitialCommentAdapter;
import com.kaway.epic.beans.Comment;
import com.kaway.epic.beans.Reply;

import java.util.ArrayList;
import java.util.List;

public class ShowComments {

    Context context;

    public ShowComments(Context context){
        this.context = context;
    }

    public void showInitialComments(RecyclerView recyclerView){

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        List<Reply> replies1 = new ArrayList<>();
        replies1.add(new Reply(1, "User A", "This is a reply."));
        replies1.add(new Reply(2, "User B", "Another reply"));
        replies1.add(new Reply(2, "User X", "Another Reply 2"));

        List<Reply> replies2 = new ArrayList<>();
        replies2.add(new Reply(3, "User C", "Yet another reply."));
        replies2.add(new Reply(3, "User B", "Yet another reply...."));

        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1, "Commenter 1", "This is the first comment.", replies1));
        comments.add(new Comment(2, "Commenter 2", "This is the second comment.", replies2));

        //Set up the adapter
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
        comments.add(new Comment(2, "Commenter 2", "This is the second comment.", replies2));

        //Set up the adapter
        FullCommentAdapter adapter = new FullCommentAdapter(context, comments);

        recyclerView.setAdapter(adapter);

    }
}
