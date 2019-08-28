package com.rabbit.tzw.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rabbit.tzw.Fragments.FunctionActivity;
import com.rabbit.tzw.R;
import com.rabbit.tzw.myself.CUser;
import com.rabbit.tzw.myself.mySocketHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    //线程池
    private Handler mMainHandler;
    private ExecutorService mThreadPool;
    //输入窗和登录注册
    private Button mPreRegister,mLogin;
    private EditText mEdtId,mEdtPassword;
    //客服端和自定义
    private mySocketHelper mSocketHelper;
    private CUser mMySelf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manageViewID();
        initData();
        manageHandle();
        initPermission();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_pre_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActicity.class);
                startActivity(intent);
                break;

        }
    }
    public void manageViewID(){
        mEdtId = findViewById(R.id.et_1);
        mEdtPassword = findViewById(R.id.et_2);
        mLogin = findViewById(R.id.btn_login);
        mPreRegister = findViewById(R.id.btn_pre_register);
        mLogin.setOnClickListener(this);
        mPreRegister.setOnClickListener(this);
    }
    private void manageHandle(){
        mMainHandler = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        Intent intent = new Intent(LoginActivity.this,FunctionActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Toast.makeText(LoginActivity.this,"用户名不存在,请联系Rabbit公司进行注册",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(LoginActivity.this,"无法连接服务器",Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(LoginActivity.this,"账号和密码未输入",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }
    //初始化数据
    private void initData(){
        mMySelf = new CUser();
        mThreadPool = Executors.newCachedThreadPool();
    }
    private void login(){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                JSONObject jsonObject = new JSONObject();
                mMySelf.mID = mEdtId.getText().toString().trim();
                mMySelf.mPassword = mEdtPassword.getText().toString().trim();
                try {
                    jsonObject.accumulate("KEY","Login");
                    jsonObject.accumulate("username",mMySelf.mID);
                    jsonObject.accumulate("password",mMySelf.mPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if((!mMySelf.mID.equals("") && !mMySelf.mPassword.equals(""))){
                    if((mSocketHelper = new mySocketHelper()).checkSocket()){
                        mSocketHelper.sendDataString(jsonObject);
                        String sRcv = mSocketHelper.getDataString();
                        if(sRcv.equals("return_success")){
                            msg.what = 0;
                        }else if(sRcv.equals("return_id_empty")){
                            msg.what = 1;
                        }else if(sRcv.equals("return_fail")){
                            msg.what = 2;
                        }
                        mSocketHelper.closeSocket();
                    }else {
                        msg.what = 3;
                    }
                }else{
                    msg.what = 4;
                }
                mMainHandler.sendMessage(msg);
            }
        });
    }
//    动态权限代码

    private void initPermission() {
        String permissions[] = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.CAMERA,
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
                Log.e("--------->", "没有权限");
            } else {

                Log.e("--------->", "已经被授权");
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // 授权被允许
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("-------->", "授权请求被允许");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.e("-------->", "授权请求被拒绝");
                }
                return;
            }
        }
    }
}
