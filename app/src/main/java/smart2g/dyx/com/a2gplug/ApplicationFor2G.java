package smart2g.dyx.com.a2gplug;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.renderscript.ScriptIntrinsicYuvToRGB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApplicationFor2G extends Application {

    public static  MQTTFor2G mqttFor2G;

    public static List<MacLists> lists;

    public static List<TimerInstance> timerLists;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final ICallBack iCallBack = new ICallBack() {

                @Override
                public void onConnected(String s , String topic) {
//                Toast.makeText(MainActivity.this,s+"上线",Toast.LENGTH_SHORT).show();
//                System.out.println("在线："+s.substring(0,s.length()-1) + "\n"+ topic);
                    String news = s;

                    try {
                        JSONObject jsonObject = new JSONObject(news);
                        String DeviceID = jsonObject.optString("DeviceID");
//                        if (DeviceID.equals(Constants.mac+"2G")){
                            System.out.println("circularTaskResult:"+news);
                            String Type = jsonObject.optString("Type");
                            String Object = jsonObject.optString("Object");
                            int Index = jsonObject.optInt("Index",-1);

                            if (Type.equals("RESPONSE") && Object.equals("SWITCH")){

                                HashMap<String,Object> map = new HashMap<>();
                                map.put(Information.GETQUERYSWITCH,news);
                                Information.saveInformation(getApplicationContext(),Information.PLUG,map);

                                Intent intent = new Intent();
                                intent.setAction(Constants.RESPONSE_SWITCH);
                                intent.putExtra(Constants.SWITCH,news);
                                getApplicationContext().sendBroadcast(intent);
                            } else if (Type.equals("REPORT") && Object.equals("SWITCH")){
                                Intent intent = new Intent();
                                intent.setAction(Constants.REPORT_SWITCH);
                                intent.putExtra(Constants.SWITCH,news);
                                getApplicationContext().sendBroadcast(intent);
                            } else if (Type.equals("RESPONSE") && Object.equals("SYSTEM")){
                                HashMap<String,Object> map = new HashMap<>();
                                map.put(Information.GETQUERY,news);
                                Information.saveInformation(getApplicationContext(),Information.PLUG,map);

                                Intent intent = new Intent();
                                intent.setAction(Constants.RESPONSE_SYSTEM);
                                intent.putExtra(Constants.SWITCH,news);
                                getApplicationContext().sendBroadcast(intent);
                            } else if (Type.equals("RESPONSE") && Object.equals("TIMER")){
                                if (Index != -1){
                                    HashMap<String,Object> map = new HashMap<>();
                                    map.put(Information.GETTIMERLIST+Index,news);
                                    map.put(Information.GETSETTIMER+Index,news);
                                    map.put(Information.GETCONTROLTIMER+Index,news);
                                    Information.saveInformation(getApplicationContext(),Information.PLUG,map);
                                }

                                Intent intent = new Intent();
                                intent.setAction(Constants.RESPONSE_TIMER);
                                intent.putExtra(Constants.SWITCH,news);
                                getApplicationContext().sendBroadcast(intent);
                            }  else if (Type.equals("REPORT") && Object.equals("SYSTEM")){
                                Intent intent = new Intent();
                                intent.setAction(Constants.REPORT_SYSTEM);
                                intent.putExtra(Constants.SWITCH,news);
                                getApplicationContext().sendBroadcast(intent);
                            } else if (Type.equals("RESPONSE") && Object.equals("TIME")){
                                Intent intent = new Intent();
                                intent.setAction(Constants.RESPONSE_TIME);
                                intent.putExtra(Constants.SWITCH,news);
                                getApplicationContext().sendBroadcast(intent);
                            } else if (Type.equals("REPORT") && Object.equals("TIMER")){
                                Intent intent = new Intent();
                                intent.setAction(Constants.REPORT_TIMER);
                                intent.putExtra(Constants.SWITCH,news);
                                getApplicationContext().sendBroadcast(intent);
                            }
//                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("s非JSON:"+s);
                    }
//
//                Message message = new Message();
//                Bundle data = new Bundle();
//                data.putString("message","SUCCESS");
//                data.putString("mac",s);
//                message.setData(data);
//                handler.sendMessage(message);

                }
            };

            lists = new ArrayList<>();
            timerLists = new ArrayList<>();

            mqttFor2G = new MQTTFor2G(iCallBack , getApplicationContext(),lists);
            mqttFor2G.connect();
            System.out.println("mqtt开始");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();


        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put(Information.QUERY,"");
        hashMap.put(Information.GETQUERY,"");
        hashMap.put(Information.CONTROLSWITCH,"");
        hashMap.put(Information.GETCONTROLSWITCH,"");
        hashMap.put(Information.QUERYSWITCH,"");
        hashMap.put(Information.GETQUERYSWITCH,"");
        hashMap.put(Information.TIMERLIST,"");
        hashMap.put(Information.GETTIMERLIST,"");
        hashMap.put(Information.TIMERLIST1,"");
        hashMap.put(Information.GETTIMERLIST1,"");
        hashMap.put(Information.TIMERLIST2,"");
        hashMap.put(Information.GETTIMERLIST2,"");
        hashMap.put(Information.TIMERLIST3,"");
        hashMap.put(Information.GETTIMERLIST3,"");
        hashMap.put(Information.TIMERLIST4,"");
        hashMap.put(Information.GETTIMERLIST4,"");
        hashMap.put(Information.TIMERLIST5,"");
        hashMap.put(Information.GETTIMERLIST5,"");
        hashMap.put(Information.SETTIMER,"");
        hashMap.put(Information.GETSETTIMER,"");
        hashMap.put(Information.SETTIMER1,"");
        hashMap.put(Information.GETSETTIMER1,"");
        hashMap.put(Information.SETTIMER2,"");
        hashMap.put(Information.GETSETTIMER2,"");
        hashMap.put(Information.SETTIMER3,"");
        hashMap.put(Information.GETSETTIMER3,"");
        hashMap.put(Information.SETTIMER4,"");
        hashMap.put(Information.GETSETTIMER4,"");
        hashMap.put(Information.SETTIMER5,"");
        hashMap.put(Information.GETSETTIMER5,"");
        hashMap.put(Information.CONTROLTIMER,"");
        hashMap.put(Information.GETCONTROLTIMER,"");
        hashMap.put(Information.CONTROLTIMER1,"");
        hashMap.put(Information.GETCONTROLTIMER1,"");
        hashMap.put(Information.CONTROLTIMER2,"");
        hashMap.put(Information.GETCONTROLTIMER2,"");
        hashMap.put(Information.CONTROLTIMER3,"");
        hashMap.put(Information.GETCONTROLTIMER3,"");
        hashMap.put(Information.CONTROLTIMER4,"");
        hashMap.put(Information.GETCONTROLTIMER4,"");
        hashMap.put(Information.CONTROLTIMER5,"");
        hashMap.put(Information.GETCONTROLTIMER5,"");

        Information.saveInformation(getApplicationContext(),Information.PLUG,hashMap);

        new Thread(runnable).start();
    }


}
