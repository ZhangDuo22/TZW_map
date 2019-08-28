package com.rabbit.tzw.classify;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rabbit.tzw.R;

import static android.view.KeyEvent.KEYCODE_BACK;

public class WebRecyclableActivity extends AppCompatActivity {
    private WebView mWvRecyclable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_recyclable);
        mWvRecyclable = findViewById(R.id.wv_recyclable);
        mWvRecyclable.getSettings().setJavaScriptEnabled(true);
        mWvRecyclable.setWebViewClient(new MyWebViewClient());
        mWvRecyclable.setWebChromeClient(new MyWebChromeClient());
        mWvRecyclable.loadUrl("https://baike.baidu.com/item/%E5%8F%AF%E5%9B%9E%E6%94%B6%E7%89%A9/2479461?fromtitle=%E5%8F%AF%E5%9B%9E%E6%94%B6%E5%9E%83%E5%9C%BE&fromid=2084517");
    }
    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }
    }

    class MyWebViewClient extends WebViewClient {
        /*@Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.loadUrl(request.getUrl().toString());
            } else {
                view.loadUrl(request.toString());
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("webView","onPageStarted...");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("webView","onPageFinished");
//            mWvMain.loadUrl("javascript:alert('hello')");
//            mWvMain.evaluateJavascript("javascript:alert('hello')",null);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && mWvRecyclable.canGoBack()) {
            mWvRecyclable.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
