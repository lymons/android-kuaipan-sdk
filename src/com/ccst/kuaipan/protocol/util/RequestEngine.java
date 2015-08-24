package com.ccst.kuaipan.protocol.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.ccst.kuaipan.database.Settings;
import com.ccst.kuaipan.database.Settings.Field;
import com.ccst.kuaipan.protocol.CreateDataProtocol.*;
import com.ccst.kuaipan.protocol.LoginProtocol;
import com.ccst.kuaipan.protocol.Protocol;
import com.ccst.kuaipan.protocol.Protocol.BaseProtocolData;
import com.ccst.kuaipan.protocol.Protocol.ProtocolType;
import com.ccst.kuaipan.protocol.data.HttpRequestInfo;
import com.ccst.kuaipan.protocol.data.KuaipanHTTPResponse;
import com.ccst.kuaipan.protocol.data.HttpRequestInfo.KuaipanURL;
import com.ccst.kuaipan.protocol.session.OauthSession;
import com.ccst.kuaipan.protocol.session.Session;
import com.ccst.kuaipan.tool.LogHelper;

public class RequestEngine {
    public abstract interface HttpRequestCallback {
        public abstract void onHttpResult(BaseProtocolData data);

        public abstract void onHttpStart(BaseProtocolData data);
    }

    public static TaskPool mRequestTaskPool = new TaskPool(3);

    private Context mContext;
    private static RequestEngine mRequestEngine;

    private Session mSession;

    private RequestEngine(Context c) {
        mContext = c;
        if(!Settings.getSettingBoolean(c, Field.IS_LOGIN, false)){
            mSession = new OauthSession(Protocol.CONSUMER_KEY, Protocol.CONSUMER_SECRET);
        }else{
            mSession = new OauthSession(Protocol.CONSUMER_KEY, Protocol.CONSUMER_SECRET);
            String tokenPairKey = Settings.getSettingString(c, Field.TOKENPAIR_KEY, "");
            String tokenPairSecret = Settings.getSettingString(c, Field.TOKENPAIR_SECRET, "");
            mSession.setAuthToken(tokenPairKey, tokenPairSecret);
        }
    }

    public Session getSession() {
        return mSession;
    }

    public void setSession(Session mSession) {
        this.mSession = mSession;
    }

    public static RequestEngine getInstance(Context c) {
        if (mRequestEngine == null) {
            mRequestEngine = new RequestEngine(c);
        }

        return mRequestEngine;
    }

    public void execute(BaseProtocolData d) {
            Time time = new Time();
            d.mSendingTime = time.toMillis(true);
            new HttpRequestTask().execute(d);
    }
    
    
    public void send(BaseProtocolData d) {
        createHttpRequestInfo(d);
        clearAllIdle();
        mRequestTaskPool.addTask(d);
    }
    
    private void createHttpRequestInfo(BaseProtocolData d) {
        int type = d.getProtocolType();
        HttpRequestInfo info = d.getHttpRequestInfo();
        TreeMap<String, String> userParams = d.getUserParams();
        info = new HttpRequestInfo(type, userParams);
        Protocol.setRequestUrl(info);
        d.setHttpRequestInfo(info);
        Protocol.setRequestQuery(mSession.consumer, mSession.token, info);
        
        callbackStart(d);
    }
    
    public void clearAllIdle() {
        Time time = new Time();
        time.setToNow();
        for (int i = mRequestTaskPool.mTaskList.size() - 1; i >= 0; --i) {
            TaskPool.TaskInterface inter = mRequestTaskPool.mTaskList.get(i);
            Protocol.BaseProtocolData data = (Protocol.BaseProtocolData) inter;
            if (i < mRequestTaskPool.mRunningTaskNumber
                    && (data.mSendingTime == 0)) {
                mRequestTaskPool.mTaskList.remove(i);
                --mRequestTaskPool.mRunningTaskNumber;
            }
        }
    }
    
    private void callbackStart(BaseProtocolData data) {
        if (data.getHttpRequestCallback() != null)
            data.getHttpRequestCallback().onHttpStart(data);
    }
    
    private void callbackResult(BaseProtocolData data) {
        if (data.getHttpRequestCallback() != null)
            data.getHttpRequestCallback().onHttpResult(data);
    }
    
    public class HttpRequestTask extends AsyncTask<BaseProtocolData, Void, BaseProtocolData> {
        @Override
        protected BaseProtocolData doInBackground(final BaseProtocolData... data) {
            KuaipanHTTPResponse resp = new KuaipanHTTPResponse();
            try {
                KuaipanURL url = data[0].getHttpRequestInfo().getKuaipanURL();
                LogHelper.log("################################");
                LogHelper.log("Request url"+ url);
                //TODO:add POST status, now just for GET
                if (data[0].getType() == ProtocolType.UPLOAD_FILE_PROTOCOL) {
                    File file = new File(data[0].getmPath());
                    FileInputStream  fis = new FileInputStream(file);
                    resp = KuaipanHTTPUtility.doUpload(url, fis, file.length(), new ProgressListener() {
                        
                        @Override
                        public void started() {
                            // TODO Auto-generated method stub
                            
                        }
                        
                        @Override
                        public void processing(long bytes, long total) {
                            // TODO Auto-generated method stub
                            Log.w("File Up", "Completed: " + bytes*100/total + "%");
                        }
                        
                        @Override
                        public int getUpdateInterval() {
                            // TODO Auto-generated method stub
                            return 0;
                        }
                        
                        @Override
                        public void completed() {
                            // TODO Auto-generated method stub
                            final Activity act = (Activity) data[0].getmContext();
                            act.runOnUiThread(new Runnable() {
                                
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(act, "Upload Done.", Toast.LENGTH_LONG).show();
                                    Log.w("File Up", "All Completed.");
                                }
                            });
                        }
                    });
                    fis.close();
                } else {
                    resp = KuaipanHTTPUtility.requestByGET(url);
                }
                
                Map<String, Object> result = OauthUtility.parseResponseToMap(resp);
                data[0].getHttpRequestInfo().setResultParams(result);
            }catch (Exception KuaipanIOException) {
                resp.code = KuaipanHTTPResponse.KUAIPAN_UNKNOWNED_ERROR;
            } finally {
                data[0].getHttpRequestInfo().setResultCode(resp.code);
                mRequestTaskPool.removeTask(data[0]);
            }
            
            return data[0];
        }


        @Override
        protected void onPostExecute(BaseProtocolData data) {
            if(data != null){
                data.parseData();
                callbackResult(data);
                mRequestTaskPool.removeTask(data);
            }
        }

    }
    
    public void requestToken(HttpRequestCallback callback){
        mSession.unAuth(mContext);
        send(new LoginProtocol.RequestTokenProtocol(mContext, ProtocolType.REQUEST_TOKEN_PROTOCOL, callback));
    }
    
    public void accessToken(HttpRequestCallback callback){
        send(new LoginProtocol.AccessTokenProtocol(mContext, ProtocolType.ACCESS_TOKEN_PROTOCOL, callback));
    }
    
    public void createFolder(String path, HttpRequestCallback callback){
        send(new CreateFolderProtocol(mContext, path, ProtocolType.CREATE_FOLDER_PROTOCOL, callback));
    }
    
    public void uploadFile(final String path, final HttpRequestCallback callback){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Map<String, Object> result = null;
                KuaipanHTTPResponse resp = new KuaipanHTTPResponse();
                KuaipanURL locateUrl = new KuaipanURL("http://api-content.dfs.kuaipan.cn/1/fileops/upload_locate");
                try {
                    LogHelper.log("################################");
                    LogHelper.log("Request url"+ locateUrl);
                    //TODO:add POST status, now just for GET
                    resp = KuaipanHTTPUtility.requestByGET(locateUrl);
                    result = OauthUtility.parseResponseToMap(resp);
                }catch (Exception KuaipanIOException) {
                    resp.code = KuaipanHTTPResponse.KUAIPAN_UNKNOWNED_ERROR;
                }
                
                return (String) result.get("url");
            }

            @Override
            protected void onPostExecute(String data) {
                if(data != null){
                    send(new UploadFileProtocol(mContext, data, path, ProtocolType.UPLOAD_FILE_PROTOCOL, callback));
                }
            }
        }.execute();
    }

}
