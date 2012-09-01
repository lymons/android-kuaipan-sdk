package com.ccst.kuaipan.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ccst.kuaipan.R;
import com.ccst.kuaipan.database.Settings;
import com.ccst.kuaipan.database.Settings.Field;
import com.ccst.kuaipan.protocol.CreateDataProtocol.CreateFolderProtocol;
import com.ccst.kuaipan.protocol.LoginProtocol;
import com.ccst.kuaipan.protocol.Protocol.BaseProtocolData;
import com.ccst.kuaipan.protocol.Protocol.ProtocolType;
import com.ccst.kuaipan.protocol.data.KuaipanHTTPResponse;
import com.ccst.kuaipan.protocol.util.RequestEngine;
import com.ccst.kuaipan.protocol.util.RequestEngine.HttpRequestCallback;

public class KuaipanActivity extends Activity implements OnClickListener, HttpRequestCallback{
    ProgressDialog mProgressDialog;
    TextView mLoginView;
    TextView mCreateFolderView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kuaipan_activity);
        
        initData();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        checkWhetherLogin();
    }
    
    private void checkWhetherLogin() {
        if(Settings.getSettingBoolean(this, Field.IS_LOGIN, false)){
            mLoginView.setText("You Have Login Success!");
        }else{
            mLoginView.setText("Waiting for Login");
        }
    }

    private void initData(){
        mLoginView = (TextView)findViewById(R.id.login_btn);
        mCreateFolderView = (TextView)findViewById(R.id.create_folder_btn);
        
        mLoginView.setOnClickListener(this);
        mCreateFolderView.setOnClickListener(this);
    }
    
    @Override
    public void onHttpStart(BaseProtocolData data) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Notice");
        mProgressDialog.setMessage("waiting...");
        mProgressDialog.show();
    }

    @Override
    public void onHttpResult(BaseProtocolData data) {
        mProgressDialog.dismiss();
        
        if(data.isSuccess){
            if(data.getType() == ProtocolType.REQUEST_TOKEN_PROTOCOL){
                OauthActivity.show(KuaipanActivity.this, 
                    LoginProtocol.AUTH_URL + RequestEngine.getInstance(KuaipanActivity.this).getSession().token.key);
            }else if(data.getType() == ProtocolType.CREATE_FOLDER_PROTOCOL){
                CreateFolderProtocol p = (CreateFolderProtocol)data;
                if(p.isSuccess){
                    Toast.makeText(KuaipanActivity.this, "Create Folder Success!", Toast.LENGTH_LONG).show();
                }
            }
        }else{
            Toast.makeText(this, KuaipanHTTPResponse.getHttpErrorCodeDescribtion(data.getHttpRequestInfo().getResultCode()), 
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.login_btn:
            RequestEngine.getInstance(this).requestToken(this);
            break;
        case R.id.create_folder_btn:
            RequestEngine.getInstance(this).createFolder("/ccst", this);
            break;
        default:
            break;
        }
    }

}
