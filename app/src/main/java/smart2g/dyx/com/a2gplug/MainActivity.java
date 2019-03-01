package smart2g.dyx.com.a2gplug;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.List;

public class MainActivity extends Activity {

    private Button connect , open , close , openAP;

    private MQTTFor2G mqttFor2G;

    private EditText mac_Ed;

    private TextView message_tv;

    private SwitchReceiver mSwitchReveiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mqttFor2G = ApplicationFor2G.mqttFor2G;
        mac_Ed = (EditText) findViewById(R.id.mac_Ed);
        connect = (Button) findViewById(R.id.connect);
        open = (Button) findViewById(R.id.open);
        openAP = (Button) findViewById(R.id.intent_bt);
        close = (Button) findViewById(R.id.close);
        message_tv = (TextView) findViewById(R.id.message_tv);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t = new Thread(runnable);
                t.start();
            }
        });

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t = new Thread(runnable2);
                t.start();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t = new Thread(runnable3);
                t.start();
            }
        });

        openAP.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Constants.mac = mac_Ed.getText().toString();

                System.out.println("mac:"+Constants.mac);

                Intent intent = new Intent();
//                intent.putExtra("mac", mac_Ed.getText().toString());
                intent.setClass(MainActivity.this,DeviceActivity.class);
                startActivity(intent);

//                try {
//                android.net.wifi.WifiManager mWifiManager = (android.net.wifi.WifiManager) getSystemService(android.content.Context.WIFI_SERVICE);
//                android.net.wifi.WifiConfiguration apConfig = new android.net.wifi.WifiConfiguration();
//                apConfig.SSID = "none";
//                apConfig.wepKeys[0] = "";
//                apConfig.allowedKeyManagement.set(android.net.wifi.WifiConfiguration.KeyMgmt.NONE);
//                apConfig.wepTxKeyIndex = 0;

//                java.lang.reflect.Method method = apConfig.getClass().getMethod("setWifiApEnabled", android.net.wifi.WifiConfiguration.class, boolean.class);
//            method.invoke(mWifiManager, apConfig, true);
//            } catch (Exception e) {
//            e.printStackTrace();
//        }
//                setWifiSSIDForHTC(apConfig);
            }
        });

        mSwitchReveiver = new SwitchReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.SWITCH_ACTION);
        registerReceiver(mSwitchReveiver,intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSwitchReveiver != null){
            unregisterReceiver(mSwitchReveiver);
        }
    }

    //    private boolean setWifiSSIDForHTC(android.net.wifi.WifiConfiguration apConfig) {
//	    boolean successed = true;
//
////	    WifiConfiguration mnetConfig = new WifiConfiguration();
//	    java.lang.reflect.Field localField1;
//
//	                    try {
//
////	                        localField1 = android.net.wifi.WifiConfiguration.class.getDeclaredField("mWifiApProfile");
//
////	                        localField1.setAccessible(true);
//
//	                        Object localObject2 = localField1.get(apConfig);
//
////	                        localField1.setAccessible(false);
//	                        if(localObject2!=null){
//
//	                            java.lang.reflect.Field localField5 = localObject2.getClass().getDeclaredField("SSID");
//
//	                            localField5.setAccessible(true);
//
//	                            localField5.set(localObject2, apConfig.SSID);// netConfig.SSID);
//
//	                            localField5.setAccessible(false);
//
////	                            java.lang.reflect.Field localField4 = localObject2.getClass().getDeclaredField("BSSID");
////
////	                            localField4.setAccessible(true);
////
////	                            localField4.set(localObject2, apConfig.BSSID);//netConfig.BSSID);
////
////	                            localField4.setAccessible(false);
//
//	                            java.lang.reflect.Field localField6 = localObject2.getClass().getDeclaredField("dhcpEnable");
//
//                                localField6.setAccessible(true);
//
////                                localField6.set(localObject2, "true");//netConfig.BSSID);
//                                localField6.setInt(localObject2, 1);
//
//                                localField6.setAccessible(false);
//
//	                        }
//
//	                    } catch(Exception e) {
//
//	                        e.printStackTrace();
//
//	                    }
//
//	    return successed;
//	}


    private class SwitchReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SWITCH_ACTION)){
                String SWITCH = intent.getStringExtra(Constants.SWITCH);
                System.out.println();
            }
        }
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String message = data.getString("message");
            String mac = data.getString("mac","");

            if (message.equals("SUCCESS")){
                if (!mac.equals("")){
                    message_tv.setText(mac);
                } else {
                    message_tv.setText("连接成功");
                }
            } else if (message.equals("ON")){
                message_tv.setText("发送开命令成功:"+"/DYX/2GPLUG/"+mac_Ed.getText().toString()+"2G");
            } else if (message.equals("OFF")){
                message_tv.setText("发送关命令成功:"+"/DYX/2GPLUG/"+mac_Ed.getText().toString()+"2G");
            } else if (message.equals("OFFLINE")){
                Toast.makeText(MainActivity.this,"服务器未连接，请点击连接重试",Toast.LENGTH_SHORT).show();
            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mqttFor2G != null){

                if (mqttFor2G.connect()){

//                    mqttFor2G.subscribe();

                    System.out.println("连接成功");
                    Message message = new Message();
                    Bundle data = new Bundle();
                    data.putString("message","SUCCESS");
                    data.putString("mac","");
                    message.setData(data);
                    handler.sendMessage(message);
                }

            }
        }
    };

    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            if (mqttFor2G != null){
                if (mqttFor2G.publish("ON",mac_Ed.getText().toString() , MainActivity.this)){
                    System.out.println("打开");

                    Message message = new Message();
                    Bundle data = new Bundle();
                    data.putString("message","ON");
                    data.putString("mac","");
                    message.setData(data);
                    handler.sendMessage(message);

                }
            } else {
                Message message = new Message();
                Bundle data = new Bundle();
                data.putString("message","OFFLINE");
                data.putString("mac","");
                message.setData(data);
                handler.sendMessage(message);
            }
        }
    };


//    private void test(){
//        AndPermission.with(MainActivity.this)
//                .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
//                .onGranted(new Action() {
//                    @Override
//                    public void onAction(List<String> permissions) {
//                        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
//                        /*ZxingConfig是配置类
//                         *可以设置是否显示底部布局，闪光灯，相册，
//                         * 是否播放提示音  震动
//                         * 设置扫描框颜色等
//                         * 也可以不传这个参数
//                         * */
//                        ZxingConfig config = new ZxingConfig();
//                        config.setPlayBeep(true);//是否播放扫描声音 默认为true
//                        config.setShake(true);//是否震动  默认为true
//                        config.setDecodeBarCode(false);//是否扫描条形码 默认为true
//                        config.setReactColor(R.color.white);//设置扫描框四个角的颜色 默认为淡蓝色
//                        config.setFrameLineColor(R.color.white);//设置扫描框边框颜色 默认无色
//                        config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
//                        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
//                        startActivityForResult(intent, 111);
//                    }
//                })
//                .onDenied(new Action() {
//                    @Override
//                    public void onAction(List<String> permissions) {
//                        Uri packageURI = Uri.parse("package:" + getPackageName());
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                        startActivity(intent);
//
//                        Toast.makeText(MainActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
//                    }
//                }).start();
//    }

    Runnable runnable3 = new Runnable() {
        @Override
        public void run() {
            if (mqttFor2G != null){
                if (mqttFor2G.publish("OFF",mac_Ed.getText().toString() , MainActivity.this)){
                    System.out.println("关闭");

                    Message message = new Message();
                    Bundle data = new Bundle();
                    data.putString("message","OFF");
                    data.putString("mac","");
                    message.setData(data);
                    handler.sendMessage(message);
                }
            }
        }
    };

}
