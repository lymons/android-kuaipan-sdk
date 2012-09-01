package com.ccst.kuaipan.protocol.session;

import android.content.Context;

import com.ccst.kuaipan.database.Settings;
import com.ccst.kuaipan.database.Settings.Field;
import com.ccst.kuaipan.protocol.data.AccessToken;
import com.ccst.kuaipan.protocol.data.Consumer;
import com.ccst.kuaipan.protocol.data.RequestToken;
import com.ccst.kuaipan.protocol.data.TokenPair;
import com.ccst.kuaipan.protocol.exception.KuaipanAuthExpiredException;


public abstract class Session {
	
	public Consumer consumer;
	public TokenPair token;
	
	public Session(String key, String secret) {
		this.consumer = new Consumer(key, secret);
		this.token = null;
	}
	
	public void setAuthToken(String key, String secret) {
		this.token = new AccessToken(key, secret);
	}
	
	public void setTempToken(String key, String secret) {
		this.token = new RequestToken(key, secret);
	}
	
	public boolean isAuth() { 
		if (this.token == null)
			return false;
		return (this.token instanceof AccessToken);
	}
	
	public void unAuth(Context c) {
	    Settings.setSettingBoolean(c, Field.IS_LOGIN, false);
		this.token = null;
	}
	
	public void assertAuth() throws KuaipanAuthExpiredException {
		if (! isAuth())
			throw new KuaipanAuthExpiredException();
	}
	
}
