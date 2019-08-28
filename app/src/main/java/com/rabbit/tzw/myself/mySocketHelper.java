package com.rabbit.tzw.myself;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class mySocketHelper {
//    private  static final String LOGIN_ADDR = "172.23.128.234";//服务器地址
    private  static final String LOGIN_ADDR = "47.103.29.26";//服务器地址
    private  static final int LOGIN_PORT = 8888;//用户服务器端口号

    private BufferedReader br = null;
    private OutputStream os = null;
    private Socket socket;

    public mySocketHelper() {
        try {
            System.out.println("你链接了吗");
            socket = new Socket(LOGIN_ADDR,LOGIN_PORT);

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public mySocketHelper(int port){
        try {
            socket = new Socket(LOGIN_ADDR, port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public mySocketHelper(String addr, int port){
        try {
            socket = new Socket(addr, port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean checkSocket(){
        if(socket == null){
            System.out.println(socket+"sdf");
            return true;
        }
        System.out.println(socket);
        return true;

    }
    public boolean sendDataString(JSONObject src)
    {
        try {
            if(os != null)
            {
                os.write((src+"\n").getBytes("utf-8"));
                os.flush();
                return true;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

//    public boolean sendDataString(Object src)
//    {
//        return sendDataString(src);
//    }
    public String getDataString()
    {
        try {
            if(br != null){
                String data =  br.readLine().toString().trim();
                return data;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public boolean closeSocket()
    {
        try {
            if(br != null)
                br.close();
            if(os != null)
                os.close();
            if(socket != null)
                socket.close();
            return true;
        }
        catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }
}
