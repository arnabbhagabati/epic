package com.kaway.epic;


import static com.kaway.epic.EpicConstants.*;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

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
import android.widget.TextView;
import android.widget.Toast;
import com.kaway.epic.androidcomponents.InitialCommentAdapter;
import com.kaway.epic.androidcomponents.EpicWebViewCLient;
import com.kaway.epic.beans.Comment;
import com.kaway.epic.beans.Vid;
import com.kaway.epic.screenLayoutUtils.LoadComments;
import com.kaway.epic.util.EpicUtils;
import com.kaway.epic.util.PreLoadComments;
import com.kaway.epic.util.VidListUtil;

import org.json.JSONArray;
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
    private boolean loadInComplete = true;

    private TextView titleView;
    private String vidTitle = "";
    private int reloadCount =0;

    String frameVideo = "<iframe src=\"https://www.youtube.com/embed/UqHh6TvGQIQ\" title=\"This is a title\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
    String frame2 = "<iframe id=\"video\" src=\"https://www.youtube.com/embed/54zE3WRyxBc?rel=0&autoplay=1\" frameborder=\"0\" allowfullscreen=\"allowfullscreen\" mozallowfullscreen=\"mozallowfullscreen\" msallowfullscreen=\"msallowfullscreen\" oallowfullscreen=\"oallowfullscreen\" webkitallowfullscreen=\"webkitallowfullscreen\"></iframe>";

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> (showSpalsh || loadInComplete ));

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showSpalsh = false; // Update the condition
        }, 500);

        super.onCreate(savedInstanceState);

        if(!EpicUtils.sharedfPrefContains(this, EpicConstants.DEFAULT_VID_ID_SET_KEY) && !EpicUtils.sharedfPrefContains(this, EpicConstants.RETRIEVED_VID_SET_SET_KEY)){
            //This is first launch
            VidListUtil vidListUtil = new VidListUtil();
            vidListUtil.loadInstallVidSetKeys(this,currVidSet);
            Set<JSONObject> defaultVidSet = EpicUtils.getDefaultVidSet(this);
            EpicUtils.setJSONSetInSharedPrefs(this, EpicConstants.DEFAULT_VID_ID_SET_KEY,defaultVidSet);
            while(currVidSet.isEmpty()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            vidObj = EpicUtils.extractRandomJson(currVidSet);
            loadInComplete = false;
        }else{
            currVidSet = new VidListUtil().getNextVidSet(this);
            vidObj = EpicUtils.extractRandomJson(currVidSet);
            loadInComplete = false;
        }

        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.mediaPlayerView);
        commentsView = findViewById(R.id.commentsRecyclerView);
        rootLayout = findViewById(R.id.rootLayout);
        titleView = findViewById(R.id.videoTitle);
        titleView.setText(vidTitle);


        if(savedInstanceState == null || savedInstanceState.isEmpty()){
            /*try {
                JSONObject vidObjTmp = new JSONObject();
                vidObjTmp.put(VID_ID,"ESg_Yz4vSMU");
                initializeWebView(vidObjTmp);
                initializeComments(vidObjTmp);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }*/
            initializeWebView(vidObj);
            initializeComments(vidObj);
        }else{
            String savedVid = savedInstanceState.getString(EpicConstants.VID_ID);
            try {
                JSONObject savedVidObj = new JSONObject(savedVid);
                initializeWebView(savedVidObj);
                initializeComments(savedVidObj);
            } catch (JSONException e) {
                Log.e(EpicConstants.EPIC_LOG_TAG, "Cloud not load vid saved in bundle", e);
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
                            float thresholdX = parent.getWidth() * 0.1f; // 20% of width
                            float thresholdY = parent.getHeight() * 0.1f; // 20% of height

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


        //Use this code intstead of OnBackPressed later.
        //Right now this makes back press very slow and hangy
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);

        OneTimeWorkRequest preLoadComments = new OneTimeWorkRequest.Builder(PreLoadComments.class).addTag(PRE_LOAD_COMMENTS_WORKER_TAG).build();
        WorkManager.getInstance(this).enqueueUniqueWork(PRE_LOAD_COMMENTS_WORKER_TAG,ExistingWorkPolicy.KEEP,preLoadComments);
    }

    /*
    @Override
    public void onBackPressed() {
        // Check if WebView has back history
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }*/


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
        String vidUrl = "";
        try {
            webView.setWebChromeClient(new MyChrome());
            webView.setWebViewClient(new EpicWebViewCLient(this,rootLayout));
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            if(vidObj != null && vidObj.has(EpicConstants.VID_ID)){
                vidUrl = EpicUtils.getEmbedUrl(vidObj.getString(EpicConstants.VID_ID));
            }
            webView.loadUrl(vidUrl);
            activityVidId = vidObj;
        } catch (JSONException e) {
            Log.e(EpicConstants.EPIC_LOG_TAG, "Cloud not initializeWebView ", e);
        }
    }


    private void initializeComments(JSONObject vidObj) {
        commentsView.setAdapter(null);
        boolean commentsFound =  false;
        List<Comment> comments = null;
        try {
            if(vidObj == null){
                return;
            }else {
                    ExecutorService executorService = Executors.newFixedThreadPool(2);
                    String vidId = vidObj.getString(EpicConstants.VID_ID);
                    Future<Vid> future = executorService.submit(new LoadComments(this, commentsView, vidId));
                    executorService.execute(() -> {
                        try {
                            Vid vid = future.get();
                            vidTitle = vid.getTitle();
                            InitialCommentAdapter adapter = new InitialCommentAdapter(this, vid.getComments(), webView);
                            runOnUiThread(() -> {
                                titleView.setText(vidTitle);
                                commentsView.setAdapter(adapter);
                            });
                        } catch (Exception e) {
                            Log.e(EpicConstants.EPIC_LOG_TAG, "Cloud not load comments for vid " + vidId, e);
                        }
                    });
                    //new ShowComments(this).showInitialComments(recyclerView,vidId);
                    executorService.shutdown();
            }
        } catch (JSONException e) {
            Log.e(EpicConstants.EPIC_LOG_TAG,"JSON Error in initializeRecyclerView", e);
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
                       EpicUtils.setJSONSetInSharedPrefs(this, EpicConstants.DEFAULT_VID_ID_SET_KEY,defaultVidSet);
                   }
                   if(vidObject == null || vidObject.isNull(EpicConstants.VID_ID)){
                       Toast.makeText(this,"Could not retrieve next video",Toast.LENGTH_LONG).show();
                   }else{
                       Log.i(EpicConstants.EPIC_LOG_TAG,"Loading new vidId in reloadMediaPlayerView ");
                   }
               }else{
                   vidObject = EpicUtils.extractRandomJson(currVidSet);
               }
           }

           if(vidObject != null){
               reloadCommentsView(vidObject);
               webView.loadUrl(EpicUtils.getEmbedUrl(vidObject.getString(EpicConstants.VID_ID)));
           }else{
               Toast.makeText(this,"Could not retrieve video",Toast.LENGTH_LONG).show();
           }

        } catch (JSONException e) {
           Log.i(EpicConstants.EPIC_LOG_TAG,"Error Loading new vidId in reloadMediaPlayerView ");
        }
        activityVidId = vidObject;
    }

    private void reloadCommentsView(JSONObject vidObject) {
        commentsView.setAdapter(null);
        titleView.setText("");
        boolean commentsFound =  false;
        List<Comment> comments = null;
        try {
            if(vidObject.has(EpicConstants.VID_DATA)){
                JSONObject vidData = vidObject.getJSONObject(EpicConstants.VID_DATA);
                if(vidData.has(VID_TITLE_KEY)){
                    vidTitle = vidData.getString(VID_TITLE_KEY);
                }

                if(vidData.has(COMMENTS_DATA)){
                    JSONArray commentsData = vidData.getJSONArray(COMMENTS_DATA);
                    comments = new EpicUtils().getCommentListFromJsonArray(commentsData);
                }

                if(comments != null && !comments.isEmpty() && vidTitle.length()>1 ){
                    commentsFound = true;
                }
            }
            if(commentsFound){
                InitialCommentAdapter adapter = new InitialCommentAdapter(this,comments,webView);
                this.runOnUiThread(() -> {
                    titleView.setText(vidTitle);
                    commentsView.setAdapter(adapter);
                    commentsView.requestLayout();
                });
            }else {
                    String vidId = vidObject.getString(EpicConstants.VID_ID);
                    ExecutorService executorService = Executors.newFixedThreadPool(2);
                    Future<Vid> future = executorService.submit(new LoadComments(this, commentsView, vidId));
                    executorService.execute(() -> {
                        try {
                            Vid vid = future.get();
                            vidTitle = vid.getTitle();
                            InitialCommentAdapter adapter = new InitialCommentAdapter(this, vid.getComments(),webView);
                            this.runOnUiThread(() -> {
                                titleView.setText(vidTitle);
                                commentsView.setAdapter(adapter);
                                commentsView.requestLayout();
                            });
                        } catch (Exception e) {
                            Log.e(EpicConstants.EPIC_LOG_TAG, "Cloud not load comments for vid" + vidId, e);
                        }
                    });
                    executorService.shutdown();

            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        reloadCount++;
        if(reloadCount%8==0){
            new EpicUtils().showRateMeDialog(this);
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
        outState.putString(EpicConstants.VID_ID,activityVidId.toString());
    }

    private void writeCurrVidSetToSharedPref(){
        String lastVidSetKey = EpicUtils.getStringInSharedPrefs(this, EpicConstants.LAST_VID_SET_KEY_IDX);
        if(lastVidSetKey != null && !lastVidSetKey.isEmpty() && lastVidSetKey.length()>1){
            EpicUtils.setCurrVidSetInSharedPrefs(currVidSet,lastVidSetKey,this);
        }else{
            Set<JSONObject> vids = EpicUtils.getJSONSetInSharedPrefs(this, EpicConstants.VID_KEY_3);
            vids.addAll(currVidSet);
            EpicUtils.setCurrVidSetInSharedPrefs(currVidSet,VID_KEY_3,this);
        }
        Log.i(EpicConstants.EPIC_LOG_TAG,"currVidSet saved");
    }




}