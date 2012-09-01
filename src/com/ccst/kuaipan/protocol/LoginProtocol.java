package com.ccst.kuaipan.protocol;

import java.util.Map;

import android.content.Context;

import com.ccst.kuaipan.database.Settings;
import com.ccst.kuaipan.database.Settings.Field;
import com.ccst.kuaipan.protocol.Protocol.BaseProtocolData;
import com.ccst.kuaipan.protocol.util.RequestEngine;
import com.ccst.kuaipan.protocol.util.RequestEngine.HttpRequestCallback;


public class LoginProtocol {
    public final static String AUTH_URL = "https://www.kuaipan.cn/api.php?ac=open&op=authorise&oauth_token=";
    
    public static class RequestTokenProtocol extends BaseProtocolData{
        public String key;
        public String secret;
        public RequestTokenProtocol(Context c, int protocolType,
                HttpRequestCallback callBack) {
            super(c, protocolType, callBack);
        }

        @Override
        void doCallback() {
            RequestEngine.getInstance(mContext).getSession().setTempToken(key, secret);
        }

        @Override
        boolean parse(Map<String, Object> resultParams) {
            key = (String) resultParams.get("oauth_token");
            secret = (String) resultParams.get("oauth_token_secret");
            return true;
        }
        
    }
    
    public static class AccessTokenProtocol extends BaseProtocolData{
        public String key;
        public String secret;
        public String charged_dir;
        public String user_id;
        
        public AccessTokenProtocol(Context c, int protocolType,
                HttpRequestCallback callBack) {
            super(c, protocolType, callBack);
        }

        @Override
        void doCallback() {
            RequestEngine.getInstance(mContext).getSession().setAuthToken(key, secret);
            Settings.setSettingBoolean(mContext, Field.IS_LOGIN, true);
            Settings.setSettingString(mContext, Field.TOKENPAIR_KEY, key);
            Settings.setSettingString(mContext, Field.TOKENPAIR_SECRET, secret);
        }

        @Override
        boolean parse(Map<String, Object> resultParams) {
            key = (String) resultParams.get("oauth_token");
            secret = (String) resultParams.get("oauth_token_secret");
            charged_dir = (String) resultParams.get("charged_dir");
            //user_id = (String) resultParams.get("user_id");
            return true;
        }
        
    }
    
}
