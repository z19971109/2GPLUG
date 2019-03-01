package smart2g.dyx.com.a2gplug;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.ListView;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Timer;
        import java.util.TimerTask;

public class TimerFragment extends Fragment {

    private ListView timer_list;

    private Button btn_add_timer;

    private List<TimerInstance> lists;

    private TimerListAdapter adapter;

    private TimerReceiver timerReceiver = new TimerReceiver();

    private MQTTFor2G mqttFor2G;

    private Bundle bundle;

    private String mac;

    private int[] indexS = new int[4];

    private boolean queryB = false;
    private boolean controlB = false;
    private boolean deleteB = false;

    private Timer mTimer;

    private int queryS = 0;
    private int controlId;
    private int controlIndex;

    private String[] mTimeTaskTypeArray;

    private MyProgressDialog myProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_layout, container, false);

        mTimeTaskTypeArray = getResources().getStringArray(R.array.time_task_type_array);

        bundle = this.getArguments();
        mac = bundle.getString("mac", "");

        for (int i = 0; i < 4; i++) {
            indexS[i] = 0;
        }

        myProgressDialog = MyProgressDialog.createDialog(getActivity());

        init(view);

        mqttFor2G = ApplicationFor2G.mqttFor2G;

        new Thread(queryTimer).start();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.RESPONSE_SYSTEM);
        intentFilter.addAction(Constants.RESPONSE_TIMER);
        intentFilter.addAction(Constants.REPORT_TIMER);
        getActivity().registerReceiver(timerReceiver, intentFilter);

        return view;
    }

    Runnable queryTimer = new Runnable() {
        @Override
        public void run() {
            if (mqttFor2G != null && mqttFor2G.isConnect()) {
//                for (int i = 1; i < 5; i++) {
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                mqttFor2G.queryTimer(mac, 0);

//                }

//                queryB = true;

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
//        mTimer.cancel();
        queryB = false;
        getActivity().unregisterReceiver(timerReceiver);
    }

    private Thread control;

    Runnable controlTimer = new Runnable() {
        @Override
        public void run() {
            TimerInstance instance = lists.get(controlId);
            String controlAction = "ENABLE";
            if (instance.getStatus().equals("ENABLE")) {
                controlAction = "DISABLE";
            } else if (instance.getStatus().equals("DISABLE")) {
                controlAction = "ENABLE";
            }
            controlIndex = instance.getIndex();
            if (mqttFor2G != null && mqttFor2G.isConnect()) {
                if (mqttFor2G.controlTimer(controlIndex, mac, controlAction)) {
                    ControlFragment.onLine = false;
                    try {
                        Thread.sleep(6000);

                        if (myProgressDialog != null && myProgressDialog.isShowing()) {

                            System.out.println("超时");

                            Message message = new Message();
                            Bundle data = new Bundle();
                            data.putBoolean("control", false);
                            message.setData(data);
                            handler.sendMessage(message);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


//                    Message message = new Message();
//                    Bundle data = new Bundle();
//                    data.putBoolean("control", true);
//                    message.setData(data);
//                    handler.sendMessage(message);
                }
            }

        }
    };

    Runnable deleteTimer = new Runnable() {
        @Override
        public void run() {
            if (mqttFor2G != null && mqttFor2G.isConnect()){
                if (mqttFor2G.controlTimer(controlIndex,mac,"DELETE")){
                    ControlFragment.onLine = false;

                    try {
                        Thread.sleep(6000);

                        if (myProgressDialog != null && myProgressDialog.isShowing()) {
                            Message message = new Message();
                            Bundle data = new Bundle();
                            data.putBoolean("control", false);
                            message.setData(data);
                            handler.sendMessage(message);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            boolean control = data.getBoolean("control", true);
            if (control) {

            } else {
                if (myProgressDialog != null && myProgressDialog.isShowing()) {
                    Toast.makeText(getActivity(),"设备已离线！",Toast.LENGTH_SHORT).show();
                    controlB = false;
                    myProgressDialog.dismiss();
                }
//                Toast.makeText(getActivity(),"请操作不要太频繁哟！",Toast.LENGTH_SHORT).show();
            }

        }
    };

    private void init(View view) {
        timer_list = (ListView) view.findViewById(R.id.timer_list);
        btn_add_timer = (Button) view.findViewById(R.id.btn_add_timer);

        lists = new ArrayList<>();
        adapter = new TimerListAdapter(getActivity(), lists);
        timer_list.setAdapter(adapter);

        adapter.setOnClickMyTextView(new TimerListAdapter.onClickMyButtonView() {
            @Override
            public void myTextViewClick(final int id) {

                TimerInstance instance = lists.get(id);
                String mode = instance.getMode();
                String status = instance.getStatus();
                if (mode.equals("ONCE") && status.equals("ENABLE")){
                    PLUGAlert.showAlert(getActivity(), "该组任务为仅此一次的任务，取消任务会同时删除改任务", "是否取消?", null, null, new PLUGAlert.OnAlertSelectId() {
                        @Override
                        public void onClick(int whichButton) {
                            switch (whichButton){
                                case 0:
                                    TimerInstance instance = lists.get(id);

                                    controlIndex = instance.getIndex();

                                    myProgressDialog.setMessage("删除中...");
                                    myProgressDialog.show();

                                    if (delete != null){
                                        delete.interrupt();
                                    }

                                    delete = new Thread(deleteTimer);
                                    delete.start();
                                    break;
                            }
                        }
                    });
                } else {
                    myProgressDialog.setMessage("控制中...");
                    myProgressDialog.show();
                    controlId = id;
                    if (control != null){
                        control.interrupt();
                    }
                    control = new Thread(controlTimer);
                    control.start();
                }


            }
        });

        timer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimerInstance instance = lists.get(position);
                Intent intent = new Intent(getActivity(), TimerEditActivity.class);
                intent.putExtra("add", false);
                intent.putExtra("STime", instance.getSTime());
                intent.putExtra("ETime", instance.getETime());
                intent.putExtra("UserDaySet", instance.getUserDaySet());
                intent.putExtra("SwitchAction", instance.getSwitchAction());
                intent.putExtra("Index", instance.getIndex());
                intent.putExtra("Mode",instance.getMode());
                intent.putExtra("mac",mac);
                startActivity(intent);
            }
        });

        timer_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PLUGAlert.showAlert(getActivity(), "", null, null, null, new PLUGAlert.OnAlertSelectId() {
                    @Override
                    public void onClick(int whichButton) {
                        switch (whichButton){
                            case 0:

                                TimerInstance instance = lists.get(position);

                                controlIndex = instance.getIndex();

                                myProgressDialog.setMessage("删除中...");
                                myProgressDialog.show();

                                if (delete != null){
                                    delete.interrupt();
                                }

                                delete = new Thread(deleteTimer);
                                delete.start();

                                break;
                        }
                    }
                });
                return true;
            }
        });

        btn_add_timer.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {

                if (getNumber() == 0){
                    Toast.makeText(getActivity(), "最多同时存在4个定时任务！", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), TimerEditActivity.class);
                    intent.putExtra("add", true);
                    intent.putExtra("mac", mac);
                    intent.putExtra("Index", getNumber());

                    startActivity(intent);
                }

            }
        });
    }

    private Thread delete ;


    private class TimerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.RESPONSE_SYSTEM)) {

            } else if ((action.equals(Constants.RESPONSE_TIMER))) {
                String SWITCH = intent.getStringExtra(Constants.SWITCH);

                System.out.println("RESPONSE_TIMER:" + SWITCH);

                try {
                    JSONObject object = new JSONObject(SWITCH);
//                    int Index = object.optInt("Index", 0);


                    HashMap<String, Object> map = new HashMap<>();
                    map.put(Information.TIMERLIST, "");
                    map.put(Information.GETTIMERLIST, "");
                    map.put(Information.SETTIMER, "");
                    map.put(Information.GETSETTIMER, "");
                    map.put(Information.CONTROLTIMER, "");
                    map.put(Information.GETCONTROLTIMER, "");

                    Information.saveInformation(getActivity(), Information.PLUG, map);

                    JSONObject data = object.optJSONObject("Data");
                    String DeviceID = object.optString("DeviceID");
                    if (DeviceID.equals(mac+"2G")){

                        ControlFragment.first = false;

                        Intent first = new Intent();
                        first.setAction("first");
                        getActivity().sendBroadcast(first);

                        ControlFragment.onLine = true;

                        if (data != null) {
                            String Status = data.optString("Status");
                            String STime = data.optString("STime");
                            String ETime = data.optString("ETime");
                            String Mode = data.optString("Mode");
                            int UserDaySet = data.optInt("UserDaySet", 0);
                            String SwitchAction = data.optString("SwitchAction");
                            int Index = object.optInt("Index", 0);

                            boolean exist = false;

                            for (int i = 1; i < 5; i++) {
                                if (Index == i) {
                                    indexS[i - 1] = i;
                                }
                            }

                            int setInt = 0;

                            for (int i = 0; i < lists.size(); i++) {
                                TimerInstance instance = lists.get(i);
                                int listsIndex = instance.getIndex();
                                if (listsIndex == Index) {
                                    setInt = i;
                                    exist = true;
                                } else {

                                }
                            }
                            System.out.println("setInt:"+setInt);
                            System.out.println("exist:" + exist);
                            TimerInstance instance = new TimerInstance(Status, STime, ETime, Mode, UserDaySet, SwitchAction, Index);
                            if (!exist) {
                                if (Status.equals("DELETED") || (Status.equals("DISABLE") && Mode.equals("ONCE"))){

                                } else {
                                    lists.add(instance);
                                    ApplicationFor2G.timerLists.add(instance);
                                }
                            } else {
                                if (Status.equals("DELETED") || (Status.equals("DISABLE") && Mode.equals("ONCE"))){
                                    lists.remove(setInt);
                                    ApplicationFor2G.timerLists.remove(instance);
                                } else {
                                    lists.set(setInt, instance);
                                    ApplicationFor2G.timerLists.set(setInt,instance);
                                }
                            }

                            if (Index == controlIndex) {
                                if (myProgressDialog != null && myProgressDialog.isShowing()) {
                                    myProgressDialog.dismiss();
                                    controlB = false;
                                }
                            }

                        } else {

                            JSONArray dataArray = object.optJSONArray("Data");
                            if (dataArray != null){

                                lists.clear();

                                ApplicationFor2G.timerLists.clear();

                                for (int i = 0 ; i < dataArray.length() ; i++){
                                    JSONObject dataObject = dataArray.optJSONObject(i);
                                    String Status = dataObject.optString("Status");
                                    String STime = dataObject.optString("STime");
                                    String ETime = dataObject.optString("ETime");
                                    String Mode = dataObject.optString("Mode");
                                    int UserDaySet = dataObject.optInt("UserDaySet", 0);
                                    String SwitchAction = dataObject.optString("SwitchAction");
                                    int Index = dataObject.optInt("Index", 0);

                                    if (Status.equals("DELETED") || (Status.equals("DISABLE") && Mode.equals("ONCE"))){

                                    } else {
                                        TimerInstance instance = new TimerInstance(Status, STime, ETime, Mode, UserDaySet, SwitchAction, Index);
                                        lists.add(instance);

                                        ApplicationFor2G.timerLists.add(instance);
                                    }
                                }
                            }
                        }
                    }


//                    JSONObject data = object.optJSONObject("Data");
//                    String Status = data.optString("Status");
//                    String STime = data.optString("STime");
//                    String ETime = data.optString("ETime");
//                    String Mode = data.optString("Mode");
//                    int UserDaySet = data.optInt("UserDaySet", 0);
//                    String SwitchAction = data.optString("SwitchAction");
//                    TimerInstance instance = new TimerInstance(Status, STime, ETime, Mode, UserDaySet, SwitchAction, Index);
//
//                    int j = 6899;
//
//                    for (int i = 0; i < lists.size(); i++) {
//                        if (Index == lists.get(i).getIndex()) {
//                            j = i;
//                        }
//                    }
//                    if (j == 6899) {
//                        lists.add(instance);
//                    } else {
//                        lists.set(j, instance);
//                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if ((action.equals(Constants.REPORT_TIMER))) {
                String SWITCH = intent.getStringExtra(Constants.SWITCH);

                try {
                    JSONObject object = new JSONObject(SWITCH);
                    String DeviceID = object.optString("DeviceID");
                    if (DeviceID.equals(mac+"2G")){

                        lists.clear();

                        ApplicationFor2G.timerLists.clear();

                        ControlFragment.onLine = true;
                        JSONArray dataArray = object.optJSONArray("Data");
                        if (dataArray != null){
                            for (int i = 0 ; i < dataArray.length() ; i++){
                                JSONObject dataObject = dataArray.optJSONObject(i);
                                String Status = dataObject.optString("Status");
                                String STime = dataObject.optString("STime");
                                String ETime = dataObject.optString("ETime");
                                String Mode = dataObject.optString("Mode");
                                int UserDaySet = dataObject.optInt("UserDaySet", 0);
                                String SwitchAction = dataObject.optString("SwitchAction");
                                int Index = dataObject.optInt("Index", 0);

                                if (Status.equals("DELETED") || (Status.equals("DISABLE") && Mode.equals("ONCE"))){

                                } else {
                                    TimerInstance instance = new TimerInstance(Status, STime, ETime, Mode, UserDaySet, SwitchAction, Index);
                                    lists.add(instance);

                                    ApplicationFor2G.timerLists.add(instance);
                                }

                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (myProgressDialog != null && myProgressDialog.isShowing()) {
                        myProgressDialog.dismiss();
                        controlB = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int getNumber(){
        int getNumber = 0;
        String numberList = "";

        for (int i = 0 ; i < lists.size() ; i++){
            TimerInstance instance = lists.get(i);
            numberList = numberList + instance.getIndex();
        }

        for (int i = 1 ; i <5 ; i++){
            if (numberList.indexOf(i+"") >= 0){

            } else {
                getNumber = i;
                break;
            }
        }
        System.out.println("getNumber:"+getNumber);
        return getNumber;
    }
}

