package com.rabbit.tzw.myself;

import android.app.Application;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(MyApplication.this, SpeechConstant.APPID + "=5d5b3eb8");//=号后面写自己应用的APPID
    }
}
