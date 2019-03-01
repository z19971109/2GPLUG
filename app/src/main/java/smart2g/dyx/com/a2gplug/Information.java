package smart2g.dyx.com.a2gplug;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/21.
 */

public class Information {

    public static String LOGIN = "Login";

    public static String LOGIN_USER_NAME = "LoginUserName";

    public static String LOGIN_PASSWORD = "LoginPassword";

    public static String LOGIN_TOKEN = "LoginToken";

    public static String PLUG = "PLUG";

    public static String QUERY = "query";

    public static String GETQUERY = "getQuery";

    public static String CONTROLSWITCH = "controlSwitch";

    public static String GETCONTROLSWITCH = "getControlSwitch";

    public static String QUERYSWITCH = "querySwitch";

    public static String GETQUERYSWITCH = "getQuerySwitch";

    public static String QUERYTIME = "queryTime";

    public static String GETQUERYTIME = "getQueryTime";

    public static String TIMERLIST = "timerList";

    public static String GETTIMERLIST = "getTimerList";

    public static String TIMERLIST1 = "timerList1";

    public static String GETTIMERLIST1 = "getTimerList1";

    public static String TIMERLIST2 = "timerList2";

    public static String GETTIMERLIST2 = "getTimerList2";

    public static String TIMERLIST3 = "timerList3";

    public static String GETTIMERLIST3 = "getTimerList3";

    public static String TIMERLIST4 = "timerList4";

    public static String GETTIMERLIST4 = "getTimerList4";

    public static String TIMERLIST5 = "timerList5";

    public static String GETTIMERLIST5 = "getTimerList5";

    public static String SETTIMER = "setTimer";

    public static String GETSETTIMER = "getSetTimer";

    public static String SETTIMER1 = "setTimer";

    public static String GETSETTIMER1 = "getSetTimer";

    public static String SETTIMER2 = "setTimer2";

    public static String GETSETTIMER2 = "getSetTimer2";

    public static String SETTIMER3 = "setTimer3";

    public static String GETSETTIMER3 = "getSetTimer3";

    public static String SETTIMER4 = "setTimer4";

    public static String GETSETTIMER4 = "getSetTimer4";

    public static String SETTIMER5= "setTimer5";

    public static String GETSETTIMER5 = "getSetTimer5";

    public static String CONTROLTIMER = "controlTimer";

    public static String GETCONTROLTIMER = "getControlTimer";

    public static String CONTROLTIMER1 = "controlTimer1";

    public static String GETCONTROLTIMER1 = "getControlTimer1";

    public static String CONTROLTIMER2 = "controlTimer2";

    public static String GETCONTROLTIMER2 = "getControlTimer2";

    public static String CONTROLTIMER3 = "controlTimer3";

    public static String GETCONTROLTIMER3 = "getControlTimer3";

    public static String CONTROLTIMER4 = "controlTimer4";

    public static String GETCONTROLTIMER4 = "getControlTimer4";

    public static String CONTROLTIMER5 = "controlTimer5";

    public static String GETCONTROLTIMER5 = "getControlTimer5";

    public static void saveInformation(Context context , String category , Map<String , Object> map){
        if (map == null || map.isEmpty())
            return;
        SharedPreferences sp = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Object value = map.get(key);
            if (value instanceof Boolean)
                editor.putBoolean(key, (Boolean) value);
            else if (value instanceof Integer)
                editor.putInt(key, (Integer) value);
            else if (value instanceof Long)
                editor.putLong(key, (Long) value);
            else if (value instanceof Float)
                editor.putFloat(key, (Float) value);
            else if (value instanceof Double)
                editor.putFloat(key, ((Double) value).floatValue());
            else if (value instanceof Bitmap) {
                Bitmap bitmap = (Bitmap) value;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                String imageBase64 = new String(android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.DEFAULT));
                editor.putString(key, imageBase64);
            } else if (value instanceof String) {
                editor.putString(key, (String) value);
            } else if (value instanceof Serializable) {
                String encodedString = null;
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(value);
                    oos.close();
                    encodedString = new String(android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.DEFAULT));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (encodedString != null) {
                    editor.putString(key, encodedString);
                }
            }
            else if (value != null)
                editor.putString(key, value.toString());
        }
        editor.commit();
    }

    public static String getStringConfig(Context context , String category , String key) {
        if (key == null)
            return null;
        SharedPreferences sp = context.getSharedPreferences(category , Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

}
