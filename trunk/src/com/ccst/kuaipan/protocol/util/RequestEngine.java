package com.ccst.kuaipan.protocol.util;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Time;

import com.ccst.kuaipan.database.Settings;
import com.ccst.kuaipan.database.Settings.Field;
import com.ccst.kuaipan.protocol.CreateDataProtocol.CreateFolderProtocol;
import com.ccst.kuaipan.protocol.LoginProtocol;
import com.ccst.kuaipan.protocol.Protocol;
import com.ccst.kuaipan.protocol.Protocol.BaseProtocolData;
import com.ccst.kuaipan.protocol.Protocol.ProtocolType;
import com.ccst.kuaipan.protocol.data.HttpRequestInfo;
import com.ccst.kuaipan.protocol.data.KuaipanHTTPResponse;
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
        TreeMap<String, String> userParams = d.getUserParams();
        HttpRequestInfo info = new HttpRequestInfo(type, userParams);
        Protocol.setRequestUrl(info);
        Protocol.setRequestQuery(mSession.consumer, mSession.token, info);

        d.setHttpRequestInfo(info);
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
        protected BaseProtocolData doInBackground(BaseProtocolData... data) {
            KuaipanHTTPResponse resp = new KuaipanHTTPResponse();
            try {
                LogHelper.log("################################");
                LogHelper.log("Request url"+data[0].getHttpRequestInfo().getKuaipanURL());
                //TODO:add POST status, now just for GET
                resp = KuaipanHTTPUtility.requestByGET(data[0].getHttpRequestInfo().getKuaipanURL());
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

}
