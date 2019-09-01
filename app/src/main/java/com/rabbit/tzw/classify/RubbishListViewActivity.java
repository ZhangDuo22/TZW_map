package com.rabbit.tzw.classify;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;
import com.rabbit.tzw.R;
import com.rabbit.tzw.myself.TTSUtility;
import com.rabbit.tzw.myself.mySocketHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RubbishListViewActivity extends AppCompatActivity {
    //线程池
    private Handler mMainHandler;
    private ExecutorService mThreadPool;
    //客服端和自定义
    private mySocketHelper mSocketHelper;
    //接收的信息
    private String information;
    //图像和三个信息框
    private TextView infor0,infor1,infor2;
    private ImageView photo;
    //自定义方法
    private String infordata1;
    private String infordata2;
    private int number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubbish_list_view);
        Intent intent = getIntent();
        information = intent.getStringExtra("editsearch");
        manageViewID();
        initData();
        search(information);
        manageHandle();
    }
    //初始化数据
    private void initData(){
        mThreadPool = Executors.newCachedThreadPool();
    }
    private void search(final String information){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                JSONObject jsonObject = new JSONObject();
                try{
                   jsonObject.accumulate("KEY","Search");
                   jsonObject.accumulate("esitSearch",information);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if((!information.equals(""))){
                    if((mSocketHelper= new mySocketHelper()).checkSocket()){
                        mSocketHelper.sendDataString(jsonObject);
                        String sRcvv = mSocketHelper.getDataString();
                        int len = sRcvv.length();
                        if (sRcvv.equals("mistake")){
                            msg.what = 10;
                        } else {
                            if (len == 1){
                                switch (sRcvv){
                                    case "0":
                                        msg.what = 2;break;
                                    case "1":
                                        msg.what = 3;break;
                                    case "2":
                                        msg.what = 4;break;
                                    case "3":
                                        msg.what = 5;break;
                                }
                            } else {
                                switch(sRcvv.charAt(0))
                                {
                                    case '0':
                                        setinformation(sRcvv.substring(1));
                                        msg.what = 6;break;
                                    case '1':
                                        setinformation(sRcvv.substring(1));
                                        msg.what = 7;break;
                                    case '2':
                                        setinformation(sRcvv.substring(1));
                                        msg.what = 8;break;
                                    case '3':
                                        setinformation(sRcvv.substring(1));
                                        msg.what = 9;break;
                                }
                            }
                        }
                        mSocketHelper.closeSocket();
                    }else {
                        msg.what = 1;
//                        连接不到服务器
                    }
                }else {
                    msg.what = 0;
                    //什么也没有输入
                }
                mMainHandler.sendMessage(msg);
            }
        });
    }
    public void manageViewID(){
        infor0 = findViewById(R.id.tv_rubbish_information0);
        infor1 = findViewById(R.id.tv_rubbish_information1);
        infor2 = findViewById(R.id.tv_rubbish_information2);
        photo = findViewById(R.id.iv_image);
    }
    private void manageHandle(){
        mMainHandler = new Handler(){
          public void handleMessage(Message msg){
              switch (msg.what){
                  case 0:
                      TTSUtility.getInstance(getApplicationContext()).speaking("你什么也没有输入，搜索个球");
                      infor0.setText("小提示");
                      infor1.setText("你什么也没有输入,搜索个球");
                      photo.setImageResource(R.drawable.help_rubbish);break;
                  case 1:
                      TTSUtility.getInstance(getApplicationContext()).speaking("无法连接服务器");
                      infor0.setText("小提示");
                      infor1.setText("无法连接服务器");
                      photo.setImageResource(R.drawable.help_rubbish);break;
                  case 2:
                      TTSUtility.getInstance(getApplicationContext()).speaking("系统预测为可回收垃圾");
                      infor0.setText("小提示");
                      infor1.setText("您输入的是一个泛类");
                      infor2.setText("系统预测为可回收垃圾");
                      photo.setImageResource(R.drawable.recycyle_rubbish);break;
                  case 3:
                      TTSUtility.getInstance(getApplicationContext()).speaking("系统预测为有害垃圾");
                      infor0.setText("小提示");
                      infor1.setText("您输入的是一个泛类");
                      infor2.setText("系统预测为有害垃圾");
                      photo.setImageResource(R.drawable.harm_rubbish);break;
                  case 4:
                      TTSUtility.getInstance(getApplicationContext()).speaking("系统预测为湿垃圾");
                      infor0.setText("小提示");
                      infor1.setText("您输入的是一个泛类");
                      infor2.setText("系统预测为湿垃圾");
                      photo.setImageResource(R.drawable.wet_rubbish);break;
                  case 5:
                      TTSUtility.getInstance(getApplicationContext()).speaking("系统预测为干垃圾");
                      infor0.setText("小提示");
                      infor1.setText("您输入的是一个泛类");
                      infor2.setText("系统预测为干垃圾");
                      photo.setImageResource(R.drawable.dry_rubbish);break;
                  case 6:
                      TTSUtility.getInstance(getApplicationContext()).speaking("可回收垃圾");
                      infor0.setText("可回收垃圾");
                      infor1.setText(getInfor1());
                      infor2.setText(getInfor2());
                      photo.setImageResource(R.drawable.recycyle_rubbish);break;
                  case 7:
                      TTSUtility.getInstance(getApplicationContext()).speaking("有害垃圾");
                      infor0.setText("有害垃圾");
                      infor1.setText(getInfor1());
                      infor2.setText(getInfor2());
                      photo.setImageResource(R.drawable.harm_rubbish);break;
                  case 8:
                      TTSUtility.getInstance(getApplicationContext()).speaking("湿垃圾");
                      infor0.setText("湿垃圾");
                      infor1.setText(getInfor1());
                      infor2.setText(getInfor2());
                      photo.setImageResource(R.drawable.wet_rubbish);break;
                  case 9:
                      TTSUtility.getInstance(getApplicationContext()).speaking("干垃圾");
                      infor0.setText("干垃圾");
                      infor1.setText(getInfor1());
                      infor2.setText(getInfor2());
                      photo.setImageResource(R.drawable.dry_rubbish);break;
                  case 10:
                      TTSUtility.getInstance(getApplicationContext()).speaking("不是寻常物品，请在试一下下");
                      infor0.setText("小提示");
                      infor1.setText("输入的内容不是寻常物品");
                      infor2.setText("请重新输入");
                      photo.setImageResource(R.drawable.mistake);break;
              }
          }
        };
    }
    private void setinformation(String word){
        for (int i = 0; i<word.length(); i++){
            if (word.charAt(i) == '；'||word.charAt(i) == '。'){
                number = i;
                this.infordata1 = word.substring(0,number);
                this.infordata2 = word.substring(number+1);
                System.out.println(infordata1);
                System.out.println(infordata2);
                break;
            }
        }
    }
    private String getInfor1(){
        return infordata1;
    }
    private String getInfor2(){
        return infordata2;
    }
}
