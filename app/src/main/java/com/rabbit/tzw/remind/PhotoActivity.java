package com.rabbit.tzw.remind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.rabbit.tzw.R;
import com.rabbit.tzw.myself.TTSUtility;

public class PhotoActivity extends AppCompatActivity {
    //图像和三个信息框
    private TextView infor0,infor1,infor2;
    private ImageView photo;
    //自定义方法
    private String infordata1;
    private String infordata2;
    private int number;
    //接收的信息
    private String information;
    private char delect0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Intent intent = getIntent();
        information = intent.getStringExtra("Photo");
        delect0 = information.charAt(0);
        setinformation(information.substring(1));
        manageViewID();
        handleMessage(delect0);
        TTSUtility.getInstance(this).speaking(information.substring(1));
    }
    public void handleMessage(char msg){
        switch (msg){
            case '0':
                infor0.setText("可回收垃圾");
                infor1.setText(getInfor1());
                infor2.setText(getInfor2());
                photo.setImageResource(R.drawable.recycyle_rubbish);break;
            case '1':
                infor0.setText("有害垃圾");
                infor1.setText(getInfor1());
                infor2.setText(getInfor2());
                photo.setImageResource(R.drawable.harm_rubbish);break;
            case '2':
                infor0.setText("湿垃圾");
                infor1.setText(getInfor1());
                infor2.setText(getInfor2());
                photo.setImageResource(R.drawable.wet_rubbish);break;
            case '3':
                infor0.setText("干垃圾");
                infor1.setText(getInfor1());
                infor2.setText(getInfor2());
                photo.setImageResource(R.drawable.dry_rubbish);break;
        }
    }
    public void manageViewID(){
        infor0 = findViewById(R.id.tv_photo_information0);
        infor1 = findViewById(R.id.tv_photo_information1);
        infor2 = findViewById(R.id.tv_photo_information2);
        photo = findViewById(R.id.iv_photo_image);
    }
    public void setinformation(String word){
        for (int i = 0; i<word.length(); i++){
            if (word.charAt(i) == '，'){
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
