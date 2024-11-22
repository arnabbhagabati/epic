package com.kaway.epic;

import static com.kaway.epic.EpicConstants.DEFAULT_VID_ID_SET;
import static com.kaway.epic.EpicConstants.DEFAULT_VID_ID_SET_KEY;
import static com.kaway.epic.EpicConstants.RETRIEVED_VID_SET_SET_KEY;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kaway.epic.beans.EpicWebViewCLient;

import com.kaway.epic.screenLayoutUtils.ShowComments;
import com.kaway.epic.util.EpicUtils;
import com.kaway.epic.util.VidListUtil;
import com.kaway.epic.ytservice.VidService;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private WebView webView;
    RecyclerView recyclerView;
    String videoId = "5V5rySkTqQE";
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

        if(!EpicUtils.sharedfPrefContains(this,DEFAULT_VID_ID_SET_KEY) && !EpicUtils.sharedfPrefContains(this,RETRIEVED_VID_SET_SET_KEY)){
            //This is first launch
            VidListUtil vidListUtil = new VidListUtil();
            vidListUtil.loadThreeVidKeys(this);
            List<String> vidList = new ArrayList<>();
            vidList.addAll(DEFAULT_VID_ID_SET);

            Random random = new Random();
            String vidId = vidList.get(random.nextInt(vidList.size()));
            videoId = vidId;
            vidList.remove(vidId);
            EpicUtils.setSetInSharedPrefs(this,DEFAULT_VID_ID_SET_KEY,new HashSet<>(vidList));
        }

        Log.i("Showing vidid {}",videoId);
        youTubeUrl = "https://www.youtube.com/embed/"+videoId+"?rel=0&autoplay=1";


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

        recyclerView = findViewById(R.id.commentsRecyclerView);
        new ShowComments(this).showInitialComments(recyclerView);

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