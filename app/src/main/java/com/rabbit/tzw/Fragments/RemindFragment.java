package com.rabbit.tzw.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.rabbit.tzw.R;
import com.rabbit.tzw.myself.mySocketHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;

import static android.app.Activity.RESULT_OK;

public class RemindFragment extends Fragment implements View.OnClickListener {
    //线程池和……
    private Handler mMainHandler;
    private ExecutorService mThreadPool;

    public static final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri imageUri;

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
                startActivityForResult(intent, TAKE_PHOTO);
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
                        picture.setImageBitmap(bitmap);
                        //压缩图片
                        bitmap = compressImage(bitmap,100);
                        //base64转码
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                        byte[] bb = bos.toByteArray();
                        final String image = Base64.encodeToString(bb, Base64.NO_WRAP);
                        request("http://api.tianapi.com/txapi/imglajifenlei/?key=f39cfc89e95e7dfe44a38368c7d74163&img=data:image/jpeg;",image);
//                        String jsonResult = request(httpUrl);
//                        System.out.println(jsonResult);
//                        new Thread(new Runnable(){
//                            @Override
//                            public void run() {
//                                JSONObject jsonObject = new JSONObject();
//                                try {
//                                    jsonObject.accumulate("KEY","Login");
//                                    jsonObject.accumulate("username",image);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                mySocketHelper mSocketHelper = new mySocketHelper();
//                                mSocketHelper.sendDataString(jsonObject);
//                            }
//                        }).start();
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
        takePhoto.setOnClickListener(this);
    }
    //压缩图片方法


    private Bitmap compressImage(Bitmap image, int size) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 50;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > size) {
            // 重置baos即清空baos
            baos.reset();
            // 每次都减少10
            options -= 10;
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);

        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }
//    强大的打印功能
//    public static void i(String tag, String msg) {  //信息太长,分段打印
//        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
//        //  把4*1024的MAX字节打印长度改为2001字符数
//        int max_str_length = 2001 - tag.length();
//        //大于4000时
//        while (msg.length() > max_str_length) {
//            Log.i(tag, msg.substring(0, max_str_length));
//            msg = msg.substring(max_str_length);
//        }
//        //剩余部分
//        Log.i(tag, msg);
//    }

    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
