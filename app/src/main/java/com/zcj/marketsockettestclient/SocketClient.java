package com.zcj.marketsockettestclient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.System.currentTimeMillis;

public class SocketClient extends Socket {

    private static final String SERVER_IP = "192.168.1.111"; // 服务端IP
    private String serverIp="";
    private static final int SERVER_PORT = 8899; // 服务端端口
    private static final String TAG="SocketClient";
    private Socket client;

    private FileInputStream fis;

    private DataOutputStream dos;
    boolean stopFlag = true;

    private int i = 0;
    private final int totalRound = 1000000000;//4000*6;

    private Handler handler;
    private String BUSINESS_TYPE ="businessType";
    private String DATA="data";
    private String ID="ID";
    private String STATUS="status";
    private String ERROR_DESC="errorDesc";
    private String receiveCode = "";
    /**
     * 构造函数<br/>
     * 与服务器建立连接
     * @throws Exception
     */
    public SocketClient(Handler handler) throws Exception {
        super(SERVER_IP, SERVER_PORT);
        Log.d(TAG, "SocketClient: in");
        this.handler = handler;
        this.client = this;
        System.out.println("Cliect[port:" + client.getLocalPort() + "] 成功连接服务端");
        Log.d(TAG, "SocketClient: "+"Cliect[port:" + client.getLocalPort() + "] 成功连接服务端");
    }
    /**
     * 构造函数<br/>
     * 与服务器建立连接
     * @throws Exception
     */
    public SocketClient(Handler handler,String ip) throws Exception {
        super(ip, SERVER_PORT);
        this.handler = handler;
        this.client = this;
        this.serverIp=ip;
        System.out.println("Cliect[port:" + client.getLocalPort() + "] 成功连接服务端");
        Log.d(TAG, "SocketClient: "+"Cliect[port:" + client.getLocalPort() + "] 成功连接服务端");

        dos = new DataOutputStream(client.getOutputStream());
        Message msg = new Message();
        msg.what = 1;
        msg.obj="SocketClient: "+"Cliect[port:" + client.getLocalPort() + "] 成功连接服务端";
        handler.sendMessage(msg);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream dis;
                PrintWriter printWriter=null;
                try {
                    dis = new DataInputStream(client.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream(),"GBK"));
                    printWriter= new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"GBK")),true);
                    // 开始接收文件
                    byte[] bytes = new byte[1024];
                    while(stopFlag){
                        String result="";
                        while (stopFlag&&(result=br.readLine())!=null){
                            //处理接收到的消息
                            result= getReadStringExclude(result);
                            if("".equals(result)){
                                LogUtil.d("接收数据解析失败。");
                            }else{
                                //处理接收到的消息
                                JSONObject jsonObject = new JSONObject(result);
                                receiveCode = jsonObject.getString("businessType");
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject root = new JSONObject();
                                    JSONObject data = new JSONObject();
                                    try {
                                        root.put(BUSINESS_TYPE,"0024");
                                        data.put("businessTypeGot",receiveCode);
                                        root.put(DATA,data);
                                        if(client!=null){
                                            sendMessage(root.toString());
                                        }else{
                                            LogUtil.d("client is null");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    LogUtil.d("receiveString: finally");
                }
            }
        }).start();
    }
    private String getWriteString(String content){
        return "#!"+content+"&"/*+"\r"*/;
    }
    private String getReadStringExclude(String content){
        int startIndex = content.indexOf("#!");
        int endIndex = content.indexOf("&");
        if(startIndex<0 || endIndex<0){
            return "";
        }
        if(startIndex>endIndex){
            return "";
        }
        LogUtil.d(content.substring(startIndex+2,endIndex));
        return content.substring(startIndex+2,endIndex);
    }


    //旧版本的向服务端传输文件函数
    public void oldSendFileVersion(String path) throws Exception{
        try {

            System.out.println("path:"+path);
            File file = new File(path);
            if(file.exists()) {
                fis = new FileInputStream(file);
                dos = new DataOutputStream(client.getOutputStream());

                // 文件名和长度
                /*dos.writeUTF(file.getName());
                dos.flush();
                dos.writeLong(file.length());
                dos.flush();*/

                // 开始传输文件
                //  System.out.println("======== 开始传输文件 ========");
                long startTime = System.currentTimeMillis();
                // System.out.println("开始时间："+ startTime);
                byte[] bytes = new byte[1024];
                int length = 0;
                long progress = 0;

                while((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    progress += length;
                    // System.out.print("| " + (100*progress/file.length()) + "% |");
                }
                Log.d(TAG, "单次传输时间: "+((System.currentTimeMillis() - startTime)+"ms"));
                //  System.out.println("传输时间："+ (System.currentTimeMillis() - startTime));
                // System.out.println("======== 文件传输成功 ========");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           /* if(fis != null)
                fis.close();
            if(dos != null)
                dos.close();
            client.close();*/
        }
    }

    boolean  flag = true;

    public void sendMessage(String message){
        LogUtil.d(message);
        try {
            String mess=addThingToMessage(message);
            dos.writeUTF(mess);
            dos.flush();

            Message msg = new Message();
            msg.what = 1;
            msg.obj=mess;
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String addThingToMessage(String message){
        return "#!"+message+"&\n";
    }

    public void sendString() throws IOException {
        flag=true;
        final String str = "华安上小学第一天，我和他手牵着手，穿过好几条街，到维多利亚小学。九月初，家家户户院子里的苹果和梨树都缀满了拳头大小的果子，枝丫因为负重而沉沉下垂，越出了树篱，勾到过路行人的头发。很多很多的孩子，在操场上等候上课的第一声铃响。小小的手，圈在爸爸的、妈妈的手心里，怯怯的眼神，打量着周遭。他们是幼稚园的毕业生，但是他们还不知道一个定律：一件事情的毕业，永远是另一件事情的开启。铃声一响，顿时人影错杂，奔往不同方向，但是在那么多穿梭纷乱的人群里，我无比清楚地看着自己孩子的背影，就好象在一百个婴儿同时哭声大作时，你仍旧能够准确听出自己那一个的位置。华安背着一个五颜六色的书包往前走，但是他不断地回头；好象穿越一条无边无际的时空长河，他的视线和我凝望的眼光隔空交会。我看着他瘦小的背影消失在门里。十六岁，他到美国作交换生一年。我送他到机场。告别时，照例拥抱，我的头只能贴到他的胸口，好象抱住了长颈鹿的脚。他很明显地在勉强忍受母亲的深情。他在长长的行列里，等候护照检验；我就站在外面，用眼睛跟着他的背影一寸一寸往前挪。终于轮到他，在海关窗口停留片刻，然后拿回护照，闪入一扇门，倏乎不见。我一直在等候，等候他消失前的回头一瞥。但是他没有";
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int num = 0;
                    while(flag){
                        try {
                            Thread.sleep(1000);
                            int nums=i-num;
                            num = i;
                            Log.e(" ", String.valueOf(nums));
                            //显示发了多少个
                            Message msg = new Message();
                            msg.what = 4;
                            msg.obj="send:"+(nums)+"个";
                            handler.sendMessage(msg);

//                            System.out.println("i = "+i);
                            if( i >= (totalRound-1) ){
                                flag = false;
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }).start();
            //LogUtil.d("loage:"+str.getBytes().length);
            long startTime = currentTimeMillis();
            Log.e("开始时间",startTime+"");
            dos = new DataOutputStream(client.getOutputStream());
            for(i = 0; i<totalRound&&flag;i++){
                dos.writeUTF(str);
                dos.flush();
            }
            long endTime = currentTimeMillis();
            Log.e("结束时间",endTime+"");
            Message msg = new Message();
            msg.what = 2;
            handler.sendMessage(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }/*finally {
            if(dos != null)
                dos.close();
            client.close();
        }*/
        // 文件名和长度

    }

    public void stopSend(){
        flag=false;
        stopFlag=false;
    }

    /**
     * 入口
     * @param args
     */
    /*public static void main(String[] args) {
        try {
            SocketClient client = new SocketClient(); // 启动客户端连接
            client.sendFile(); // 传输文件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
