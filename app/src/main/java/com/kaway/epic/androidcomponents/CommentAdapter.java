package com.kaway.epic.androidcomponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaway.epic.R;
import com.kaway.epic.beans.Comment;
import com.kaway.epic.beans.Reply;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
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

        // Clear any previous replies in the container
        holder.repliesContainer.removeAllViews();

        // Inflate and add reply views dynamically
        for (Reply reply : comment.getReplies()) {
            View replyView = LayoutInflater.from(context).inflate(R.layout.comment_reply, holder.repliesContainer, false);
            TextView replyAuthor = replyView.findViewById(R.id.reply_author);
            TextView replyText = replyView.findViewById(R.id.reply_text);

            replyAuthor.setText(reply.getAuthor());
            replyText.setText(reply.getText());

            holder.repliesContainer.addView(replyView);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentAuthor;
        TextView commentText;
        LinearLayout repliesContainer;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentAuthor = itemView.findViewById(R.id.commenterName);
            commentText = itemView.findViewById(R.id.commentText);
            repliesContainer = itemView.findViewById(R.id.replies_container);
        }
    }
}
