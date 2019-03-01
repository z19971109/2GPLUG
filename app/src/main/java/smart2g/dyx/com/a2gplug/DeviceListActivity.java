package smart2g.dyx.com.a2gplug;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceListActivity extends Activity {

    private ImageButton slide_device;

    private ListView listview;

    private TextView logOut;

    private DeviceListAdapter adapter;

    private List<DeviceInstance> lists;

    private int REQUEST_CODE_SCAN = 111;

    private MQTTFor2G mqttFor2G;

    private String mac;

    private boolean lineState = false;

    private QueryReceiver queryReceiver = new QueryReceiver();

    private MyProgressDialog myProgressDialog;

    private String status;
    private String configMac;
    private String phone;
    private String token;
    private String password;

    private Timer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list_layout);
        phone = Information.getStringConfig(DeviceListActivity.this, Information.LOGIN, Information.LOGIN_USER_NAME);
        token = Information.getStringConfig(DeviceListActivity.this, Information.LOGIN, Information.LOGIN_TOKEN);
        password = Information.getStringConfig(DeviceListActivity.this, Information.LOGIN, Information.LOGIN_PASSWORD);
        mqttFor2G = ApplicationFor2G.mqttFor2G;
        init();
//        initControl();


//        getLists(true);

        secondLogin();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.RESPONSE_SWITCH);
        intentFilter.addAction(Constants.RESPONSE_SYSTEM);
        registerReceiver(queryReceiver, intentFilter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (timer == null){
//            timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    getLists(false);
//                }
//            },300,3000);
//        }
    }

    private void secondLogin() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("phone", phone);
        requestParams.put("password", password);

        asyncHttpClient.post(Constants.MAIN_LINK + "login", requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);

                System.out.println("login:" + s);
                try {
                    JSONObject object = new JSONObject(s);
                    int code = object.optInt("code", 1);
                    if (code == 0) {
                        token = object.optString("access_token");
                        HashMap<String, Object> loginMap = new HashMap<>();
                        loginMap.put(Information.LOGIN_TOKEN, token);
                        loginMap.put(Information.LOGIN_PASSWORD, password);
                        loginMap.put(Information.LOGIN_USER_NAME, phone);
                        Information.saveInformation(DeviceListActivity.this, Information.LOGIN, loginMap);

                        getLists(true);

                    } else {
                        String msg = object.optString("msg");
                        Toast.makeText(DeviceListActivity.this, msg + ",请重新登录！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DeviceListActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
                Toast.makeText(DeviceListActivity.this, "网络超时!请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLists(final boolean once) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(3000);
        asyncHttpClient.get(Constants.MAIN_LINK + "device/list?phone=" + phone + "&access_token=" + token, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                System.out.println("getLists:" + s);
                lists.clear();
                ApplicationFor2G.lists.clear();
                try {
                    JSONObject object = new JSONObject(s);
                    JSONArray list = object.optJSONArray("list");
                    if (list != null && list.length() > 0) {
                        for (int i = 0; i < list.length(); i++) {
                            String mac = list.getString(i);
                            DeviceInstance deviceInstance = new DeviceInstance(6899, "2G插座", mac,
                                    "6899", 0, "0", new JSONArray().toString(), "", "", "", "");
                            lists.add(deviceInstance);

                            ApplicationFor2G.lists.add(new MacLists(mac));

                            if (once) {
                            }

                        }

                        new Thread(firstSub).start();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message message = new Message();
                Bundle data = new Bundle();
                data.putBoolean("refresh", true);
                message.setData(data);
                handler.sendMessage(message);

            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
                System.out.println("getLists接口异常");

                Toast.makeText(DeviceListActivity.this, "网络超时!请检查网络", Toast.LENGTH_SHORT).show();

                Message message = new Message();
                Bundle data = new Bundle();
                data.putBoolean("refresh", true);
                message.setData(data);
                handler.sendMessage(message);

            }

        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(queryReceiver);
    }

//    private void initControl() {
//        Thread t = new Thread(runnable);
//        t.start();
//    }
//
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (mqttFor2G != null) {
//
//                mqttFor2G.subscribe();
//
//                System.out.println("发布完成");
//
//            } else {
//                if (mqttFor2G.connect()) {
//                    mqttFor2G.subscribe();
//                    System.out.println("发布完成");
//                }
//            }
//        }
//    };

    private class QueryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.RESPONSE_SWITCH)) {
                String Switch = intent.getStringExtra(Constants.SWITCH);
                System.out.println("ListRESPONSE_SWITCH:"+Switch);
                try {
                    JSONObject object = new JSONObject(Switch);
                    String DeviceID = object.optString("DeviceID");
                    JSONObject Data = object.optJSONObject("Data");
                    if (Data != null) {
                        status = Data.optString("Status");
                    }
                    if (DeviceID.equals(mac + "2G")) {
                        lineState = true;

                        Message message = new Message();
                        Bundle data = new Bundle();
                        data.putBoolean("isConnect", true);
                        data.putBoolean("onLine", true);
                        message.setData(data);
                        handler.sendMessage(message);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (action.equals(Constants.RESPONSE_SYSTEM)) {
                String Switch = intent.getStringExtra(Constants.SWITCH);
                try {
                    JSONObject object = new JSONObject(Switch);
                    String DeviceID = object.optString("DeviceID");
                    if (DeviceID.equals(mac + "2G")) {
                        lineState = true;

                        Message message = new Message();
                        Bundle data = new Bundle();
                        data.putBoolean("isConnect", true);
                        data.putBoolean("onLine", true);
                        message.setData(data);
                        handler.sendMessage(message);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    ;

    Runnable subOnce = new Runnable() {
        @Override
        public void run() {

            if (ApplicationFor2G.mqttFor2G != null && ApplicationFor2G.mqttFor2G.isConnect()) {
//                for (int i = 0 ; i < MQTTApplication.lists.size() ; i++){
                if (ApplicationFor2G.mqttFor2G.subscribe(configMac)) {
                }
//                }
            }
        }
    };

    Runnable firstSub = new Runnable() {
        @Override
        public void run() {
            if (ApplicationFor2G.mqttFor2G != null && ApplicationFor2G.mqttFor2G.isConnect()) {
                for (int i = 0; i < lists.size(); i++) {
                    DeviceInstance instance = lists.get(i);
                    String mac = instance.getMac();
                    if (ApplicationFor2G.mqttFor2G.subscribe(mac)) ;
                }
            }
        }
    };

    private void bindDevice(final String mac) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(Constants.MAIN_LINK + "device/bind?phone=" + phone + "&deviceId=" + mac + "&access_token=" + token, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                System.out.println("bindDevice:" + s);
                try {
                    JSONObject object = new JSONObject(s);
                    int code = object.optInt("code");
                    String msg = object.optString("msg");
                    if (code == 0) {

                        Toast.makeText(DeviceListActivity.this, msg, Toast.LENGTH_SHORT).show();

                        configMac = mac;

                        new Thread(subOnce).start();

                        getLists(false);

                    } else {

                        if (code == 2) {

                            HashMap<String, Object> loginMap = new HashMap<>();

//                            if (re_pass.isChecked()){
                            loginMap.put(Information.LOGIN_TOKEN, "");
                            loginMap.put(Information.LOGIN_PASSWORD, "");
//

                            Information.saveInformation(DeviceListActivity.this, Information.LOGIN, loginMap);

                            Intent intent = new Intent();
                            intent.setClass(DeviceListActivity.this, LoginActivity.class);
                            startActivity(intent);

                            Toast.makeText(DeviceListActivity.this, msg + ",请重新登录！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DeviceListActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
                System.out.println("bindDevice接口异常");
                Toast.makeText(DeviceListActivity.this, "网络超时!请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
//                result.setText("扫描结果为：" + content);

//                DeviceInstance deviceInstance = new DeviceInstance(6899,"2G插座",content ,
//                        "6899",0,"0",new JSONArray().toString(),"","","","");

//                configMac = content;

                bindDevice(content);

//                new Thread(subOnce).start();
//
//                lists.add(deviceInstance);
//
//                ApplicationFor2G.lists.add(new MacLists(configMac));
//
//                adapter.notifyDataSetChanged();

            }
        }

    }

    private void init() {
        slide_device = findViewById(R.id.slide_device);
        listview = findViewById(R.id.listview);
        logOut = findViewById(R.id.logOut);

        lists = new ArrayList<>();
        adapter = new DeviceListAdapter(lists, DeviceListActivity.this);
        listview.setAdapter(adapter);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DeviceListActivity.this, LoginActivity.class);
                startActivity(intent);

                HashMap<String, Object> loginMap = new HashMap<>();

                loginMap.put(Information.LOGIN_TOKEN, "");
                loginMap.put(Information.LOGIN_PASSWORD, "");

                Information.saveInformation(DeviceListActivity.this, Information.LOGIN, loginMap);

                finish();
            }
        });

        slide_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndPermission.with(DeviceListActivity.this)
                        .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Intent intent = new Intent(DeviceListActivity.this, CaptureActivity.class);
                                /*ZxingConfig是配置类
                                 *可以设置是否显示底部布局，闪光灯，相册，
                                 * 是否播放提示音  震动
                                 * 设置扫描框颜色等
                                 * 也可以不传这个参数
                                 * */
                                ZxingConfig config = new ZxingConfig();
                                config.setPlayBeep(true);//是否播放扫描声音 默认为true
                                config.setShake(true);//是否震动  默认为true
                                config.setDecodeBarCode(false);//是否扫描条形码 默认为true
                                config.setReactColor(R.color.white);//设置扫描框四个角的颜色 默认为淡蓝色
                                config.setFrameLineColor(R.color.white);//设置扫描框边框颜色 默认无色
                                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                                startActivityForResult(intent, REQUEST_CODE_SCAN);
                            }
                        })
                        .onDenied(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Uri packageURI = Uri.parse("package:" + getPackageName());
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);

                                Toast.makeText(DeviceListActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                            }
                        }).start();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myProgressDialog = MyProgressDialog.createDialog(DeviceListActivity.this);
                myProgressDialog.setMessage("查询状态中...");
                myProgressDialog.show();
                mac = lists.get(position).getMac();
                new Thread(query).start();
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mac = lists.get(position).getMac();

                PLUGAlert.showAlert(DeviceListActivity.this, "确认删除这个设备？", "", null, null, new PLUGAlert.OnAlertSelectId() {
                    @Override
                    public void onClick(int whichButton) {
                        switch (whichButton) {
                            case 0:

                                myProgressDialog = MyProgressDialog.createDialog(DeviceListActivity.this);
                                myProgressDialog.setMessage("删除中...");
                                myProgressDialog.show();

                                deleteDevice(mac);

                                break;
                        }
                    }
                });

                return true;
            }
        });


    }

    private void deleteDevice(String mac) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(10000);

        System.out.println(Constants.MAIN_LINK + "device/delete?phone=" + phone + "&deviceId=" + mac + "&access_token=" + token);

        RequestParams requestParams = new RequestParams();
        requestParams.put("phone", phone);
        requestParams.put("deviceId", mac);
        requestParams.put("access_token", token);

        asyncHttpClient.post(Constants.MAIN_LINK + "device/delete", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                super.onSuccess(i, s);
                System.out.println("deleteDevice:" + s + "|" + i);
                try {
                    JSONObject object = new JSONObject(s);
                    int code = object.optInt("code");
                    String msg = object.optString("msg");


                    myProgressDialog.dismiss();

                    if (code == 0) {
                        getLists(true);
                        Toast.makeText(DeviceListActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        if (code == 2) {
                            HashMap<String, Object> loginMap = new HashMap<>();

//                            if (re_pass.isChecked()){
                            loginMap.put(Information.LOGIN_TOKEN, "");
                            loginMap.put(Information.LOGIN_PASSWORD, "");
//

                            Information.saveInformation(DeviceListActivity.this, Information.LOGIN, loginMap);

                            Intent intent = new Intent();
                            intent.setClass(DeviceListActivity.this, LoginActivity.class);
                            startActivity(intent);

                            Toast.makeText(DeviceListActivity.this, msg + ",请重新登录！", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(DeviceListActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    myProgressDialog.dismiss();

                }
            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
                System.out.println("deleteDevice接口不通或网络异常:" + throwable.getMessage());

                Toast.makeText(DeviceListActivity.this, "删除失败!请检查网络", Toast.LENGTH_SHORT).show();

                myProgressDialog.dismiss();
            }
        });
    }

    Runnable query = new Runnable() {
        @Override
        public void run() {
            if (mqttFor2G.isConnect()) {
                if (mqttFor2G.querySwitch(mac)) {
                    try {
                        Thread.sleep(3500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    Bundle data = new Bundle();

                    System.out.println("lineState:" + lineState);

                    if (lineState) {

                    } else {
                        data.putBoolean("isConnect", true);
                        data.putBoolean("onLine", false);
                    }

                    lineState = false;

                    message.setData(data);
                    handler.sendMessage(message);

                } else {
                    Message message = new Message();
                    Bundle data = new Bundle();

                    data.putBoolean("isConnect", false);
                    data.putBoolean("onLine", true);
                    lineState = false;

                    message.setData(data);
                    handler.sendMessage(message);
                }
            } else {
                Message message = new Message();
                Bundle data = new Bundle();

                data.putBoolean("isConnect", false);
                data.putBoolean("onLine", true);

                lineState = false;

                message.setData(data);
                handler.sendMessage(message);
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            boolean onLine = data.getBoolean("onLine");
            boolean isConnect = data.getBoolean("isConnect",true);


            boolean refresh = data.getBoolean("refresh");
            if (refresh) {
                adapter.notifyDataSetChanged();
            }

            if (isConnect){
                if (onLine) {
                    if (myProgressDialog != null && myProgressDialog.isShowing()) {
                        myProgressDialog.dismiss();
                        Intent intent = new Intent(DeviceListActivity.this, DeviceActivity.class);
                        intent.putExtra("mac", mac);
                        intent.putExtra("status", status);
                        startActivity(intent);
                    }
                } else {
                    if (myProgressDialog != null && myProgressDialog.isShowing()) {
                        myProgressDialog.dismiss();
                        Toast.makeText(DeviceListActivity.this, "设备已离线！", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (myProgressDialog != null && myProgressDialog.isShowing()) {
                    myProgressDialog.dismiss();
                }
                Toast.makeText(DeviceListActivity.this, "网络异常,请检查网络！", Toast.LENGTH_SHORT).show();
            }

        }
    };


}
