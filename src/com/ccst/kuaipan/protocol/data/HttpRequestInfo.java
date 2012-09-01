package com.ccst.kuaipan.protocol.data;

import java.util.Map;
import java.util.TreeMap;

public class HttpRequestInfo {
    private Integer mType;
    private Map<String, String> mUserParams;
    private Map<String, Object> mResultParams;
    private KuaipanURL mKuaipanURL;
    private int mResultCode;

    public HttpRequestInfo(int type, TreeMap<String, String> params) {
        setType(type);
        setUserParams(params);
        mKuaipanURL = new KuaipanURL();
        mResultParams = new TreeMap<String, Object>();
    }
    
    public KuaipanURL getKuaipanURL() {
        return mKuaipanURL;
    }

    public void setKuaipanURL(KuaipanURL mKuaipanURL) {
        this.mKuaipanURL = mKuaipanURL;
    }

    public Map<String, String> getUserParams() {
        return mUserParams;
    }

    public void setUserParams(Map<String, String> mProtocolParams) {
        this.mUserParams = mProtocolParams;
    }

    public Map<String, Object> getResultParams() {
        return mResultParams;
    }

    public void setResultParams(Map<String, Object> mResultParams) {
        this.mResultParams = mResultParams;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public int getType() {
        return mType;
    }
    
     public void setResultCode(int code) {
        this.mResultCode = code;
    }

    public int getResultCode() {
        return mResultCode;
    }

    public static class KuaipanURL {
        public String url;
        public String query;
        public Map<String, String> headers; 
        
        public KuaipanURL(){
            this(null);
        }
        
        public KuaipanURL(String url) {
            this(url, null);
        }
        
        public KuaipanURL(String url, String query) {
            this(url, query, null);
        }
        
        public KuaipanURL(String url, String query, Map<String, String> headers) {
            this.url = url;
            this.query = query;
            this.headers = headers;
        }
        
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public void convert2Get() {
            url = queryByGetUrl();
            query = null;
        }
        
        public String queryByGetUrl() {
            if (query != null)
                return  url + "?" + query;
            return url;
        }
        
        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("\nurl=");
            buf.append(url);
            buf.append("\nquery=");
            if (query != null)
                buf.append(query);
            buf.append("\nheaders=");
            if (headers != null)
                buf.append(headers.toString());
            return buf.toString();
        }
    }

}
