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
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceActivity extends FragmentActivity {

    private ViewPager device_view_page;

    private List<Fragment> mPageList = new ArrayList<Fragment>();

    private TextView control_text, timer_text, mac_text;

    private MQTTFor2G mqttFor2G;

    private String mac;

    private String status;

    private boolean sub = false;

    private int Index;

    private DeviceReceiver receiver = new DeviceReceiver();

    private int Timer;

    private Timer mTimer;

    private String object;

    private int REQUEST_CODE_SCAN = 111;

    public static final int REQUEST_CODE = 111;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_layout);
        status = getIntent().getStringExtra("status");
        mac = getIntent().getStringExtra("mac");

        mqttFor2G = ApplicationFor2G.mqttFor2G;
//        initControl();
        init();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.RESPONSE_SYSTEM);
        registerReceiver(receiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Information.QUERY, "");
        hashMap.put(Information.GETQUERY, "");
        hashMap.put(Information.CONTROLSWITCH, "");
        hashMap.put(Information.GETCONTROLSWITCH, "");
        hashMap.put(Information.QUERYSWITCH, "");
        hashMap.put(Information.GETQUERYSWITCH, "");
        hashMap.put(Information.TIMERLIST, "");
        hashMap.put(Information.GETTIMERLIST, "");
        hashMap.put(Information.TIMERLIST1, "");
        hashMap.put(Information.GETTIMERLIST1, "");
        hashMap.put(Information.TIMERLIST2, "");
        hashMap.put(Information.GETTIMERLIST2, "");
        hashMap.put(Information.TIMERLIST3, "");
        hashMap.put(Information.GETTIMERLIST3, "");
        hashMap.put(Information.TIMERLIST4, "");
        hashMap.put(Information.GETTIMERLIST4, "");
        hashMap.put(Information.TIMERLIST5, "");
        hashMap.put(Information.GETTIMERLIST5, "");
        hashMap.put(Information.SETTIMER, "");
        hashMap.put(Information.GETSETTIMER, "");
        hashMap.put(Information.SETTIMER1, "");
        hashMap.put(Information.GETSETTIMER1, "");
        hashMap.put(Information.SETTIMER2, "");
        hashMap.put(Information.GETSETTIMER2, "");
        hashMap.put(Information.SETTIMER3, "");
        hashMap.put(Information.GETSETTIMER3, "");
        hashMap.put(Information.SETTIMER4, "");
        hashMap.put(Information.GETSETTIMER4, "");
        hashMap.put(Information.SETTIMER5, "");
        hashMap.put(Information.GETSETTIMER5, "");
        hashMap.put(Information.CONTROLTIMER, "");
        hashMap.put(Information.GETCONTROLTIMER, "");
        hashMap.put(Information.CONTROLTIMER1, "");
        hashMap.put(Information.GETCONTROLTIMER1, "");
        hashMap.put(Information.CONTROLTIMER2, "");
        hashMap.put(Information.GETCONTROLTIMER2, "");
        hashMap.put(Information.CONTROLTIMER3, "");
        hashMap.put(Information.GETCONTROLTIMER3, "");
        hashMap.put(Information.CONTROLTIMER4, "");
        hashMap.put(Information.GETCONTROLTIMER4, "");
        hashMap.put(Information.CONTROLTIMER5, "");
        hashMap.put(Information.GETCONTROLTIMER5, "");

        Information.saveInformation(DeviceActivity.this, Information.PLUG, hashMap);

        unregisterReceiver(receiver);

    }

    private class DeviceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Constants.RESPONSE_SYSTEM)) {

                HashMap<String, Object> map = new HashMap<>();
                map.put(Information.QUERY, "");
                map.put(Information.GETQUERY, "");

                Information.saveInformation(DeviceActivity.this, Information.PLUG, map);

                String SWITCH = intent.getStringExtra(Constants.SWITCH);
                Message message = new Message();
                Bundle data = new Bundle();
                data.putString("switch", SWITCH);
                data.putBoolean("CheckTimerList", true);
                message.setData(data);
                timerListHandler.sendMessage(message);
            } else if (action.equals(Constants.REPORT_SYSTEM)) {
//                String SWITCH = intent.getStringExtra(Constants.SWITCH);
//                Message message = new Message();
//                Bundle data = new Bundle();
//                data.putString("switch", SWITCH);
//                data.putBoolean("CheckTimerList", true);
//                message.setData(data);
//                timerListHandler.sendMessage(message);
            }
        }
    }

    Handler timerListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();

            boolean check = data.getBoolean("CheckTimerList", false);

            if (check) {
                final String SWITCH = data.getString("switch");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 写子线程中的操作
                        try {
                            System.out.println("DeviceReceiver:" + SWITCH);
                            JSONObject object = new JSONObject(SWITCH);
                            JSONObject data_Ob = object.optJSONObject("Data");
                            Index = data_Ob.optInt("TIMER");
                            for (int i = 1; i <= Index; i++) {
                                Timer = i;

                                Thread.sleep(5000);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }


}
    };


    Runnable query = new Runnable() {
        @Override
        public void run() {
            if (mqttFor2G != null) {
                if (mqttFor2G.query(Constants.mac, DeviceActivity.this)) {

                } else {
                    Message message = new Message();
                    Bundle data = new Bundle();
                    data.putString("message", "OFFLINE");
                    data.putString("mac", "");
                    message.setData(data);
                    handler.sendMessage(message);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//        if (mTimer == null) {
//            mTimer = new Timer();
//            mTimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//
//                    String query_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.QUERY);
//                    String getQuery = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETQUERY);
//
//                    if (!query_s.equals("") ){
//                        circular(query_s);
//                    }
//
//                    String querySwitch_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.QUERYSWITCH);
//                    String getQuerySwitch = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETQUERYSWITCH);
//
//                    if (!querySwitch_s.equals("") ){
//                        circular(querySwitch_s);
//                    }
//
//                    String control_Switch_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.CONTROLSWITCH);
//                    String getControlSwitch = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETCONTROLSWITCH);
//
//                    if (!control_Switch_s.equals("")){
//                        circular(control_Switch_s);
//                    }
//
//                    String timerList1_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.TIMERLIST1);
//                    String getTimerList1_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETTIMERLIST1);
//                    String setTimer1_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.SETTIMER1);
////                    String getSetTimer1_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETSETTIMER1);
//                    String controlTimer1_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.CONTROLTIMER1);
////                    String getControlTimer1_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETCONTROLTIMER1);
//
//                    if (!timerList1_s.equals("")){
//                        circular(timerList1_s);
//                    }
//                    if (!setTimer1_s.equals("")){
//                        circular(setTimer1_s);
//                    }
//                    if (!controlTimer1_s.equals("")){
//                        circular(controlTimer1_s);
//                    }
//
//                    String timerList2_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.TIMERLIST2);
////                    String getTimerList2_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETTIMERLIST2);
//                    String setTimer2_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.SETTIMER2);
////                    String getSetTimer2_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETSETTIMER2);
//                    String controlTimer2_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.CONTROLTIMER2);
////                    String getControlTimer2_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETCONTROLTIMER2);
//
//                    if (!timerList2_s.equals("")){
//                        circular(timerList2_s);
//                    }
//
//                    if (!setTimer2_s.equals("")){
//                        circular(setTimer2_s);
//                    }
//
//                    if (!controlTimer2_s.equals("")){
//                        circular(controlTimer2_s);
//                    }
//
//                    String timerList3_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.TIMERLIST3);
////                    String getTimerList3_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETTIMERLIST3);
//                    String setTimer3_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.SETTIMER3);
////                    String getSetTimer3_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETSETTIMER3);
//                    String controlTimer3_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.CONTROLTIMER3);
////                    String getControlTimer3_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETCONTROLTIMER3);
//
//                    if (!timerList3_s.equals("")){
//                        circular(timerList3_s);
//                    }
//
//                    if (!setTimer3_s.equals("")){
//                        circular(setTimer3_s);
//                    }
//                    if (!controlTimer3_s.equals("")){
//                        circular(controlTimer3_s);
//                    }
//
//                    String timerList4_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.TIMERLIST4);
////                    String getTimerList4_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETTIMERLIST4);
//                    String setTimer4_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.SETTIMER4);
////                    String getSetTimer4_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETSETTIMER4);
//                    String controlTimer4_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.CONTROLTIMER4);
////                    String getControlTimer4_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETCONTROLTIMER4);
//
//                    if (!timerList4_s.equals("")){
//                        circular(timerList4_s);
//                    }
//
//                    if (!setTimer4_s.equals("")){
//                        circular(setTimer4_s);
//                    }
//
//                    if (!controlTimer4_s.equals("")){
//                        circular(controlTimer4_s);
//                    }
//
//                    String timerList5_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.TIMERLIST5);
////                    String getTimerList5_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETTIMERLIST5);
//                    String setTimer5_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.SETTIMER5);
////                    String getSetTimer5_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETSETTIMER5);
//                    String controlTimer5_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.CONTROLTIMER5);
////                    String getControlTimer5_s = Information.getStringConfig(DeviceActivity.this,Information.PLUG,Information.GETCONTROLTIMER5);
//
//                    if (!timerList5_s.equals("")){
//                        circular(timerList5_s);
//                    }
//
//                    if (!setTimer5_s.equals("")){
//                        circular(setTimer5_s);
//                    }
//
//                    if (!controlTimer5_s.equals("")){
//                        circular(controlTimer5_s);
//                    }
//
//
//                }
//            }, 300,10000);
//        }
    }

    public void circular(String Timer) {
        object = Timer;
        Thread task = new Thread(circularTask);
        task.start();
    }

    Runnable circularTask = new Runnable() {
        @Override
        public void run() {
            if (mqttFor2G != null) {
                if (mqttFor2G.circularTask(object, Constants.mac)) {

                }
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String message = data.getString("message");
            String mac = data.getString("mac", "");

            if (message.equals("SUCCESS")) {

                sub = true;
                Toast.makeText(DeviceActivity.this, "初始化成功!", Toast.LENGTH_SHORT).show();

                Thread query = new Thread(timer_list);
                query.start();

//                Thread t = new Thread(query);
//                t.start();

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == REQUEST_CODE) {
//            //处理扫描结果（在界面上显示）
//            if (null != data) {
//                Bundle bundle = data.getExtras();
//                if (bundle == null) {
//                    return;
//                }
//                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
//                    String result = bundle.getString(CodeUtils.RESULT_STRING);
//                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
//                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
//                    Toast.makeText(DeviceActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
//                }
//            }
//        }

    }

    Runnable timer_list = new Runnable() {
        @Override
        public void run() {
            if (mqttFor2G != null) {

//                System.out.println("Timer:" + Timer);
                mqttFor2G.querySwitch(Constants.mac);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                mqttFor2G.queryTimer(Constants.mac);
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                mqttFor2G.queryTime(Constants.mac);


            }
        }
    };

    public boolean Subscribe() {
        return sub;
    }

    private void init() {
        Button device_back_bt = findViewById(R.id.device_back_bt);

        device_back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        control_text = findViewById(R.id.control_text);
        timer_text = findViewById(R.id.timer_text);
        device_view_page = findViewById(R.id.device_view_page);
        mac_text = findViewById(R.id.mac_text);
//        mac_text.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void doOnClick(View v) {
//
//                Intent intent = new Intent(DeviceActivity.this, SecondActivity.class);
//                startActivityForResult(intent, REQUEST_CODE);
//
//
//            }
//        });
        control_text.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                device_view_page.setCurrentItem(0);
            }
        });
        timer_text.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                device_view_page.setCurrentItem(1);
            }
        });
        ControlFragment controlFragment = new ControlFragment();
        Bundle bundle = new Bundle();
        bundle.putString("mac",mac);
        bundle.putString("status",status);
        controlFragment.setArguments(bundle);
        TimerFragment timerFragment = new TimerFragment();
        timerFragment.setArguments(bundle);
        mPageList.add(controlFragment);
        mPageList.add(timerFragment);
        device_view_page.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
        device_view_page.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
//                    myStock_text.setTextColor(getResources().getColor(R.color.sp_mini_bar_text_blue));
                    control_text.setTextColor(getResources().getColor(R.color.sp_mini_bar_text_blue));
                    control_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.mini_control_2);
                } else {
//                    myStock_text.setTextColor(getResources().getColor(R.color.sp_mini_bar_text_gray));
                    control_text.setTextColor(getResources().getColor(R.color.sp_mini_bar_text_gray));
                    control_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.mini_control_1);
                }

                if (position == 1) {
//                    setForMy_text.setTextColor(getResources().getColor(R.color.sp_mini_bar_text_blue));
                    timer_text.setTextColor(getResources().getColor(R.color.sp_mini_bar_text_blue));
                    timer_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.mini_delay_2);
                } else {
//                    setForMy_text.setTextColor(getResources().getColor(R.color.sp_mini_bar_text_gray));
                    timer_text.setTextColor(getResources().getColor(R.color.sp_mini_bar_text_gray));
                    timer_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.mini_delay_1);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        mac = mac_text.getText().toString();
    }

    public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int location) {
            return mPageList.get(location);
        }

        @Override
        public int getCount() {
            return mPageList.size();
        }
    }
}
