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
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kaway.epic.androidcomponents.CommentAdapter;
import com.kaway.epic.beans.Comment;
import com.kaway.epic.beans.EpicWebViewCLient;
import com.kaway.epic.beans.Reply;
import com.kaway.epic.ytservice.YTComments;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    RecyclerView recyclerView;
    String videoId = "54zE3WRyxBc";
    String youTubeUrl = "https://www.youtube.com/embed/"+videoId+"?rel=0&autoplay=1";

    String frameVideo = "<iframe src=\"https://www.youtube.com/embed/UqHh6TvGQIQ\" title=\"This is a title\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
    String frame2 = "<iframe id=\"video\" src=\"https://www.youtube.com/embed/54zE3WRyxBc?rel=0&autoplay=1\" frameborder=\"0\" allowfullscreen=\"allowfullscreen\" mozallowfullscreen=\"mozallowfullscreen\" msallowfullscreen=\"msallowfullscreen\" oallowfullscreen=\"oallowfullscreen\" webkitallowfullscreen=\"webkitallowfullscreen\"></iframe>";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.mediaPlayerView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new MyChrome());
        webView.setWebViewClient(new EpicWebViewCLient());

        String regexYoutUbe = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        if (youTubeUrl.matches(regexYoutUbe)) {

            //setting web client

            //web settings for JavaScript Mode
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webView.loadUrl(youTubeUrl);
            //webView.loadDataWithBaseURL("https://www.youtube.com", frame2, "text/html", "UTF-8", null);




        } else {
            Toast.makeText(MainActivity.this, "This is other video",
                    Toast.LENGTH_SHORT).show();
        }

        new YTComments().getComments(videoId);
        recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Example comments with replies
        List<Reply> replies1 = new ArrayList<>();
        replies1.add(new Reply(1, "User A", "This is a reply."));
        replies1.add(new Reply(2, "User B", "Another reply."));

        List<Reply> replies2 = new ArrayList<>();
        replies2.add(new Reply(3, "User C", "Yet another reply."));

        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1, "Commenter 1", "This is the first comment.", replies1));
        comments.add(new Comment(2, "Commenter 2", "This is the second comment.", replies2));

        //Set up the adapter
        CommentAdapter adapter = new CommentAdapter(this, comments);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        // Check if WebView has back history
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}