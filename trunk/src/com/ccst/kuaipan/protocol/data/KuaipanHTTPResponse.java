package com.ccst.kuaipan.protocol.data;

import com.ccst.kuaipan.protocol.data.HttpRequestInfo.KuaipanURL;

public class KuaipanHTTPResponse {
    public final static int NET_ERROR = 110;
	
	public final static int KUAIPAN_OK = 200;
	public final static int KUAIPAN_LOGICAL_ERROR = 202;
	
	public final static int KUAIPAN_BAD_REQUEST_ERROR = 400;
	public final static int KUAIPAN_AUTHORIZATION_ERROR = 401;
	public final static int KUAIPAN_FOBBIDEN_ERROR = 403;
	public final static int KUAIPAN_NOT_FOUND_ERROR = 404;
	public final static int KUAIPAN_TOO_MANY_FILES_ERROR = 406;
	public final static int KUAIPAN_TOO_LARGE_ERROR = 413;
	
	public final static int KUAIPAN_SERVER_ERROR = 500;	
	public final static int KUAIPAN_OVER_SPACE_ERROR = 507;
	public final static int KUAIPAN_UNKNOWNED_ERROR = 578;
	
	public final static String MSG_CONSUMER_EXPIRED = "bad consumer key";
	public final static String MSG_AUTHORIZATION_EXPIRED = "authorization expired";
	
	public int code=KUAIPAN_UNKNOWNED_ERROR;
	public String content;
	public KuaipanURL url;
	
	public static String getHttpErrorCodeDescribtion(int code){
	    String errorDescribtion = "";
	    switch (code) {
        case NET_ERROR:
            errorDescribtion = "Net Error!";
            break;
        case KUAIPAN_LOGICAL_ERROR:
            errorDescribtion = "Logical Error!";
            break;
        case KUAIPAN_BAD_REQUEST_ERROR:
            errorDescribtion = "Bad Request Error!";
            break;
        case KUAIPAN_AUTHORIZATION_ERROR:
            errorDescribtion = "Authorization Error!";
            break;
        case KUAIPAN_FOBBIDEN_ERROR:
            errorDescribtion = "Fobbiden Error!";
            break;
        case KUAIPAN_TOO_MANY_FILES_ERROR:
            errorDescribtion = "Too Many Files Error!";
            break;
        case KUAIPAN_TOO_LARGE_ERROR:
            errorDescribtion = "Too Large Error!";
            break;
        case KUAIPAN_SERVER_ERROR:
            errorDescribtion = "Server Error!";
            break;
        case KUAIPAN_OVER_SPACE_ERROR:
            errorDescribtion = "Over Space Error!";
            break;
        case KUAIPAN_UNKNOWNED_ERROR:
            errorDescribtion = "Unknow Error!";
            break;
        default:
            errorDescribtion = "Unknow Error!";
            break;
        }
	    
	    return errorDescribtion;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("code=");
		buf.append(Integer.toString(code));
		buf.append("\n");		
		if (content != null) {
			buf.append("body=");
			buf.append(content);
			buf.append("\n");
		}
		return buf.toString();
	}
}
