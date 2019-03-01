package smart2g.dyx.com.a2gplug;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimerEditActivity extends Activity {

    private RelativeLayout period_start_time_layout, period_end_time_layout, select_week_layout;

    private TextView start_time_text, end_time_text, weeks;

    private Button confirm_timer_bt, period_on_time_enable_button, period_off_time_enable_button, cancel_timer_bt;

    private int[] mWeeks = new int[7];

    private int weeksInt = 0;

    private boolean task_bo = true;

    private String mac;

    private boolean add;

    private String STime, ETime, SwitchAction, Mode;

    private int UserDaySet, Index;

    private MQTTFor2G mqttFor2G;

    private MyProgressDialog myProgressDialog;

    private SetTimerReceiver setTimerReceiver = new SetTimerReceiver();

    private boolean setB = false;

    private int forWeeksInt = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sp_timer_edit_layout);
        mqttFor2G = ApplicationFor2G.mqttFor2G;

        myProgressDialog = MyProgressDialog.createDialog(TimerEditActivity.this);

        add = getIntent().getBooleanExtra("add", false);
        if (add) {
            Index = getIntent().getIntExtra("Index", 0);
        } else {
            STime = getIntent().getStringExtra("STime");
            ETime = getIntent().getStringExtra("ETime");
            SwitchAction = getIntent().getStringExtra("SwitchAction");
            UserDaySet = getIntent().getIntExtra("UserDaySet", 0);

            weeksInt = UserDaySet;

            Index = getIntent().getIntExtra("Index", 0);
            Mode = getIntent().getStringExtra("Mode");
        }
        mac = getIntent().getStringExtra("mac");
        init();
        setListener();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.RESPONSE_TIMER);
        registerReceiver(setTimerReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(setTimerReceiver);
    }

    private class SetTimerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.RESPONSE_TIMER)) {
                String SWITCH = intent.getStringExtra(Constants.SWITCH);
                System.out.println("setTimer:" + SWITCH);
                try {
                    JSONObject object = new JSONObject(SWITCH);

                    String DeviceID = object.optString("DeviceID");
                    if (DeviceID.equals(mac + "2G")) {

                        ControlFragment.onLine = true;

                        int setTimerIndex = object.optInt("Index");
                        if (setTimerIndex == Index && setB) {

                            if (myProgressDialog != null && myProgressDialog.isShowing()) {
                                myProgressDialog.dismiss();
                            }
                            Toast.makeText(TimerEditActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void init() {
        period_start_time_layout = (RelativeLayout) findViewById(R.id.period_start_time_layout);
        period_end_time_layout = (RelativeLayout) findViewById(R.id.period_end_time_layout);
        select_week_layout = (RelativeLayout) findViewById(R.id.select_week_layout);
        start_time_text = (TextView) findViewById(R.id.start_time_text);
        end_time_text = (TextView) findViewById(R.id.end_time_text);
        weeks = (TextView) findViewById(R.id.weeks);
        confirm_timer_bt = (Button) findViewById(R.id.confirm_timer_bt);
        period_on_time_enable_button = findViewById(R.id.period_on_time_enable_button);
        period_off_time_enable_button = findViewById(R.id.period_off_time_enable_button);
//        task_able_bt = (Button) findViewById(R.id.task_able_bt);
        cancel_timer_bt = (Button) findViewById(R.id.cancel_timer_bt);
        if (STime != null && ETime != null && !STime.equals("") && !ETime.equals("")) {
            start_time_text.setText(STime);
            end_time_text.setText(ETime);
        } else {
            String time = getTime();
            String[] times = time.split(":");
            String hour = times[0];
            String min = times[1];
            String second = times[2];

            if (Integer.parseInt(min) < 55) {
                if (Integer.parseInt(min) < 5) {
                    min = "0" + (Integer.parseInt(min) + 5);
                } else {
                    min = (Integer.parseInt(min) + 5) + "";
                }
            } else {
                min = "0" + (Integer.parseInt(min) + 5 - 60);
                if (Integer.parseInt(hour) <= 23) {
                    hour = (Integer.parseInt(hour) + 1) + "";
                } else {
                    hour = "01";
                }

            }

            start_time_text.setText(hour + ":" + min + ":" + second);

            if (Integer.parseInt(min) < 55) {
                if (Integer.parseInt(min) < 5) {
                    min = "0" + (Integer.parseInt(min) + 5);
                } else {
                    min = (Integer.parseInt(min) + 5) + "";
                }
            } else {
                min = "0" + (Integer.parseInt(min) + 5 - 60);
                if (Integer.parseInt(hour) <= 23) {
                    hour = (Integer.parseInt(hour) + 1) + "";
                } else {
                    hour = "01";
                }

            }

            end_time_text.setText(hour + ":" + min + ":" + second);

        }

        if (SwitchAction != null) {
            if (SwitchAction.equals("ON")) {
//                task_able_bt.setBackgroundResource(R.drawable.switch_on);

                period_on_time_enable_button.setBackgroundResource(R.drawable.switch_on);
                period_off_time_enable_button.setBackgroundResource(R.drawable.switch_off);

                task_bo = true;
            } else if (SwitchAction.equals("OFF")) {
//                task_able_bt.setBackgroundResource(R.drawable.switch_off);

                period_on_time_enable_button.setBackgroundResource(R.drawable.switch_off);
                period_off_time_enable_button.setBackgroundResource(R.drawable.switch_on);

                task_bo = false;
            }
        }

        String result = Integer.toBinaryString(UserDaySet);

        if (Mode != null) {
            if (Mode.equals("ONCE")) {
                weeks.setText("执行一次");
            } else if (Mode.equals("EVERYDAY")) {
                weeks.setText("每天");
            } else if (Mode.equals("CUSTOM")) {


                String newResult = result;

                if (result.length() != 7) {

                    for (int i = 0; i < 7 - result.length(); i++) {
                        newResult = "0" + newResult;

                        System.out.println("resultNew:" + newResult);

                    }
                }


                int[] str = new int[newResult.length()];
                for (int i = 0; i < newResult.length(); i++) {
                    str[i] = Integer.parseInt(newResult.substring(i, i + 1));
                }
                weeks.setText(getweeks(str));
//            for (int i = 0 ; i < str.length ; i++){
//                System.out.println("str:"+str[i]);
//            }
            }
        }

    }

    private String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    private String getDay() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    Runnable setTimer = new Runnable() {
        @Override
        public void run() {
            if (mqttFor2G != null) {

                String mode = "ONCE";
                if (weeksInt == 127) {
                    mode = "EVERYDAY";
                } else if (weeksInt > 0 && weeksInt < 127) {
                    mode = "CUSTOM";
                } else if (weeksInt == 0) {
                    mode = "ONCE";
                }

                String Action = "ON";
                if (task_bo) {
                    Action = "ON";
                } else {
                    Action = "OFF";
                }

//                if (add) {
//                    if (mqttFor2G.setTimer(Index + 1, start_time_text.getText().toString(), end_time_text.getText().toString(), mode, weeksInt, Action, Constants.mac)) {
//
//                        Message message = new Message();
//                        Bundle data = new Bundle();
//                        data.putBoolean("Timer", true);
//                        message.setData(data);
//                        handler.sendMessage(message);
//
//                    }
//                } else {
                if (mqttFor2G.setTimer(Index, start_time_text.getText().toString(), end_time_text.getText().toString(), mode, weeksInt, Action, mac)) {
                    setB = true;

                    ControlFragment.onLine = false;

                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    if (myProgressDialog != null && myProgressDialog.isShowing()) {
                        Message message = new Message();
                        Bundle data = new Bundle();
                        data.putBoolean("Timer", false);
                        message.setData(data);
                        handler.sendMessage(message);
                    }
                }
//                }
            }
        }
    };

    private void setListener() {

        period_on_time_enable_button.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (task_bo) {
                    task_bo = false;
                    period_on_time_enable_button.setBackgroundResource(R.drawable.switch_off);
                    period_off_time_enable_button.setBackgroundResource(R.drawable.switch_on);
                } else {
                    task_bo = true;
                    period_on_time_enable_button.setBackgroundResource(R.drawable.switch_on);
                    period_off_time_enable_button.setBackgroundResource(R.drawable.switch_off);
                }
            }
        });

        period_off_time_enable_button.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (task_bo) {
                    task_bo = false;
                    period_on_time_enable_button.setBackgroundResource(R.drawable.switch_off);
                    period_off_time_enable_button.setBackgroundResource(R.drawable.switch_on);
                } else {
                    task_bo = true;
                    period_on_time_enable_button.setBackgroundResource(R.drawable.switch_on);
                    period_off_time_enable_button.setBackgroundResource(R.drawable.switch_off);
                }

            }
        });

        period_start_time_layout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                int hour, min, sec;
                try {
                    String[] time = start_time_text.getText().toString().split(":");
                    hour = Integer.parseInt(time[0]);
                    min = Integer.parseInt(time[1]);
                    sec = Integer.parseInt(time[2]);
                } catch (Exception e) {
                    hour = CommonUnit.getPhoneHour();
                    min = CommonUnit.getPhoneMin();
                    sec = CommonUnit.getPhoneSeconds();
                }
                BLTimerAlert.showSecondAlert(TimerEditActivity.this, hour, min, sec, new BLTimerAlert.OnSecondAlertClick() {

                    @Override
                    public void onClick(int hour, int min, int sec) {
                        start_time_text.setText(formatTime(hour, min, sec));
                    }
                });
            }
        });

        period_end_time_layout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                int hour, min, sec;

                try {
                    String[] time = end_time_text.getText().toString().split(":");
                    hour = Integer.parseInt(time[0]);
                    min = Integer.parseInt(time[1]);
                    sec = Integer.parseInt(time[2]);
                } catch (Exception e) {
                    hour = CommonUnit.getPhoneHour();
                    min = CommonUnit.getPhoneMin();
                    sec = CommonUnit.getPhoneSeconds();
                }
                BLTimerAlert.showSecondAlert(TimerEditActivity.this, hour, min, sec, new BLTimerAlert.OnSecondAlertClick() {

                    @Override
                    public void onClick(int hour, int min, int sec) {
                        end_time_text.setText(formatTime(hour, min, sec));
                    }
                });
            }
        });

        weeks.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                Intent intent = new Intent();
                intent.setClass(TimerEditActivity.this, A1SelectWeeksActivity.class);
                startActivityForResult(intent, 2);
            }
        });

//        task_able_bt.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void doOnClick(View v) {
//                if (task_bo) {
//                    task_bo = false;
//                    task_able_bt.setBackgroundResource(R.drawable.switch_off);
//                } else {
//                    task_bo = true;
//                    task_able_bt.setBackgroundResource(R.drawable.switch_on);
//                }
//            }
//        });
        confirm_timer_bt.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {

                String nowTime = getDay() + " " + getTime();

                String starTime = getDay() + " " + start_time_text.getText().toString();

                String endTime = getDay() + " " + end_time_text.getText().toString();

                long now = dateToStamp(nowTime);

                long start = dateToStamp(starTime);

                long end = dateToStamp(endTime);

                System.out.println("nowTime:" + nowTime + "|" + "starTime:" + starTime + "|" + "endTime:" + endTime);

                System.out.println("时间戳:" + now + "|" + start + "|" + end);

//                String nowTime[] = getTime().split(":");
//
//                int hour = Integer.parseInt(nowTime[0]);
//
//                int min = Integer.parseInt(nowTime[1]);
//
//                int sec = Integer.parseInt(nowTime[2]);
//
//                String startTime[] = start_time_text.getText().toString().split(":");
//
//                int starHour = Integer.parseInt(startTime[0]);
//
//                int startMin = Integer.parseInt(startTime[1]);
//
//                int startSec = Integer.parseInt(startTime[2]);
//
//                String endTime[] = end_time_text.getText().toString().split(":");
//
//                int endHour = Integer.parseInt(endTime[0]);
//
//                int endMin = Integer.parseInt(endTime[1]);
//
//                int endSec = Integer.parseInt(endTime[2]);

//                if (weeksInt == 0) {
                //仅一次
                if (now > start && weeksInt == 0) {
                    Toast.makeText(TimerEditActivity.this, "开始时间无法小于当前时间,否则仅一次定时任务将不会执行!", Toast.LENGTH_SHORT).show();
                } else if (start >= end) {
                    Toast.makeText(TimerEditActivity.this, "结束时间无法小于等于开始时间,否则定时任务将不会执行!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean add = true;

                    String time = "";

                    qos:
                    for (int i = 0; i < ApplicationFor2G.timerLists.size(); i++) {
                        TimerInstance instance = ApplicationFor2G.timerLists.get(i);
                        int index = instance.getIndex();
                        String mode = instance.getMode();
                        String status = instance.getStatus();
                        int days = instance.getUserDaySet();
                        if (index != Index && status.equals("ENABLE")) {
                            String sTime = instance.getSTime();
                            String eTime = instance.getETime();

                            time = sTime + "-" + eTime;

                            System.out.println("time:" + time);
                            System.out.println("mode:"+mode);

                            if (weeksInt == 0) {
                                if (mode.equals("ONCE")) {
                                    System.out.println("ONCEtoONCE");
                                    if (sTime.equals(start_time_text.getText().toString())) {
                                        add = false;
                                        break qos;
                                    } else if (eTime.equals(end_time_text.getText().toString())) {
                                        add = false;
                                        break qos;
                                    } else if (sTime.equals(end_time_text.getText().toString())) {
                                        add = false;
                                        break qos;
                                    } else if (eTime.equals(start_time_text.getText().toString())) {
                                        add = false;
                                        break qos;
                                    } else {
                                        add = true;
                                    }
                                } else if (mode.equals("EVERYDAY")) {
                                    System.out.println("ONCEtoEVERYDAY");
                                    if (sTime.equals(start_time_text.getText().toString())) {
                                        add = false;
                                        break qos;
                                    } else if (eTime.equals(end_time_text.getText().toString())) {
                                        add = false;
                                        break qos;
                                    } else if (sTime.equals(end_time_text.getText().toString())) {
                                        add = false;
                                        break qos;
                                    } else if (eTime.equals(start_time_text.getText().toString())) {
                                        add = false;
                                        break qos;
                                    } else {
                                        add = true;
                                    }

                                } else if (mode.equals("CUSTOM")) {
                                    System.out.println("ONCEtoCUSTOM");

                                    String week = getWeek();
                                    String[] weeksStrings = getResources().getStringArray(R.array.m1_week_array);
                                    int[] weekStr = new int[weeksStrings.length];
                                    for (int j = 0; j < weeksStrings.length; j++) {
                                        if (week.equals(weeksStrings[j])) {
                                            weekStr[j] = 1;
                                        } else {
                                            weekStr[j] = 0;
                                        }
                                    }

                                    String dayResult = Integer.toBinaryString(days);
                                    String newDayResult = dayResult;

                                    if (dayResult.length() != 7) {
                                        for (int j = 0; j < 7 - dayResult.length(); j++) {
                                            newDayResult = "0" + newDayResult;
                                        }
                                    }
                                    System.out.println("newDayResult:" + newDayResult);
                                    int[] dayStr = new int[newDayResult.length()];
                                    for (int j = 0; j < newDayResult.length(); j++) {
                                        dayStr[j] = Integer.parseInt(newDayResult.substring(j, j + 1));
                                    }
                                    for (int j = 0; j < dayStr.length; j++) {
                                        if (weekStr[j] == dayStr[j] && weekStr[j] == 1) {
                                            if (sTime.equals(start_time_text.getText().toString())) {
                                                add = false;
                                                break qos;
                                            } else if (eTime.equals(end_time_text.getText().toString())) {
                                                add = false;
                                                break qos;
                                            } else if (sTime.equals(end_time_text.getText().toString())) {
                                                add = false;
                                                break qos;
                                            } else if (eTime.equals(start_time_text.getText().toString())) {
                                                add = false;
                                                break qos;
                                            } else {
                                                add = true;
                                            }
                                        } else {
                                            add = true;
                                        }
                                    }

                                }
                            } else if (weeksInt == 127) {

                                if (sTime.equals(start_time_text.getText().toString())) {
                                    add = false;
                                    break qos;
                                } else if (eTime.equals(end_time_text.getText().toString())) {
                                    add = false;
                                    break qos;
                                } else if (sTime.equals(end_time_text.getText().toString())) {
                                    add = false;
                                    break qos;
                                } else if (eTime.equals(start_time_text.getText().toString())) {
                                    add = false;
                                    break qos;
                                } else {
                                    add = true;
                                }

                                if (mode.equals("ONCE")) {
                                    System.out.println("EVERYDAYtoONCE");
                                } else if (mode.equals("EVERYDAY")) {
                                    System.out.println("EVERYDAYtoEVERYDAY");
                                } else if (mode.equals("CUSTOM")) {
                                    System.out.println("EVERYDAYtoCUSTOM");
                                }
                            } else {
                                if (mode.equals("ONCE")) {
                                    System.out.println("CUSTOMtoONCE");

                                    String week = getWeek();
                                    String[] weeksStrings = getResources().getStringArray(R.array.m1_week_array);
                                    int[] weekStr = new int[weeksStrings.length];
                                    for (int j = 0; j < weeksStrings.length; j++) {
                                        if (week.equals(weeksStrings[j])) {
                                            weekStr[j] = 1;
                                        } else {
                                            weekStr[j] = 0;
                                        }
                                    }

                                    String dayResult = Integer.toBinaryString(weeksInt);
                                    String newDayResult = dayResult;

                                    if (dayResult.length() != 7) {
                                        for (int j = 0; j < 7 - dayResult.length(); j++) {
                                            newDayResult = "0" + newDayResult;
                                        }
                                    }
                                    System.out.println("newDayResult:" + newDayResult);
                                    int[] dayStr = new int[newDayResult.length()];
                                    for (int j = 0; j < newDayResult.length(); j++) {
                                        dayStr[j] = Integer.parseInt(newDayResult.substring(j, j + 1));
                                    }
                                    for (int j = 0; j < dayStr.length; j++) {
                                        if (weekStr[j] == dayStr[j] && weekStr[j] == 1) {
                                            if (sTime.equals(start_time_text.getText().toString())) {
                                                add = false;
                                                break qos;
                                            } else if (eTime.equals(end_time_text.getText().toString())) {
                                                add = false;
                                                break qos;
                                            } else if (sTime.equals(end_time_text.getText().toString())) {
                                                add = false;
                                                break qos;
                                            } else if (eTime.equals(start_time_text.getText().toString())) {
                                                add = false;
                                                break qos;
                                            } else {
                                                add = true;
                                            }
                                        } else {
                                            add = true;
                                        }
                                    }

                                } else if (mode.equals("EVERYDAY")) {
                                    System.out.println("CUSTOMtoEVERYDAY");

                                    if (sTime.equals(start_time_text.getText().toString())) {
                                        add = false;
                                        break qos;
                                    } else if (eTime.equals(end_time_text.getText().toString())) {
                                        add = false;
                                        break qos;
                                    } else if (sTime.equals(end_time_text.getText().toString())) {
                                        add = false;
                                        break qos;
                                    } else if (eTime.equals(start_time_text.getText().toString())) {
                                        add = false;
                                        break qos;
                                    } else {
                                        add = true;
                                    }

                                } else if (mode.equals("CUSTOM")) {
                                    System.out.println("CUSTOMtoCUSTOM");

                                    String result = Integer.toBinaryString(weeksInt);
                                    String newResult = result;

                                    if (result.length() != 7) {
                                        for (int j = 0; j < 7 - result.length(); j++) {
                                            newResult = "0" + newResult;
                                        }
                                    }

                                    System.out.println("newResult:" + newResult);

                                    int[] str = new int[newResult.length()];
                                    for (int j = 0; j < newResult.length(); j++) {
                                        str[j] = Integer.parseInt(newResult.substring(j, j + 1));
                                    }

                                    String dayResult = Integer.toBinaryString(days);
                                    String newDayResult = dayResult;

                                    if (dayResult.length() != 7) {
                                        for (int j = 0; j < 7 - dayResult.length(); j++) {
                                            newDayResult = "0" + newDayResult;
                                        }
                                    }
                                    System.out.println("newDayResult:" + newDayResult);

                                    int[] dayStr = new int[newDayResult.length()];
                                    for (int j = 0; j < newDayResult.length(); j++) {
                                        dayStr[j] = Integer.parseInt(newDayResult.substring(j, j + 1));
                                    }

                                    for (int j = 0; j < dayStr.length; j++) {
                                        if (str[j] == dayStr[j] && dayStr[j] == 1) {
                                            if (sTime.equals(start_time_text.getText().toString())) {
                                                add = false;
                                                break qos;
                                            } else if (eTime.equals(end_time_text.getText().toString())) {
                                                add = false;
                                                break qos;
                                            } else if (sTime.equals(end_time_text.getText().toString())) {
                                                add = false;
                                                break qos;
                                            } else if (eTime.equals(start_time_text.getText().toString())) {
                                                add = false;
                                                break qos;
                                            } else {
                                                add = true;
                                            }
                                        } else {
                                            add = true;
                                        }
                                    }

                                }
                            }

//                                if (weeksInt != 0 && weeksInt != 127) {
//
//                                    System.out.println("CUSTOM");
//
//                                    String result = Integer.toBinaryString(weeksInt);
//                                    String newResult = result;
//
//                                    if (result.length()!=7){
//                                        for (int j = 0 ; j < 7 - result.length() ; j++){
//                                            newResult = "0" +newResult;
//                                        }
//                                    }
//
//                                    System.out.println("newResult:"+newResult);
//
//                                    int [] str = new int[newResult.length()];
//                                    for(int j=0; j< newResult.length(); j++){
//                                        str[j] = Integer.parseInt(newResult.substring(j,j+1));
//                                    }
//
//                                    String dayResult = Integer.toBinaryString(days);
//                                    String newDayResult = dayResult;
//
//                                    if (dayResult.length()!= 7){
//                                        for (int j = 0 ; j < 7 - dayResult.length() ; j++){
//                                            newDayResult = "0" +newDayResult;
//                                        }
//                                    }
//                                    System.out.println("newDayResult:"+newDayResult);
//
//                                    int [] dayStr = new int[newDayResult.length()];
//                                    for(int j=0; j< newDayResult.length(); j++){
//                                        dayStr[j] = Integer.parseInt(newDayResult.substring(j,j+1));
//                                    }
//
//                                    for (int j = 0 ; j < dayStr.length ; j++){
//                                        if (str[j] == dayStr[j] && dayStr[j] == 1){
//                                            add = false;
//                                            break qos;
//                                        } else {
//                                            add = true;
//                                        }
//                                    }
//
//                                } else {
//
//                                    if (mode.equals("ONCE")){
//                                        System.out.println("ONCE");
//                                        String week = getWeek();
//                                        String[] weeksStrings = getResources().getStringArray(R.array.m1_week_array);
//                                        int [] weekStr = new int[weeksStrings.length];
//                                        for (int j = 0 ; j < weeksStrings.length ; j++){
//                                            if (week.equals(weeksStrings[j])){
//                                                weekStr[j] = 1;
//                                            } else {
//                                                weekStr[j] = 0;
//                                            }
//                                        }
//
//                                        String dayResult = Integer.toBinaryString(days);
//                                        String newDayResult = dayResult;
//
//                                        if (dayResult.length()!= 7){
//                                            for (int j = 0 ; j < 7 - dayResult.length() ; j++){
//                                                newDayResult = "0" +newDayResult;
//                                            }
//                                        }
//                                        System.out.println("newDayResult:"+newDayResult);
//                                        int [] dayStr = new int[newDayResult.length()];
//                                        for(int j=0; j< newDayResult.length(); j++){
//                                            dayStr[j] = Integer.parseInt(newDayResult.substring(j,j+1));
//                                        }
//
//                                        if (weeksInt == 0){
//                                            if (sTime.equals(start_time_text.getText().toString())){
//                                                add = false;
//                                                break;
//                                            } else if (eTime.equals(end_time_text.getText().toString())){
//                                                add = false;
//                                                break;
//                                            } else if (sTime.equals(end_time_text.getText().toString())){
//                                                add = false;
//                                                break;
//                                            } else if (eTime.equals(start_time_text.getText().toString())){
//                                                add = false;
//                                                break;
//                                            } else {
//                                                add = true;
//                                            }
//                                        } else {
//                                            for (int j = 0 ; j < dayStr.length ; j++){
//                                                if (weekStr[j] == dayStr[j] && weekStr[j] == 1){
//                                                    add = false;
//                                                    break qos;
//                                                } else {
//                                                    add = true;
//                                                }
//                                            }
//                                        }
//
//
//
//
//
//                                    } else {
//                                        System.out.println("EVERY");
//
//                                        if (sTime.equals(start_time_text.getText().toString())){
//                                            add = false;
//                                            break;
//                                        } else if (eTime.equals(end_time_text.getText().toString())){
//                                            add = false;
//                                            break;
//                                        } else if (sTime.equals(end_time_text.getText().toString())){
//                                            add = false;
//                                            break;
//                                        } else if (eTime.equals(start_time_text.getText().toString())){
//                                            add = false;
//                                            break;
//                                        } else {
//                                            add = true;
//                                        }
//                                    }
//                                }

                        } else {
                            add = true;
                        }
                    }

                    System.out.println("add:" + add);

                    if (add) {
                        myProgressDialog.setMessage("设置中...");
                        myProgressDialog.show();

                        Thread thread = new Thread(setTimer);
                        thread.start();
                    } else {
                        Toast.makeText(TimerEditActivity.this, "时间设置与" + time + "任务冲突，请重新选择时间!", Toast.LENGTH_SHORT).show();
                    }

                }

//                }  else {
//                    myProgressDialog.setMessage("设置中...");
//                    myProgressDialog.show();
//
//                    Thread thread = new Thread(setTimer);
//                    thread.start();
//                }

            }
        });

        cancel_timer_bt.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                finish();
            }
        });
    }

    public static long dateToStamp(String s) {
        long ts = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ts = date.getTime();
        return ts;
    }

    /*获取星期几*/
    public static String getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            default:
                return "";
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            boolean Timer = data.getBoolean("Timer", false);
            if (Timer) {
//                Toast.makeText(TimerEditActivity.this,"定时任务设置成功!",Toast.LENGTH_SHORT).show();
//                finish();
            } else {
                if (myProgressDialog != null && myProgressDialog.isShowing()) {
                    myProgressDialog.dismiss();
                    setB = false;
                    Toast.makeText(TimerEditActivity.this, "设备已离线！", Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(TimerEditActivity.this,"控制超时！",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private String formatTime(int hour, int min, int sec) {
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2) {
            mWeeks = data.getIntArrayExtra("TimerWeeks");
            weeks.setText(getweeks2(mWeeks));
            weeksInt = data.getIntExtra("TimerWeeksInt", 0);
            forWeeksInt = weeksInt;
            System.out.println("weeksInt:" + weeksInt);
        }
    }

    private String getweeks2(int[] weeks) {

        String selectWeek = "";
        String[] weeksStrings = getResources().getStringArray(R.array.week_array);
        for (int i = 0; i < weeks.length; i++) {
            if (weeks[i] == 1)
                selectWeek = selectWeek + "  " + weeksStrings[i];
        }

        if (selectWeek.equals("")) {
            return getString(R.string.run_one_time);
        } else {
            return selectWeek;
        }
    }

    private String getweeks(int[] weeks) {

        String selectWeek = "";
        String[] weeksStrings = getResources().getStringArray(R.array.m1_week_array);
        for (int i = weeksStrings.length - 1; i >= 0; i--) {
            if (weeks[i] == 1) {
                selectWeek = selectWeek + "  " + weeksStrings[i];
//                System.out.println("select:"+selectWeek);
            }

        }

        if (selectWeek.equals("")) {
            return getString(R.string.run_one_time);
        } else {
            return selectWeek;
        }
    }
}
