package smart2g.dyx.com.a2gplug;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class Text {

    private AsyncHttpClient asyncHttpClient ;

    private void getCode(String phone , final Context context){
        if (asyncHttpClient == null){
            asyncHttpClient = new AsyncHttpClient();
        }

        asyncHttpClient.setTimeout(1000);
        asyncHttpClient.get("http://47.99.42.142/message/demo/PHP/sms_send.php?phone="+phone,new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                System.out.println("getCode:"+s);
                try {
                    JSONObject object = new JSONObject(s);
                    String code = object.optString("code");
                    if (!code.equals("")){
                        Toast.makeText(context,"验证码发送成功！",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
                System.out.println("getCode网络不通或接口异常:"+throwable.getMessage());
            }
        });

    }


    private void register(String phone, String passWord , final Context context) {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient();
        }
        asyncHttpClient.setTimeout(1000);
        RequestParams requestParams = new RequestParams();
        requestParams.put("phone", phone);
        requestParams.put("password", passWord);
        System.out.println("register:"+requestParams.toString());
        asyncHttpClient.post(Constants.MAIN_LINK + "/register", requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                System.out.println("register:" + s);
                try {
                    JSONObject object = new JSONObject(s);
                    String message = object.optString("message");
                    String code = object.optString("code");
                    if (code.equals("0")) {

                    }
                    Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
                System.out.println("register接口不通或网络异常：" + throwable.getMessage());
            }
        });
    }


    private void login(String phone, String passWord , final Context context) {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient();
        }
        asyncHttpClient.setTimeout(1000);
        RequestParams requestParams = new RequestParams();
        requestParams.put("phone", phone);
        requestParams.put("password", passWord);
        System.out.println("register:"+requestParams.toString());
        asyncHttpClient.post(Constants.MAIN_LINK + "/register", requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                System.out.println("register:" + s);
                try {
                    JSONObject object = new JSONObject(s);
                    String message = object.optString("message");
                    String code = object.optString("code");
                    String access_token = object.optString("access_token");
                    if (code.equals("0")) {

                    }
                    Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
                System.out.println("register接口不通或网络异常：" + throwable.getMessage());
            }
        });
    }

    private void bindDevice(String phone , String deviceId , String token , final Context context){
        if (asyncHttpClient == null){
            asyncHttpClient = new AsyncHttpClient();
        }

        asyncHttpClient.setTimeout(1000);
        asyncHttpClient.get(Constants.MAIN_LINK+"/device/bind?phone="+phone+"&deviceId="+deviceId+"&access_token="+token,new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject object = new JSONObject(s);
                    String code = object.optString("code");
                    String message = object.optString("message");
                    if (code.equals("0")){

                    } else if (code.equals("1")){

                    }
                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
            }
        });

    }

    private void checkDevice(String phone , String deviceId , String token , final Context context){
        if (asyncHttpClient == null){
            asyncHttpClient = new AsyncHttpClient();
        }

        asyncHttpClient.setTimeout(1000);
        asyncHttpClient.get(Constants.MAIN_LINK+"/device/check?phone="+phone+"&deviceId="+deviceId+"&access_token="+token,new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject object = new JSONObject(s);
                    String code = object.optString("code");
                    String message = object.optString("message");
                    if (code.equals("0")){

                    } else if (code.equals("1")){

                    }
                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
            }
        });
    }

}
