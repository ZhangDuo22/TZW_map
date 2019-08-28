package com.rabbit.tzw.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rabbit.tzw.R;
import com.rabbit.tzw.myself.CUser;
import com.rabbit.tzw.myself.mySocketHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActicity extends AppCompatActivity implements View.OnClickListener {
    //线程池和……
    private Handler mMainHandler;
    private ExecutorService mThreadPool;
    private CUser mMyself;
    private mySocketHelper mySocket;
    //文本框和按键
    private EditText mUsername,mPhone,mEdtSex,mEdtPwd,mEdtDtmPwd;
    private Button mRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acticity);
        registerId();
        initregister();
        registerHandle();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_register:
                register();
                break;
        }
    }
    public void registerId(){
        mUsername = findViewById(R.id.et_3);
        mPhone = findViewById(R.id.et_4);
        mEdtSex = findViewById(R.id.et_5);
        mEdtPwd = findViewById(R.id.et_6);
        mEdtDtmPwd = findViewById(R.id.et_7);
        mRegister = findViewById(R.id.btn_register);
        mRegister.setOnClickListener(this);
    }
    private void registerHandle(){
        mMainHandler = new Handler(){
            public void handleMessage(Message message){
                switch (message.what){
                    case 0:{
                        Toast.makeText(RegisterActicity.this,"输入内容不能为空，都是必填项",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 1:{
                        Toast.makeText(RegisterActicity.this,"手机号码错误，请重试",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 2:{
                        Toast.makeText(RegisterActicity.this,"性别错误，请重试",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 3:{
                        Toast.makeText(RegisterActicity.this,"输入密码不一致，请重试",Toast.LENGTH_LONG).show();
                        break;
                    }
                    case 4:{
                        Toast.makeText(RegisterActicity.this,"无法连接服务器",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 5:{
                        Toast.makeText(RegisterActicity.this,"感谢注册Rabbit公司提供账号",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActicity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 6:{
                        Toast.makeText(RegisterActicity.this,"密码长度必须大于7位，请重试",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 7:{
                        Toast.makeText(RegisterActicity.this,"用户名已存在，请重试",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 8:{
                        Toast.makeText(RegisterActicity.this,"服务器繁忙，无法注册，请稍后重试",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        };
    }

    private void initregister(){
        mMyself = new CUser();
        mThreadPool = Executors.newCachedThreadPool();
    }

    private void register(){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                JSONObject json = new JSONObject();
                mMyself.mUser = mUsername.getText().toString().trim();
                mMyself.mNumber = mPhone.getText().toString().trim();
                mMyself.mSex = mEdtSex.getText().toString().trim();
                mMyself.mRegpwd = mEdtPwd.getText().toString().trim();
                mMyself.mRegDtmpwd = mEdtDtmPwd.getText().toString().trim();
                try{
                    json.accumulate("KEY","Register");
                    json.accumulate("username",mMyself.mUser);
                    json.accumulate("password",mMyself.mRegpwd);
                    json.accumulate("phone",mMyself.mNumber);
                    json.accumulate("sex",mMyself.mSex);
                    System.out.println(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if((!mMyself.mUser.equals("") && (!mMyself.mNumber.equals("") && (!mMyself.mSex.equals("") && (!mMyself.mRegpwd.equals("") && (!mMyself.mRegDtmpwd.equals(""))))))){
                    if(mMyself.mNumber.length() == 11){
                        if(mMyself.mSex.equals("男") || mMyself.mSex.equals("女")) {
                            if(mMyself.mRegpwd.equals(mMyself.mRegDtmpwd)){
                                if(mMyself.mRegpwd.length() >= 8){
                                    if((mySocket = new mySocketHelper()).checkSocket()){
                                        mySocket.sendDataString(json);
                                        String rrcv = mySocket.getDataString();
                                        if(rrcv.equals("user_name_occupied")){
                                            //用户名字已存在
                                            message.what = 7;
                                        }else {
                                            if (rrcv.equals("return_register_success")){
                                                message.what = 5;
                                            }else {
                                                message.what = 8;
                                            }
                                        }
                                    }else {
                                        //无法连接服务器
                                        message.what = 4;
                                    }
                                }else {
                                    //密码长度必须大于8
                                    message.what = 6;
                                }
                            }else {
                                //输入密码不一致
                                message.what = 3;
                            }
                        }else{
                            //性别错误
                            message.what = 2;
                        }
                    }else{
                        //手机号码错误
                        message.what = 1;
                    }
                }else {
                    //输入内容不能为空，都是必填项
                    message.what = 0;
                }
                mMainHandler.sendMessage(message);
            }
        });
    }
}
