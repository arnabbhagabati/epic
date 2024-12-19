package com.epic.androidcomponents;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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
    private WebView webView;

    public InitialCommentAdapter(Context context, List<Comment> comments, WebView webView){
        this.context = context;
        this.comments = comments;
        this.webView = webView;
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

        holder.commentAuthor.setOnClickListener(view -> {
            // Navigate to profile or perform an action
            webView.loadUrl("https://www.youtube.com/@"+comment.getAuthor());
        });


        viewMoreReplies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewMoreReplies.getText().toString().equals(context.getString(R.string.view_replies))) {

                    for(Reply reply : comment.getReplies()){
                        setReply(holder,reply);
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


    private void setReply(CommentViewHolder holder,Reply reply){
        View replyView = LayoutInflater.from(context).inflate(R.layout.comment_reply, holder.repliesContainer, false);
        TextView replyAuthor = replyView.findViewById(R.id.replierName);
        TextView replyText = replyView.findViewById(R.id.replyText);

        replyAuthor.setText(reply.getAuthor());


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
        replyAuthor.setOnClickListener(view -> {
            // Navigate to profile or perform an action
            webView.loadUrl("https://www.youtube.com/@"+reply.getAuthor());
        });

        if(reply.getText().contains("@@")) {
            String fullText = reply.getText();
            int startIdx =  reply.getText().indexOf("@@")+2;
            int endIdx = reply.getText().indexOf(" ",startIdx);
            String subText = fullText.substring(startIdx,endIdx);

            fullText = fullText.replace("@@","");

            // Create a SpannableString
            SpannableString spannableString = new SpannableString(fullText);

            // Find the start and end index of the subtext
            int startIndex = fullText.indexOf(subText);
            int endIndex = startIndex + subText.length();

            // Create a ClickableSpan for the subtext
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Redirect to another activity or URL
                    webView.loadUrl("https://www.youtube.com/@"+subText);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getColor(android.R.color.holo_blue_light));
                    ds.setUnderlineText(false);
                }
            };

            // Apply the ClickableSpan to the subtext
            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Optionally change the color of the link
            spannableString.setSpan(
                    new ForegroundColorSpan(context.getColor(android.R.color.holo_blue_light)),
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            // Set the SpannableString to the TextView
            replyText.setText(spannableString);
            // Enable clickable behavior
            replyText.setMovementMethod(LinkMovementMethod.getInstance());
        }else{
            replyText.setText(reply.getText());
        }


        holder.repliesContainer.addView(replyView);
    }
}
