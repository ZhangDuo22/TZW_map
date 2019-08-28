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

public class WebResidualActivity extends AppCompatActivity {
    private WebView mWvResidual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_residual);
        mWvResidual = findViewById(R.id.wv_residual);
        mWvResidual.getSettings().setJavaScriptEnabled(true);
        mWvResidual.setWebViewClient(new MyWebViewClient());
        mWvResidual.setWebChromeClient(new MyWebChromeClient());
        mWvResidual.loadUrl("https://baike.baidu.com/item/%E5%B9%B2%E5%9E%83%E5%9C%BE");
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
        if ((keyCode == KEYCODE_BACK) && mWvResidual.canGoBack()) {
            mWvResidual.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
