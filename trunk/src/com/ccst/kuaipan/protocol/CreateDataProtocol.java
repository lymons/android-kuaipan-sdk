package com.ccst.kuaipan.protocol;

import java.util.Map;

import android.content.Context;

import com.ccst.kuaipan.protocol.Protocol.BaseProtocolData;
import com.ccst.kuaipan.protocol.util.RequestEngine.HttpRequestCallback;

//add obtain data protocol here
public class CreateDataProtocol {
    
    public static class CreateFolderProtocol extends BaseProtocolData{
        public boolean isSuccess = false;
        
        public CreateFolderProtocol(Context c, String path, int protocolType,
                HttpRequestCallback callBack) {
            super(c, protocolType, callBack);
            getUserParams().put("root", Protocol.APP_ROOT);
            getUserParams().put("path", path);
        }

        @Override
        boolean parse(Map<String, Object> resultParams) {
            String msg = (String)resultParams.get("msg");
            if(msg.equals("ok")){
                isSuccess = true;
            }
            return true;
        }

        @Override
        void doCallback() {
        }
    }
    
    
}
