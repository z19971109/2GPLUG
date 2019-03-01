package smart2g.dyx.com.a2gplug;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ControlFragment extends Fragment {

    private Button switch_control;

    private AVLoadingIndicatorView loadingInd;

    private boolean mInSwitchControl = false;

    private boolean switchStatus = false;

    private MQTTFor2G mqttFor2G;

    private boolean sub;

    public static boolean first = true;

    private Timer mTimer;

    private SwitchReceiver mSwitchReveiver;

    private String action;

    final static int COUNTS = 5;// 点击次数
    final static long DURATION = 1000;// 规定有效时间
    long[] mHits = new long[COUNTS];

    private String mac;
    private String status;

    private Bundle bundle;

    public static boolean onLine = true;

    private MyProgressDialog myProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.control_layout, container, false);
        mqttFor2G = ApplicationFor2G.mqttFor2G;

        bundle = this.getArguments();
        mac = bundle.getString("mac", "");
        status = bundle.getString("status", "");

        init(view);

        first = true;

        if (first){
            loadingInd.setVisibility(View.VISIBLE);
        }

        mSwitchReveiver = new SwitchReceiver();

//        myProgressDialog = MyProgressDialog.createDialog2(getActivity());
//        myProgressDialog.setCancelable(true);

        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Constants.RESPONSE_SYSTEM);
        intentFilter.addAction(Constants.RESPONSE_SWITCH);
        intentFilter.addAction(Constants.REPORT_SWITCH);
        intentFilter.addAction(Constants.RESPONSE_TIME);
        intentFilter.addAction("first");
        getActivity().registerReceiver(mSwitchReveiver, intentFilter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mSwitchReveiver);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (mTimer == null){
//            mTimer = new Timer();
//            mTimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//
//                }
//            },200,3000);
//        }
    }

    private class SwitchReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            if (action.equals(Constants.RESPONSE_SYSTEM)) {
////                String SWITCH = intent.getStringExtra(Constants.SWITCH);
////                System.out.println("RESPONSE_SYSTEM:" + SWITCH);
////                initControl(SWITCH);
//
//            } else
            if (action.equals(Constants.RESPONSE_SWITCH)) {
                String SWITCH = intent.getStringExtra(Constants.SWITCH);
                System.out.println("RESPONSE_SWITCH:" + SWITCH);

                HashMap<String, Object> map = new HashMap<>();
                map.put(Information.QUERYSWITCH, "");
                map.put(Information.GETQUERYSWITCH, "");
                map.put(Information.CONTROLSWITCH, "");
                map.put(Information.GETCONTROLSWITCH, "");

                Information.saveInformation(getActivity(), Information.PLUG, map);

                initControl(SWITCH);

            } else if (action.equals(Constants.REPORT_SWITCH)) {
                String SWITCH = intent.getStringExtra(Constants.SWITCH);
                System.out.println("REPORT_SWITCH:" + SWITCH);
                initControl(SWITCH);

            } else if (action.equals(Constants.RESPONSE_TIME)) {
                String SWITCH = intent.getStringExtra(Constants.SWITCH);
                System.out.println("RESPONSE_TIME:" + SWITCH);
            } else if (action.equals("first")){

                if (!first){
                    System.out.println("first");
                    switchStatus = true;
                    loadingInd.setVisibility(View.INVISIBLE);
                }

            }
        }
    }

    private void initControl(String SWITCH) {
//        mInSwitchControl = false;
        try {
            JSONObject object = new JSONObject(SWITCH);
            JSONObject data = object.optJSONObject("Data");
            String status = data.optString("Status");
            String DeviceID = object.optString("DeviceID");
            if (DeviceID.equals(mac + "2G")) {

                mInSwitchControl = true;

                onLine = true;

                if (status.equals("ON")) {
//                switch_control.setEnabled(true);

                    if (switchStatus) {
                        if (action.equals("OFF")) {
                            Toast.makeText(getActivity(), "设备繁忙！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    switch_control.setBackgroundResource(R.drawable.mini_power_on_selector);
                    action = "OFF";
                } else if (status.equals("OFF")){
//                switch_control.setEnabled(true);

                    if (switchStatus) {
                        if (action.equals("ON")) {
                            Toast.makeText(getActivity(), "设备繁忙！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    switch_control.setBackgroundResource(R.drawable.mini_power_off_selector);
                    action = "ON";
                }
                if (!first){
                    loadingInd.setVisibility(View.INVISIBLE);
                }

                switchStatus = true;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mTimer != null){
//            mTimer = new Timer();
//            mTimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    if (sub){
//                        System.out.println("aaa");
//                    } else {
//                        System.out.println("sss");
//                    }
//                }
//            },300,02000);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        sub = ((DeviceActivity) activity).Subscribe();
    }


    private void init(View view) {
        switch_control = view.findViewById(R.id.switch_control);
        loadingInd = view.findViewById(R.id.loadingInd);

        if (status.equals("ON")) {
            switch_control.setBackgroundResource(R.drawable.mini_power_on_selector);
            action = "OFF";
        } else if (status.equals("OFF")) {
            switch_control.setBackgroundResource(R.drawable.mini_power_off_selector);
            action = "ON";
        }

        switch_control.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {

//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        //execute the task
//
//                        Thread query = new Thread(runnable3);
//                        query.start();
//
//                    }
//                }, 3000);

//                continuousClick();
//                switchStatus = true;

                if (switchStatus) {

                    switchStatus  = false;

                    loadingInd.setVisibility(View.VISIBLE);

                    if (connect != null) {
                        connect.interrupt();
                    }

                    connect = new Thread(controlSwitch);
                    connect.start();

                }

            }
        });
    }

    private Thread connect;

    Runnable controlSwitch = new Runnable() {
        @Override
        public void run() {

            if (mqttFor2G != null && mqttFor2G.isConnect()) {

                if (mqttFor2G.publish(action, mac, getActivity())) {
//                    System.out.println("打开");
                    mInSwitchControl = false;
                    Message message = new Message();
                    Bundle data = new Bundle();
                    data.putString("message", action);
                    data.putString("mac", "");
                    message.setData(data);
                    handler.sendMessage(message);
                }

                try {
                    Thread.sleep(5000);
                    if (!mInSwitchControl) {
                        onLine = false;
                        Message message = new Message();
                        Bundle data = new Bundle();
                        data.putString("message", "OFFLINEDevice");
                        data.putString("mac", "");
                        message.setData(data);
                        handler.sendMessage(message);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                Message message = new Message();
                Bundle data = new Bundle();
                data.putString("message", "OFFLINE");
                data.putString("mac", "");
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
            String message = data.getString("message");
            String mac = data.getString("mac", "");

            if (message.equals("SUCCESS")) {
            } else if (message.equals("ON")) {
//                switch_control.setEnabled(false);
//                message_tv.setText("发送开命令成功:"+"/DYX/2GPLUG/"+mac_Ed.getText().toString()+"2G");
            } else if (message.equals("OFF")) {
//                switch_control.setEnabled(false);
//                message_tv.setText("发送关命令成功:"+"/DYX/2GPLUG/"+mac_Ed.getText().toString()+"2G");
            } else if (message.equals("QUERY")) {
            } else if (message.equals("OFFLINE")) {
            } else if (message.equals("OFFLINEDevice")) {
//                if (myProgressDialog != null && myProgressDialog.isShowing()) {
//                    myProgressDialog.dismiss();

                loadingInd.setVisibility(View.INVISIBLE);

                switchStatus = true;

                Toast.makeText(getActivity(), "设备已离线，请勿重复操作！", Toast.LENGTH_SHORT).show();

//                }
            }
        }
    };

}
