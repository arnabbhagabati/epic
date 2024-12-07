package com.kaway.epic;

import static com.kaway.epic.EpicConstants.*;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kaway.epic.androidcomponents.InitialCommentAdapter;
import com.kaway.epic.beans.Comment;
import com.kaway.epic.beans.EpicWebViewCLient;

import com.kaway.epic.screenLayoutUtils.LoadComments;
import com.kaway.epic.util.EpicUtils;
import com.kaway.epic.util.VidListUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class MainActivity extends AppCompatActivity {

    private WebView webView;
    RecyclerView commentsView;
    private GestureDetector gestureDetector;
    Set<JSONObject> currVidSet = new HashSet<>();
    JSONObject activityVidId = null;
    boolean isLongPress;
    private float originalX, originalY;
    ConstraintLayout rootLayout;
    JSONObject vidObj = null;
    private boolean showSpalsh = true;

    String frameVideo = "<iframe src=\"https://www.youtube.com/embed/UqHh6TvGQIQ\" title=\"This is a title\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
    String frame2 = "<iframe id=\"video\" src=\"https://www.youtube.com/embed/54zE3WRyxBc?rel=0&autoplay=1\" frameborder=\"0\" allowfullscreen=\"allowfullscreen\" mozallowfullscreen=\"mozallowfullscreen\" msallowfullscreen=\"msallowfullscreen\" oallowfullscreen=\"oallowfullscreen\" webkitallowfullscreen=\"webkitallowfullscreen\"></iframe>";

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> (showSpalsh || (vidObj == null) ));

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showSpalsh = false; // Update the condition
        }, 500);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.mediaPlayerView);
        commentsView = findViewById(R.id.commentsRecyclerView);
        rootLayout = findViewById(R.id.rootLayout);


        if(!EpicUtils.sharedfPrefContains(this,DEFAULT_VID_ID_SET_KEY) && !EpicUtils.sharedfPrefContains(this,RETRIEVED_VID_SET_SET_KEY)){
            //This is first launch
            VidListUtil vidListUtil = new VidListUtil();
            vidListUtil.loadVidSetKeys(this,currVidSet);
            Set<JSONObject> defaultVidSet = EpicUtils.getDefaultVidSet(this);
            vidObj = EpicUtils.extractRandomJson(defaultVidSet);
            EpicUtils.setJSONSetInSharedPrefs(this,DEFAULT_VID_ID_SET_KEY,defaultVidSet);
        }else{
            currVidSet = new VidListUtil().getNextVidSet(this);
            if(!currVidSet.isEmpty()){
                vidObj = EpicUtils.extractRandomJson(currVidSet);
            }else{
               Set<JSONObject> defaultVidSet = EpicUtils.getDefaultVidSet(this);
               if(defaultVidSet.isEmpty()){
                   Toast.makeText(this,"Could not find any more videos to show",Toast.LENGTH_LONG).show();
               }else {
                   vidObj = EpicUtils.extractRandomJson(defaultVidSet);
                   EpicUtils.setJSONSetInSharedPrefs(this, DEFAULT_VID_ID_SET_KEY, defaultVidSet);
               }
            }
        }

        if(null == vidObj){
            Toast.makeText(this,"- Could not retrieve videos -",Toast.LENGTH_LONG).show();
            return;
        }

        Log.i(EPIC_LOG_TAG , "Showing vidid "+vidObj);

        if(savedInstanceState == null || savedInstanceState.isEmpty()){
            initializeWebView(vidObj);
            initializeComments(vidObj);
        }else{
            String savedVid = savedInstanceState.getString(VID_ID);
            try {
                JSONObject savedVidObj = new JSONObject(savedVid);
                initializeWebView(savedVidObj);
                initializeComments(savedVidObj);
            } catch (JSONException e) {
                Log.e(EPIC_LOG_TAG, "Cloud not load vid saved in bundle", e);
            }
        }

        // GestureDetector for detecting long press
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                isLongPress = true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        originalX = rootLayout.getTranslationX();
        originalY = rootLayout.getTranslationY();

        // OnTouchListener to handle touch events
        commentsView.setOnTouchListener(new View.OnTouchListener() {
            private float initialX, initialY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                gestureDetector.onTouchEvent(event);
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getRawX();
                        initialY = event.getRawY();

                        isLongPress = false;
                    case MotionEvent.ACTION_MOVE:
                        if (isLongPress) {

                            float deltaX = event.getRawX() - initialX;
                            float deltaY = event.getRawY() - initialY;

                            // Adjust for the view's layout parameters
                            rootLayout.setTranslationX(originalX + deltaX);
                            rootLayout.setTranslationY(originalY + deltaY);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        commentsView.removeCallbacks(null); // Remove long press callback
                        if (isLongPress) {
                            float deltaX = Math.abs(rootLayout.getTranslationX() - originalX);
                            float deltaY = Math.abs(rootLayout.getTranslationY() - originalY);

                            // Get the parent's dimensions
                            View parent = (View) rootLayout.getParent();
                            float thresholdX = parent.getWidth() * 0.15f; // 20% of width
                            float thresholdY = parent.getHeight() * 0.15f; // 20% of height

                            // Snap back if the movement is less than 20% of the parent's dimensions
                            if (deltaX < thresholdX && deltaY < thresholdY) {
                                rootLayout.animate()
                                        .translationX(originalX)
                                        .translationY(originalY)
                                        .setDuration(20)
                                        .start();
                            }else{
                                //rootLayout.setVisibility(View.INVISIBLE);
                                rootLayout.setX(originalX);
                                rootLayout.setY(originalY);
                                reloadMediaPlayerView();
                            }
                            rootLayout.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        }
                        isLongPress = false;
                        return false;
                }
                return false;
            }
        });

         /*
        Use this code intstead of OnBackPressed later.
        Right now this makes back press very low and hangy
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);*/

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
        private CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback)
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

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView(JSONObject vidObj) {
        String vidUrl = null;
        try {
            vidUrl = EpicUtils.getEmbedUrl(vidObj.getString(VID_ID));
            webView.setWebChromeClient(new MyChrome());
            webView.setWebViewClient(new EpicWebViewCLient(this,rootLayout));
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webView.loadUrl(vidUrl);
            activityVidId = vidObj;
        } catch (JSONException e) {
            Log.e(EPIC_LOG_TAG, "Cloud not initializeWebView ", e);
        }
    }


    private void initializeComments(JSONObject vidObj) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        if(vidObj.has(COMMENTS_DATA)){

        }else {
            try {
                String vidId = vidObj.getString(VID_ID);
                Future<List<Comment>> future = executorService.submit(new LoadComments(this, commentsView, vidId));
                executorService.execute(() -> {
                    try {
                        // Get the result from the Callable
                        List<Comment> comments = future.get();
                        InitialCommentAdapter adapter = new InitialCommentAdapter(this, comments);

                        runOnUiThread(() -> commentsView.setAdapter(adapter));
                    } catch (Exception e) {
                        Log.e(EPIC_LOG_TAG, "Cloud not load comments for vid " + vidId, e);
                    }
                });
                //new ShowComments(this).showInitialComments(recyclerView,vidId);
                executorService.shutdown();
            } catch (JSONException e) {
                Log.e(EPIC_LOG_TAG,"JSON Error in initializeRecyclerView", e);
            }
        }
    }

    private void reloadMediaPlayerView() {
       JSONObject vidObject = null;
       try {
           if(currVidSet != null && !currVidSet.isEmpty()){
               vidObject = EpicUtils.extractRandomJson(currVidSet);
           }else{
               writeCurrVidSetToSharedPref();
               currVidSet = new VidListUtil().getNextVidSet(this);
               if(currVidSet == null || currVidSet.isEmpty()){
                   Set<JSONObject> defaultVidSet = EpicUtils.getDefaultVidSet(this);
                   if(defaultVidSet != null && !defaultVidSet.isEmpty()){
                       vidObject = EpicUtils.extractRandomJson(defaultVidSet);
                       EpicUtils.setJSONSetInSharedPrefs(this,DEFAULT_VID_ID_SET_KEY,defaultVidSet);
                   }
                   if(vidObject == null || vidObject.isNull(VID_ID)){
                       Toast.makeText(this,"Could not retrieve next video",Toast.LENGTH_LONG).show();
                   }else{
                       Log.i(EPIC_LOG_TAG,"Loading new vidId in reloadMediaPlayerView ");
                   }
               }else{
                   vidObject = EpicUtils.extractRandomJson(currVidSet);
               }
           }

           if(vidObject != null){
               reloadCommentsView(vidObject);
               webView.loadUrl(EpicUtils.getEmbedUrl(vidObject.getString(VID_ID)));
           }else{
               Toast.makeText(this,"Could not retrieve video",Toast.LENGTH_LONG).show();
           }

        } catch (JSONException e) {
           Log.i(EPIC_LOG_TAG,"Error Loading new vidId in reloadMediaPlayerView ");
        }
        activityVidId = vidObject;
    }

    private void reloadCommentsView(JSONObject vidObject) {
        commentsView.setAdapter(null);
        if(vidObject.has(COMMENTS_DATA)){

        }else {

            try {
                String vidId = vidObject.getString(VID_ID);
                ExecutorService executorService = Executors.newFixedThreadPool(1);
                Future<List<Comment>> future = executorService.submit(new LoadComments(this, commentsView, vidId));
                executorService.execute(() -> {
                    try {
                        // Get the result from the Callable
                        List<Comment> comments = future.get();
                        InitialCommentAdapter adapter = new InitialCommentAdapter(this, comments);

                        runOnUiThread(() -> commentsView.setAdapter(adapter));
                    } catch (Exception e) {
                        Log.e(EPIC_LOG_TAG, "Cloud not load comments for vid" + vidId, e);
                    }
                });
                executorService.shutdown();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onStop() {
        writeCurrVidSetToSharedPref();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(VID_ID,activityVidId.toString());
    }

    private void writeCurrVidSetToSharedPref(){
        String lastVidSetKey = EpicUtils.getStringInSharedPrefs(this,LAST_VID_SET_KEY_IDX);
        EpicUtils.setJSONSetInSharedPrefs(this,lastVidSetKey,currVidSet);
        Log.i(EPIC_LOG_TAG,"currVidSet saved");
    }

}