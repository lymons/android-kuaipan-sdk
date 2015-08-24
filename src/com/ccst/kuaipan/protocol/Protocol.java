package com.ccst.kuaipan.protocol;

import java.net.HttpURLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;

import com.ccst.kuaipan.protocol.data.Consumer;
import com.ccst.kuaipan.protocol.data.HttpRequestInfo;
import com.ccst.kuaipan.protocol.data.KuaipanHTTPResponse;
import com.ccst.kuaipan.protocol.data.TokenPair;
import com.ccst.kuaipan.protocol.util.OauthUtility;
import com.ccst.kuaipan.protocol.util.RequestEngine;
import com.ccst.kuaipan.protocol.util.RequestEngine.HttpRequestCallback;
import com.ccst.kuaipan.protocol.util.TaskPool.TaskInterface;

public class Protocol {
    public final static String CONSUMER_KEY = "xcaJo32lEB9tW2LQ";
    public final static String CONSUMER_SECRET = "Q3wdWhi3LtevXtls";
    
    public static final String APP_ROOT="app_folder";
    public final static String API_HOST = "openapi.kuaipan.cn";
    
    public static String customApiHostString;
    
    public static class ProtocolType {
        //login
        public static final int REQUEST_TOKEN_PROTOCOL = 100;
        public static final int ACCESS_TOKEN_PROTOCOL = 101;
        
        //create folder
        public static final int CREATE_FOLDER_PROTOCOL = 200;
        
        //upload file
        public static final int UPLOAD_FILE_PROTOCOL = 300;
        
    }
    
    public static String getApiHost(int type) {
        if (type == ProtocolType.UPLOAD_FILE_PROTOCOL) {
            return customApiHostString;
        }
        return API_HOST;
    }
    
    public static String getHttpSuffix(int type){
        String uri="";
        switch (type) {
        case ProtocolType.REQUEST_TOKEN_PROTOCOL:
            uri = "/open/requestToken/";
            break;
        case ProtocolType.ACCESS_TOKEN_PROTOCOL:
            uri = "/open/accessToken/";
            break;
        case ProtocolType.CREATE_FOLDER_PROTOCOL:
            uri = "/1/fileops/create_folder/";
            break;
        case ProtocolType.UPLOAD_FILE_PROTOCOL:
            uri = "/1/fileops/upload_file/";
            break;
        default:
            break;
        }
        
        return uri;
    }
    
    public static String getHttpOrHttps(int type){
        String p = "http://";
        switch (type) {
        case ProtocolType.REQUEST_TOKEN_PROTOCOL:
        case ProtocolType.ACCESS_TOKEN_PROTOCOL:
            p = "https://";
            break;

        default:
            break;
        }
        
        return p;
    }
    
    public static String getPostOrGet(int type){
        String s = "GET";
        switch (type) {
        //TODO:modify for specific code
        case ProtocolType.UPLOAD_FILE_PROTOCOL:
            s = "POST";
            break;

        default:
            break;
        }
        
        return s;
    }
    
    public static void setRequestUrl(HttpRequestInfo info){
        String url = getHttpOrHttps(info.getType()) + Protocol.getApiHost(info.getType()) + getHttpSuffix(info.getType());
        info.getKuaipanURL().setUrl(url);
    }
    
    public static void setRequestQuery(Consumer consumer, TokenPair token, HttpRequestInfo info){
        TreeMap<String, String> signed_params;
        int type = info.getType();
        String location = OauthUtility.urlEncode(getHttpSuffix(type));
        Map<String, String> params = info.getUserParams();
        
        if (params != null)
            signed_params = new TreeMap<String, String>(params);
        else
            signed_params = new TreeMap<String, String>();
        
        signed_params.put("oauth_nonce", OauthUtility.generateNonce());
        signed_params.put("oauth_timestamp", Long.toString((System.currentTimeMillis()/1000)));
        signed_params.put("oauth_version", "1.0");
        signed_params.put("oauth_signature_method", "HMAC-SHA1");
        
        String signature = "";
        try {
            signature = OauthUtility.generateSignature(getPostOrGet(type), 
                    Protocol.getApiHost(info.getType()), location, signed_params, consumer, 
                    token, getHttpOrHttps(type).equals("https://"));
        } catch (NoSuchAlgorithmException e) {
        } catch (InvalidKeyException e) {
        }
        
        signed_params.put("oauth_signature", signature);
        signed_params.put("oauth_consumer_key", consumer.key);
        if (token != null)
            signed_params.put("oauth_token", token.key);
        
        String query = OauthUtility.encodeParameters(signed_params);
        
        info.getKuaipanURL().setQuery(query);
        info.getKuaipanURL().convert2Get();
    }
    
    public abstract static class BaseProtocolData implements TaskInterface {
        protected String mPath;
        public String getmPath() {
            return mPath;
        }

        public void setmPath(String mPath) {
            this.mPath = mPath;
        }

        protected int mProtocolType;
        protected TreeMap<String, String> mUserParams;

        protected Context mContext;

        public Context getmContext() {
            return mContext;
        }

        public void setmContext(Context mContext) {
            this.mContext = mContext;
        }

        protected HttpRequestCallback mRequestCallback = null;
        protected HttpRequestInfo mRequestInfo = null;

        public long mSendingTime = 0;
        public boolean isSuccess = false;
        
        protected BaseProtocolData(Context c, int protocolType, HttpRequestCallback callBack) {
            mContext = c;
            mProtocolType = protocolType;
            mRequestCallback = callBack;
            mUserParams = new TreeMap<String,String>();
            
            Protocol.customApiHostString = null;
        }
        
        public TreeMap<String, String> getUserParams() {
            return mUserParams;
        }

        public void setUserParams(TreeMap<String, String> mUserParams) {
            this.mUserParams = mUserParams;
        }

        @Override
        public void excute() {
            RequestEngine engine = RequestEngine.getInstance(mContext);
            engine.execute(this);
        }

        @Override
        public boolean isEqual(Object object) {
            return this == object;
        }

        public boolean isHttpSuccess() {
            return mRequestInfo == null ? false : mRequestInfo.getResultCode() == HttpURLConnection.HTTP_OK;
        }

        public int getProtocolType() {
            return mProtocolType;
        }

        public HttpRequestInfo getHttpRequestInfo() {
            return mRequestInfo;
        }

        public HttpRequestCallback getHttpRequestCallback() {
            return mRequestCallback;
        }

        public void setHttpRequestInfo(HttpRequestInfo info) {
            mRequestInfo = info;
        }

        public void setHttpRequestCallback(HttpRequestCallback callback) {
            mRequestCallback = callback;
        }

        public int getType() {
            return mProtocolType;
        }

        public void parseData() {
            Map<String, Object> resultParams = mRequestInfo.getResultParams();
            if(mRequestInfo.getResultCode() != KuaipanHTTPResponse.KUAIPAN_OK){
                isSuccess = false;
                return;
            }else{
                isSuccess = true;
            }
            if(resultParams != null && parse(resultParams)){
                doCallback();
            }
        }
        
        public boolean isSuccess() {
            return isSuccess;
        }

        abstract boolean parse(Map<String, Object> resultParams);

        abstract void doCallback();
    }

}
