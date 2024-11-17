package com.kaway.epic.androidcomponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kaway.epic.R;
import com.kaway.epic.beans.Comment;
import com.kaway.epic.beans.Reply;

import java.util.List;

public class InitialCommentAdapter  extends RecyclerView.Adapter<InitialCommentAdapter.CommentViewHolder>{

    private Context context;
    private List<Comment> comments;

    public InitialCommentAdapter(Context context, List<Comment> comments){
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.commentAuthor.setText(comment.getAuthor());
        holder.commentText.setText(comment.getText());
        String imgUrl = "https://yt3.ggpht.com/GNO2Zd94dZUUwdbrpEgXfH5wqv3O61YG3IhR5iQ_nb-iPOJ0Ws2f1gYUz58j133XXtPbQ3Jr=s48-c-k-c0x00ffffff-no-rj";

        Glide.with(context)
                .load(imgUrl)
                .placeholder(R.drawable.person_24dp).error(R.drawable.person_24dp)
                .into(holder.commenterProfilePic);

        holder.commenterProfilePic.setBackgroundResource(R.drawable.circular_background);
        // Clear any previous replies in the container
        holder.repliesContainer.removeAllViews();

        View replyView = LayoutInflater.from(context).inflate(R.layout.comment_reply_initial, holder.repliesContainer, false);
        TextView viewMoreReplies = replyView.findViewById(R.id.viewMoreReplies);


        viewMoreReplies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewMoreReplies.getText().toString().equals(context.getString(R.string.view_replies))) {
                    for(Reply reply : comment.getReplies()){
                        View replyView = LayoutInflater.from(context).inflate(R.layout.comment_reply, holder.repliesContainer, false);
                        TextView replyAuthor = replyView.findViewById(R.id.reply_author);
                        TextView replyText = replyView.findViewById(R.id.reply_text);

                        replyAuthor.setText(reply.getAuthor());
                        replyText.setText(reply.getText());

                        holder.repliesContainer.addView(replyView);
                    }
                    viewMoreReplies.setText(context.getString(R.string.hide_replies));
                }else{
                    holder.repliesContainer.removeAllViews();
                    viewMoreReplies.setText(context.getString(R.string.view_replies));
                    holder.repliesContainer.addView(replyView);

                }
            }
        });

        holder.repliesContainer.addView(replyView);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView commenterProfilePic;
        TextView commentAuthor;
        TextView commentText;
        LinearLayout repliesContainer;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commenterProfilePic = itemView.findViewById(R.id.commentProfileIcon);
            commentAuthor = itemView.findViewById(R.id.commenterName);
            commentText = itemView.findViewById(R.id.commentText);
            repliesContainer = itemView.findViewById(R.id.replies_container);
        }
    }
}
