package com.kaway.epic;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.kaway.epic.androidcomponents.CommentAdapter;
import com.kaway.epic.beans.Comment;
import com.kaway.epic.beans.Reply;
import com.kaway.epic.ytservice.YTComments;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    String videoId = "e3yEg15PcGQ";
    String youTubeUrl = "https://www.youtube.com/embed/"+videoId;

    String frameVideo = "<html><body><iframe width=\"370\" height=\"380\" " +
            "src='" + youTubeUrl + "' frameborder=\"0\" allowfullscreen>" +
            "</iframe></body></html>";


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.mediaPlayerView);

        String regexYoutUbe = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        if (youTubeUrl.matches(regexYoutUbe)) {

            //setting web client
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });
            //web settings for JavaScript Mode
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webView.setWebChromeClient(new WebChromeClient());
            webView.loadUrl(youTubeUrl);
            //webView.loadData(frameVideo, "text/html", "utf-8");


        } else {
            Toast.makeText(MainActivity.this, "This is other video",
                    Toast.LENGTH_SHORT).show();
        }

        new YTComments().getComments(videoId);
        //RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Example comments with replies
        List<Reply> replies1 = new ArrayList<>();
        replies1.add(new Reply(1, "User A", "This is a reply."));
        replies1.add(new Reply(2, "User B", "Another reply."));

        List<Reply> replies2 = new ArrayList<>();
        replies2.add(new Reply(3, "User C", "Yet another reply."));

        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1, "Commenter 1", "This is the first comment.", replies1));
        comments.add(new Comment(2, "Commenter 2", "This is the second comment.", replies2));

        // Set up the adapter
        //CommentAdapter adapter = new CommentAdapter(this, comments);
        //recyclerView.setAdapter(adapter);
    }
}