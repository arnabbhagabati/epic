package com.kaway.epic.beans;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.constraintlayout.widget.ConstraintLayout;


public class EpicWebViewCLient extends WebViewClient{

    Context context;
    ConstraintLayout rootLayout;

    public EpicWebViewCLient(Context context,ConstraintLayout rootLayout) {
        this.context = context;
        this.rootLayout = rootLayout;
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);

        if(!isReload && !url.contains("www.youtube.com/embed")){
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
            layoutParams.matchConstraintPercentHeight = 1.0f;
            layoutParams.matchConstraintPercentWidth = 1.0f;
            view.setLayoutParams(layoutParams);
            view.setFocusable(WebView.FOCUSABLE);
            view.setFocusableInTouchMode(true);
        }else{
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
            int orientation = context.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // code for portrait mode
                layoutParams.matchConstraintPercentHeight = 0.65f;
                layoutParams.matchConstraintPercentWidth = 1.0f;
            } else {
                // code for landscape mode
                layoutParams.matchConstraintPercentHeight = 1.0f;
                layoutParams.matchConstraintPercentWidth = 0.7f;
            }
            view.setLayoutParams(layoutParams);
        }

    }

    // Enable back navigation with WebView history
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        System.out.println("onPageFinished called");

        if(url.contains("www.youtube.com/embed")){
            view.clearHistory();
        }
        rootLayout.setVisibility(View.VISIBLE);
        view.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()");  // this line is needed to autoplay the video
        super.onPageFinished(view, url);
        //view.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()");
    }


}
