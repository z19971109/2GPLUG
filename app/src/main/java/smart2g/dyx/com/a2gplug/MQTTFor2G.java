package smart2g.dyx.com.a2gplug;

import android.content.Context;
import android.provider.Settings;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.telephony.TelephonyManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class MQTTFor2G {

    private ICallBack callBack;

    public static final String HOST = "tcp://ali.5955555.cn:1883";

    private MqttClient mqttClient;

    private Context context;

    private MqttConnectOptions options;

    private List<MacLists> lists;

    private String mac = "";

    public MQTTFor2G( ICallBack callBack , Context context , List<MacLists> lists) {
        this.callBack = callBack;
        this.context = context;
        this.lists = lists;
    }

    public boolean connect() {
        try {
            MqttCallback callback = new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    mqttClient = null;
                    System.out.println("已断开连接");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (connect()){
                        for (int i = 0 ; i < lists.size() ; i++){
                            mac = lists.get(i).getMac();
                            subscribe(mac);
                        }

                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    System.out.println("topic:" + topic + "\n" + "message:" + message.toString());
//                    if (topic.indexOf(mac.toUpperCase()) != -1) {
                        callBack.onConnected(message.toString(),topic);
//                    }

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            };
            System.out.println("连接外部");



            if (mqttClient == null) {

                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

                String imei = m_szAndroidID;


                mqttClient = new MqttClient(HOST, imei, new MemoryPersistence());
                // MQTT的连接设置
                options = new MqttConnectOptions();
                options.setMqttVersion(4);
                // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
                options.setCleanSession(true);
//            // 设置连接的用户名
//            options.setUserName(userName);
//            // 设置连接的密码
//            options.setPassword(passWord.toCharArray());
                // 设置超时时间 单位为秒
                options.setConnectionTimeout(10);
                // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
                options.setKeepAliveInterval(60);
                // 设置回调
                mqttClient.setCallback(callback);

                mqttClient.connect(options);

                System.out.println("连接成功");

//                mqttClient.subscribe("/DYX/APP/CONNECT/" + mac.toUpperCase() + "2G", 0);
//
//                System.out.println("创建并发布完成" + "/DYX/APP/CONNECT/" + mac.toUpperCase() + "2G");

            } else if (mqttClient != null && mqttClient.isConnected()) {
                System.out.println("无需连接");

//                return false;
//                mqttClient.subscribe("/DYX/APP/CONNECT/" + mac.toUpperCase() + "2G", 0);
//
//                System.out.println("发布完成" + "/DYX/APP/CONNECT/" + mac.toUpperCase() + "2G");

            }

            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("异常:" + e.getMessage());
            mqttClient = null;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            if (connect()){
                for (int i = 0 ; i < lists.size() ; i++){
                    mac = lists.get(i).getMac();
                    subscribe(mac);
                }
            }

            return false;
        }
    }

    public boolean isConnect(){
        return mqttClient!=null && mqttClient.isConnected()  ;
    }

    public boolean subscribe(String mac) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
//                System.out.println("无需连接");
//                return false;
                mqttClient.subscribe("/DYX/2GPLUG/"+mac+"2G", 0);

                System.out.println("发布完成" + "/DYX/2GPLUG/"+mac+"2G");

                return true;

            } else {

                System.out.println("未实例化");

                return false;
            }
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setTimer(int index, String STime, String ETime, String Mode, int UserDaySet, String SwitchAction , String mac) {
        if (mqttClient != null) {
            JSONObject object = new JSONObject();
            try {
                object.put("Type", "CONTROL");
                object.put("Object", "TIMER");
                object.put("Index", index);
                object.put("Action", "SETUP");
                JSONObject data = new JSONObject();
                data.put("Status", "ENABLE");
                data.put("STime", STime);
                data.put("ETime", ETime);
                data.put("Mode", Mode);
                data.put("UserDaySet", UserDaySet);
                data.put("SwitchAction", SwitchAction);
                object.put("DATA", data);
//                object.put("deviceID",mac+"2G");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("setTimer：" + object.toString());

            try {
                mqttClient.publish("/DYX/2GPLUG/" + mac + "2G", object.toString().getBytes(), 1, true);
                System.out.println("setTimer成功发送:" + "/DYX/2GPLUG/" + mac + "2G");

                HashMap<String , Object> map = new HashMap<>();
                map.put(Information.SETTIMER+index,object.toString());

                Information.saveInformation(context , Information.PLUG , map);

                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                System.out.println("setTimer发送失败:" + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean circularTask(String object , String mac){
        if (mqttClient != null) {
            try {

                System.out.println("circularTask:"+object);

                mqttClient.publish("/DYX/2GPLUG/" + mac + "2G", object.toString().getBytes(), 1, true);
                System.out.println("circularTask成功发送:" + "/DYX/2GPLUG/" + mac + "2G");

                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                System.out.println("circularTask发送失败:" + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean controlTimer(int index , String mac , String action){
        if (mqttClient != null){
            JSONObject object = new JSONObject();

            try {
                object.put("Type","CONTROL");
                object.put("Object","TIMER");
                object.put("Index",index);
                object.put("Action",action);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("controlTimer:"+object.toString());

            try {
                mqttClient.publish("/DYX/2GPLUG/" + mac + "2G", object.toString().getBytes(), 1, true);
                System.out.println("controlTimer成功发送:" + "/DYX/2GPLUG/" + mac + "2G");

//                System.out.println("queryTimer:"+Information.TIMERLIST+index);

                HashMap<String , Object> map = new HashMap<>();
                map.put(Information.CONTROLTIMER+index,object.toString());

                Information.saveInformation(context , Information.PLUG , map);

                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                System.out.println("controlTimer发送失败:" + e.getMessage());
                return false;
            }

        }
        return false;
    }

    public boolean queryTimer(String mac , int index){
        if (mqttClient != null){
            JSONObject object = new JSONObject();

            try {
                object.put("Type","QUERY");
                object.put("Object","TIMER");
                object.put("Index",index);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("queryTimer:"+object.toString());

            try {
                mqttClient.publish("/DYX/2GPLUG/" + mac + "2G", object.toString().getBytes(), 1, true);
                System.out.println("queryTimer成功发送:" + "/DYX/2GPLUG/" + mac + "2G");

//                System.out.println("queryTimer:"+Information.TIMERLIST+index);

                HashMap<String , Object> map = new HashMap<>();
                map.put(Information.TIMERLIST,object.toString());

                Information.saveInformation(context , Information.PLUG , map);

                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                System.out.println("queryTimer发送失败:" + e.getMessage());
                return false;
            }

        }

        return false;
    }

    public boolean query(String mac , Context context) {
        if (mqttClient != null) {
            JSONObject object = new JSONObject();
            try {
                object.put("Type", "QUERY");
                object.put("Object", "SYSTEM");
                object.put("Index", 0);
//                object.put("deviceID",mac+"2G");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("query：" + object.toString());

            try {
                mqttClient.publish("/DYX/2GPLUG/" + mac + "2G", object.toString().getBytes(), 1, true);
                System.out.println("query成功发送:" + "/DYX/2GPLUG/" + mac + "2G");

                HashMap<String , Object> map = new HashMap<>();
                map.put(Information.QUERY,object.toString());

                Information.saveInformation(context , Information.PLUG , map);

                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                System.out.println("query发送失败:" + e.getMessage());
                return false;
            }

        }
        return false;
    }

    public boolean queryTime(String mac){
        if (mqttClient != null) {
            JSONObject object = new JSONObject();
            try {
                object.put("Type", "QUERY");
                object.put("Object", "TIME");
                object.put("Index", 0);
//                object.put("deviceID",mac+"2G");
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            System.out.println("querySwitch：" + object.toString());

            try {
                mqttClient.publish("/DYX/2GPLUG/" + mac + "2G", object.toString().getBytes(), 1, true);
                System.out.println("queryTime成功发送:" + "/DYX/2GPLUG/" + mac + "2G");

                HashMap<String , Object> map = new HashMap<>();
                map.put(Information.QUERYTIME,object.toString());

                Information.saveInformation(context , Information.PLUG , map);

                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                System.out.println("queryTime发送失败:" + e.getMessage());
                return false;
            }

        }
        return false;
    }

    public boolean querySwitch(String mac){
        if (mqttClient != null) {
            JSONObject object = new JSONObject();
            try {
                object.put("Type", "QUERY");
                object.put("Object", "SWITCH");
                object.put("Index", 0);
//                object.put("deviceID",mac+"2G");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("querySwitch：" + object.toString());

            try {
                mqttClient.publish("/DYX/2GPLUG/" + mac + "2G", object.toString().getBytes(), 1, true);
                System.out.println("querySwitch成功发送:" + "/DYX/2GPLUG/" + mac + "2G");

                HashMap<String , Object> map = new HashMap<>();
                map.put(Information.QUERYSWITCH,object.toString());

                Information.saveInformation(context , Information.PLUG , map);

                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                System.out.println("querySwitch发送失败:" + e.getMessage());
                return false;
            }

        }
        return false;
    }

    public boolean publish(String action, String mac , Context context) {
        if (mqttClient != null) {
            JSONObject object = new JSONObject();
            try {
                object.put("Type", "CONTROL");
                object.put("Object", "SWITCH");
                object.put("Index", 1);
                object.put("Action", action);
//                object.put("deviceID",mac+"2G");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("publish：" + object.toString());

            try {
                mqttClient.publish("/DYX/2GPLUG/" + mac + "2G", object.toString().getBytes(), 1, true);
                System.out.println("publish成功发送:" + "/DYX/2GPLUG/" + mac + "2G");

                HashMap<String , Object> map = new HashMap<>();
                map.put(Information.CONTROLSWITCH,object.toString());

                Information.saveInformation(context , Information.PLUG , map);

                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                System.out.println("publish发送失败:" + e.getMessage());
                return false;
            }
        }
        return false;
    }

}
