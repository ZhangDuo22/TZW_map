package com.rabbit.tzw.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.rabbit.tzw.R;
import com.rabbit.tzw.myself.mySocketHelper;
import com.rabbit.tzw.remind.PhotoActivity;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import static android.app.Activity.RESULT_OK;
import static com.rabbit.tzw.myself.myRemind.rotateBitmap;
import static com.rabbit.tzw.myself.myRemind.compressImage;

public class RemindFragment extends Fragment implements View.OnClickListener {
    //线程池和……
    private Handler mMainHandler;
    private ExecutorService mThreadPool;

    public static final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri imageUri;
    private String img;
    private TextView mTv12;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_remind,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.remind_photo0:{
                File outputImage = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
                try {
                    if(outputImage.exists() ) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(getActivity(),"com.example.cameraalbumtest.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);break;
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if( resultCode == RESULT_OK ) {
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        //旋转图片
                        bitmap = rotateBitmap(bitmap);
                        picture.setImageBitmap(bitmap);
                        //压缩图片
                        bitmap = compressImage(bitmap,70);
                        //base64转码
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                        byte[] bb = bos.toByteArray();
                        final String image = Base64.encodeToString(bb, Base64.NO_WRAP);
                        setimage(image);
                        MyThread myThread = new MyThread();
                        myThread.start();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
    private void initData(){
        Button takePhoto = getActivity().findViewById(R.id.remind_photo0);
        picture = getActivity().findViewById(R.id.picture);
        mTv12 = getActivity().findViewById(R.id.tv_12);
        takePhoto.setOnClickListener(this);
    }

    public class MyThread extends Thread{
        @Override
        public void run() {
            String image = getimage();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("KEY", "Photo");
                jsonObject.accumulate("image", image.substring(0, 950));
                jsonObject.accumulate("condition", "first");
                mySocketHelper mSocketHelper = new mySocketHelper();
                mSocketHelper.sendDataString(jsonObject);
                image = image.substring(950);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            while (true) {
                int max = 800;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("KEY", "Photo");
                    jsonObject.accumulate("image", image.substring(0,max));
                    jsonObject.accumulate("condition", "no");
                    mySocketHelper mSocketHelper = new mySocketHelper();
                    mSocketHelper.sendDataString(jsonObject);
                    image = image.substring(max);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(image.length() - max < 40){
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("KEY", "Photo");
                        jsonObject.accumulate("image", image);
                        jsonObject.accumulate("condition", "yes");
                        mySocketHelper mSocketHelper = new mySocketHelper();
                        mSocketHelper.sendDataString(jsonObject);
                        String sRv = mSocketHelper.getDataString();
                        Log.i("张夺来巡山了",sRv);
                        Intent intent = new Intent(getActivity(), PhotoActivity.class);
                        startActivity(intent);
//                        mTv12.setText(sRv);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

        }
    }
    private void setimage(String img){
        this.img = img;
    }
    private String getimage(){
        return img;
    }
}
