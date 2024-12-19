package com.epic.androidcomponents;

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
import com.epic.beans.Comment;
import com.epic.beans.Reply;

import java.util.List;
import com.epic.R;

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
        String imgUrl = comment.getProfileIconUrl();

        Glide.with(context)
                .load(imgUrl)
                .placeholder(R.drawable.person_24dp).error(R.drawable.person_24dp)
                .into(holder.commenterProfilePic);

        holder.commentTimeElapsed.setText(comment.getCommentDate());
        holder.commenterProfilePic.setBackgroundResource(R.drawable.circular_background);
        holder.commentLikeCount.setText(comment.getLikeCount());
        // Clear any previous replies in the container
        holder.repliesContainer.removeAllViews();

        View replyView = LayoutInflater.from(context).inflate(R.layout.view_replies, holder.repliesContainer, false);
        TextView viewMoreReplies = replyView.findViewById(R.id.viewMoreReplies);

        if(comment.getReplies().isEmpty()){
            viewMoreReplies.setVisibility(View.GONE);
        }


        viewMoreReplies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewMoreReplies.getText().toString().equals(context.getString(R.string.view_replies))) {
                    for(Reply reply : comment.getReplies()){
                        View replyView = LayoutInflater.from(context).inflate(R.layout.comment_reply, holder.repliesContainer, false);
                        TextView replyAuthor = replyView.findViewById(R.id.replierName);
                        TextView replyText = replyView.findViewById(R.id.replyText);

                        replyAuthor.setText(reply.getAuthor());
                        replyText.setText(reply.getText());

                        ImageView replyProfileIcon = replyView.findViewById(R.id.replyProfileIcon);
                        String imgUrl = reply.getProfileIcon();

                        Glide.with(context)
                                .load(imgUrl)
                                .placeholder(R.drawable.person_24dp).error(R.drawable.person_24dp)
                                .into(replyProfileIcon);

                        TextView replyTImeELapsed = replyView.findViewById(R.id.replyTimeElapsed);
                        replyTImeELapsed.setText(reply.getReplyDate());

                        TextView replyLikes = replyView.findViewById(R.id.replyLikesCount);
                        replyLikes.setText(reply.getLikeCount());

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
        TextView commentTimeElapsed;
        TextView commentLikeCount;
        LinearLayout repliesContainer;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commenterProfilePic = itemView.findViewById(R.id.commentProfileIcon);
            commentAuthor = itemView.findViewById(R.id.commenterName);
            commentText = itemView.findViewById(R.id.commentText);
            repliesContainer = itemView.findViewById(R.id.replies_container);
            commentTimeElapsed = itemView.findViewById(R.id.commentTimeElapsed);
            commentLikeCount = itemView.findViewById(R.id.commentLikesCount);
        }
    }
}
