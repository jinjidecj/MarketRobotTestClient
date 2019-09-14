package com.zcj.marketsockettestclient;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SocketClient client;
    TextView textView;
    EditText editTextIP;
    TextView tvStatus;
    Button btnStop;
    String area = "Area1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.start);
        textView = (TextView)findViewById(R.id.textView);
        editTextIP= (EditText) findViewById(R.id.ipEditText);
        tvStatus= (TextView) findViewById(R.id.tvStatus);
        btnStop= (Button) findViewById(R.id.btnStop);
        if(!"".equals(getIp())){
            editTextIP.setText(getIp());
        }
        initPermission();
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(client!=null){
                    client.stopSend();
                }
            }
        });
        Switch switch1 = findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    LogUtil.d("true");
                    area = "Area2";
                }else{
                    LogUtil.d("false");
                    area = "Area1";
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(textView.getText()+"\n"+"开始连接...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String ip=editTextIP.getText().toString();
                            saveIp(ip);
                            LogUtil.d(ip);
                            if(!ip.equals(""))
                                client = new SocketClient(handler,ip); // 启动客户端连接
                            else{
                                Message message=new Message();
                                message.what=3;
                                handler.sendMessage(message);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //发送String
                        // client.sendString();

                        JSONObject root = new JSONObject();
                        JSONObject data = new JSONObject();
                        try {
                            root.put(BUSINESS_TYPE,"0000");
                            data.put(ID,area);
                            root.put(DATA,data);
                            if(client!=null){
                                client.sendMessage(root.toString());
                            }else{
                                LogUtil.d("client is null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_set_success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject root = new JSONObject();
                        JSONObject data = new JSONObject();
                        try {
                            root.put(BUSINESS_TYPE,"0002");
                            data.put(STATUS,"4");
                            data.put(ERROR_DESC,"复位成功");
                            root.put(DATA,data);
                            if(client!=null){
                                client.sendMessage(root.toString());
                            }else{
                                LogUtil.d("client is null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();



            }
        });
        findViewById(R.id.btn_start_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject root = new JSONObject();
                        JSONObject data = new JSONObject();
                        try {
                            root.put(BUSINESS_TYPE,"0004");
                            data.put(STATUS,"1");
                            data.put(ERROR_DESC,"开始取货");
                            root.put(DATA,data);
                            if(client!=null){
                                client.sendMessage(root.toString());
                            }else{
                                LogUtil.d("client is null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();


            }
        });
        findViewById(R.id.btn_success_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject root = new JSONObject();
                        JSONObject data = new JSONObject();
                        try {
                            root.put(BUSINESS_TYPE,"0005");
                            data.put(STATUS,"2");
                            data.put(ERROR_DESC,"成功取货");
                            root.put(DATA,data);
                            if(client!=null){
                                client.sendMessage(root.toString());
                            }else{
                                LogUtil.d("client is null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });
        findViewById(R.id.btn_success_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject root = new JSONObject();
                        JSONObject data = new JSONObject();
                        try {
                            root.put(BUSINESS_TYPE,"0006");
                            data.put(STATUS,"3");
                            data.put(ERROR_DESC,"成功出货");
                            root.put(DATA,data);
                            if(client!=null){
                                client.sendMessage(root.toString());
                            }else{
                                LogUtil.d("client is null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });
        findViewById(R.id.btn_status4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject root = new JSONObject();
                        JSONObject data = new JSONObject();
                        try {
                            root.put(BUSINESS_TYPE,"0001");
                            data.put(STATUS,"4");
                            data.put(ERROR_DESC,"状态是复位成功");
                            root.put(DATA,data);
                            if(client!=null){
                                client.sendMessage(root.toString());
                            }else{
                                LogUtil.d("client is null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();


            }
        });
    }

    private void saveIp(String ip){
        SharedPreferences sp = getSharedPreferences("ip",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("ip",ip);
        editor.commit();
    }
    private String getIp(){
        SharedPreferences sp = getSharedPreferences("ip",MODE_PRIVATE);
        return sp.getString("ip","");
    }

    private String BUSINESS_TYPE ="businessType";
    private String DATA="data";
    private String ID="ID";
    private String STATUS="status";
    private String ERROR_DESC="errorDesc";

    private void initPermission(){
        try {
            //对于危险权限，需要申请读写文件权限
            List<String> permissionList = new ArrayList<String>();
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissionList.isEmpty()) {
                ActivityCompat.requestPermissions(MainActivity.this, permissionList.toArray(new String[permissionList.size()]), 123);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            TextView textStatus= (TextView) findViewById(R.id.textView);
            switch (msg.what) {
                case 1://连接
                    String text= (String) textStatus.getText();
                    textStatus.setText(msg.obj+"\n");
                    break;
                case 2://发送结束
                    String text1= (String) textStatus.getText();
                    textStatus.setText(text1+"\nsocket server 发送结束");
                    break;
                case 3://发送ip为空
                    String text2= (String) textStatus.getText();
                    textStatus.setText(text2+"\nip不可为空");
                    break;
                case 4://统计发送了多少个
                    tvStatus.setText(""+msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.stopSend();
    }

}
