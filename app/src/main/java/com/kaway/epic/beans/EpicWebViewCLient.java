package com.kaway.epic.beans;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class EpicWebViewCLient extends WebViewClient{
    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);

        // Example: Update the state of back and forward buttons
        updateNavigationButtons(view);

        System.out.println("something about the url changed "+url);
    }

    // Enable back navigation with WebView history
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    private void updateNavigationButtons(WebView myWebView) {
        // Check if the WebView can go back or forward in the browsing history
        boolean canGoBack = myWebView.canGoBack();
        boolean canGoForward = myWebView.canGoForward();

        // Here you would update the visibility or state of back/forward buttons
        // For example:
        // backButton.setEnabled(canGoBack);
        // forwardButton.setEnabled(canGoForward);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        System.out.println("onPageFinished called");
        view.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()");
    }


}
