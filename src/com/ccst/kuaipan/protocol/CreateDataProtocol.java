package com.ccst.kuaipan.protocol;

import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;

import com.ccst.kuaipan.protocol.Protocol.BaseProtocolData;
import com.ccst.kuaipan.protocol.data.HttpRequestInfo;
import com.ccst.kuaipan.protocol.data.HttpRequestInfo.KuaipanURL;
import com.ccst.kuaipan.protocol.util.OauthUtility;
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
    
    public static class UploadFileProtocol extends BaseProtocolData{
        public boolean isSuccess = false;
        public String fileID;
        
        public UploadFileProtocol(Context c, String locate, String path, int protocolType,
                HttpRequestCallback callBack) {
            super(c, protocolType, callBack);
            mPath = path;
            
            getUserParams().put("overwrite", "true");
            getUserParams().put("root", Protocol.APP_ROOT);
            String[] component = path.split("/");
            String savePathString = "/" + component[component.length-1];
            getUserParams().put("path", savePathString);
            
            if (locate.startsWith("http://")) {
                locate = locate.substring(7);
            }
            if (locate.startsWith("https://")) {
                locate = locate.substring(8);
            }
            int end = locate.length()-1;
            while (end > 0 && locate.charAt(end) == '/') {
                end --;
            }
            if (end > 0) {
                locate = locate.substring(0, end+1);
            }
            Protocol.customApiHostString = locate;
        }

        @Override
        boolean parse(Map<String, Object> resultParams) {
//            String msg = (String)resultParams.get("msg");
//            if(msg.equals("ok")){
//                isSuccess = true;
//            }
            isSuccess = true;
            fileID = (String)resultParams.get("file_id");
            return true;
        }

        @Override
        void doCallback() {
            
        }
    }
    
    
}
