package com.kaway.epic;

import static com.kaway.epic.EpicConstants.DEFAULT_VID_ID_SET;
import static com.kaway.epic.EpicConstants.DEFAULT_VID_ID_SET_KEY;
import static com.kaway.epic.EpicConstants.EPIC_LOG_TAG;
import static com.kaway.epic.EpicConstants.RETRIEVED_VID_SET_SET_KEY;
import static com.kaway.epic.EpicConstants.VID_KEY_0;
import static com.kaway.epic.EpicConstants.VID_KEY_3;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
    private GestureDetector gestureDetector;
    Set<String> currVidSet = new HashSet<>();

    String frameVideo = "<iframe src=\"https://www.youtube.com/embed/UqHh6TvGQIQ\" title=\"This is a title\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
    String frame2 = "<iframe id=\"video\" src=\"https://www.youtube.com/embed/54zE3WRyxBc?rel=0&autoplay=1\" frameborder=\"0\" allowfullscreen=\"allowfullscreen\" mozallowfullscreen=\"mozallowfullscreen\" msallowfullscreen=\"msallowfullscreen\" oallowfullscreen=\"oallowfullscreen\" webkitallowfullscreen=\"webkitallowfullscreen\"></iframe>";

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.mediaPlayerView);
        ConstraintLayout constraintLayout = findViewById(R.id.rootLayout);
        recyclerView = findViewById(R.id.commentsRecyclerView);

        String videoId = "PmvsAi89BDM";
        if(!EpicUtils.sharedfPrefContains(this,DEFAULT_VID_ID_SET_KEY) && !EpicUtils.sharedfPrefContains(this,RETRIEVED_VID_SET_SET_KEY)){
            //This is first launch
            VidListUtil vidListUtil = new VidListUtil();
            vidListUtil.loadThreeVidKeys(this,currVidSet);
            Set<String> defaultVidSet = EpicUtils.getDefaultVidSet(this);
            videoId = EpicUtils.extractRandomString(defaultVidSet);
            EpicUtils.setSetInSharedPrefs(this,DEFAULT_VID_ID_SET_KEY,defaultVidSet);
        }else{
            currVidSet = new VidListUtil().getNextVidSet(this);
            if(!currVidSet.isEmpty()){
               videoId = EpicUtils.extractRandomString(currVidSet);
            }else{
               Set<String> defaultVidSet = EpicUtils.getDefaultVidSet(this);
               videoId = EpicUtils.extractRandomString(defaultVidSet);
               EpicUtils.setSetInSharedPrefs(this,DEFAULT_VID_ID_SET_KEY,defaultVidSet);
            }
        }

        Log.i(EPIC_LOG_TAG , "Showing vidid "+videoId);


        gestureDetector = new GestureDetector(this, new GestureListener());
        /*webView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });*/
        recyclerView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });
        constraintLayout.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });

        initializeWebView(EpicUtils.getEmbedUrl(videoId));
        initializeRecyclerView(videoId);
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


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 75;  // Minimum distance for a swipe
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;  // Minimum velocity for a swipe

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) { // Check if horizontal swipe
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private void onSwipeRight() {
        // Action for right swipe
        System.out.println("Swiped Right!");
        reloadMediaPlayerView();
    }

    private void onSwipeLeft() {
        // Action for left swipe
        System.out.println("Swiped Left!");
        reloadMediaPlayerView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView(String vidUrl) {
        webView.setWebChromeClient(new MyChrome());
        webView.setWebViewClient(new EpicWebViewCLient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.loadUrl(vidUrl);
    }


    private void initializeRecyclerView(String vidId) {
        new ShowComments(this).showInitialComments(recyclerView,vidId);
    }

    private void reloadMediaPlayerView() {
        String vidId = null;
       if(!currVidSet.isEmpty()){
           vidId = EpicUtils.extractRandomString(currVidSet);
           Log.i(EPIC_LOG_TAG,"Loading new vidId "+vidId);
       }else{
           currVidSet = new VidListUtil().getNextVidSet(this);
           if(currVidSet.isEmpty()){
               Set<String> defaultVidSet = EpicUtils.getDefaultVidSet(this);
               vidId = EpicUtils.extractRandomString(defaultVidSet);
               EpicUtils.setSetInSharedPrefs(this,DEFAULT_VID_ID_SET_KEY,defaultVidSet);
               if(vidId == null){
                   Toast.makeText(this,"Could not retrieve next video",Toast.LENGTH_LONG).show();
               }else{
                   Log.i(EPIC_LOG_TAG,"Loading new vidId "+vidId);
               }
           }
       }

       reloadCommentsRecyclerView(vidId);
       webView.loadUrl(EpicUtils.getEmbedUrl(vidId));
    }

    private void reloadCommentsRecyclerView(String vidId) {
        recyclerView.removeAllViews();
        new ShowComments(this).showInitialComments(recyclerView,vidId);
    }

    @Override
    public void onStop() {
        EpicUtils.setSetInSharedPrefs(this,VID_KEY_3,currVidSet);
        Log.i(EPIC_LOG_TAG,"currVidSet saved");
        super.onStop();
    }


}