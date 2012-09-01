package com.ccst.kuaipan.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ccst.kuaipan.R;
import com.ccst.kuaipan.protocol.Protocol.BaseProtocolData;
import com.ccst.kuaipan.protocol.Protocol.ProtocolType;
import com.ccst.kuaipan.protocol.util.RequestEngine;
import com.ccst.kuaipan.protocol.util.RequestEngine.HttpRequestCallback;

public class OauthActivity extends Activity implements HttpRequestCallback{
    private WebView mWebView;
    private MyWebViewClient mClient;
    
    private String mWebUrl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oauth_activity);
        
        mWebUrl = getIntent().getStringExtra("url");
        if(TextUtils.isEmpty(mWebUrl)){
            finish();
            return;
        }
        
        initData();
    }
    
    private void initData() {
        mWebView = (WebView)findViewById(R.id.oauth_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.clearCache(true);
        mClient = new MyWebViewClient();
        mWebView.setWebViewClient(mClient);       
        
        mWebView.loadUrl(mWebUrl);
        
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView = null;
        System.gc();
    }
    
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            RequestEngine.getInstance(OauthActivity.this).accessToken(OauthActivity.this);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {       
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {       
            mWebView.goBack();       
            return true;       
        }       
        return super.onKeyDown(keyCode, event);       
    } 
    
    public static void show(Context c, String url){
        Intent helper = new Intent(c, OauthActivity.class);
        helper.putExtra("url", url);
        c.startActivity(helper);
    }

    @Override
    public void onHttpResult(BaseProtocolData data) {
        if(data.isSuccess){
            if(data.getType() == ProtocolType.ACCESS_TOKEN_PROTOCOL){
                finish();    
            }
        }
    }

    @Override
    public void onHttpStart(BaseProtocolData data) {
        
    }
}