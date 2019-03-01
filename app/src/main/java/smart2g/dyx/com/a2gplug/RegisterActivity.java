package smart2g.dyx.com.a2gplug;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/8/13.
 */

public class RegisterActivity extends Activity {

    private EditText register_phone , register_pass , register_code;
    private CheckBox register_check;
    private Button next_bt , send_code_bt , register_back_bt;

    private AsyncHttpClient asyncHttpClient;

    private String code = "";

    private MyProgressDialog myProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        asyncHttpClient = new AsyncHttpClient();
        init();
        setTitleHeight();
    }

    private void init(){
        register_phone = (EditText) findViewById(R.id.register_phone);
        register_pass = (EditText) findViewById(R.id.register_pass);
        register_check = (CheckBox) findViewById(R.id.register_check);
        next_bt = (Button) findViewById(R.id.next_bt);
        register_code = (EditText) findViewById(R.id.register_code);
        send_code_bt = (Button) findViewById(R.id.send_code_bt);
        register_back_bt = (Button) findViewById(R.id.register_back_bt);
        send_code_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myProgressDialog = MyProgressDialog.createDialog(RegisterActivity.this);
                myProgressDialog.show();
                code = "";
                getRegisterCode();
//                helper.setOnFinishListener(new CountDownButtonHelper.OnFinishListener() {
//
//                    @Override
//                    public void finish() {
//                        Toast.makeText(RegisterActivity.this,"倒计时结束",Toast.LENGTH_SHORT).show();
//                    }
//                });


            }
        });
        register_back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        next_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (register_phone.length() < 11) {
                    Toast.makeText(RegisterActivity.this,"请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                } else if (!code.equals(register_code.getText().toString())&&!code.equals("")){
                    Toast.makeText(RegisterActivity.this,"请输入正确的手机验证码!", Toast.LENGTH_SHORT).show();
                } else if (register_pass.length() < 6 || Constants.isNumber(register_pass.getText().toString())){
                    Toast.makeText(RegisterActivity.this,"您设置的密码为弱密码!需要大于6位且包含字符和数字", Toast.LENGTH_SHORT).show();
                } else {
                    myProgressDialog = MyProgressDialog.createDialog(RegisterActivity.this);
                    myProgressDialog.show();
                    register();
                }

            }
        });
    }


    private void getRegisterCode (){
        if (asyncHttpClient==null){
            asyncHttpClient = new AsyncHttpClient();
        }
        asyncHttpClient.setTimeout(10000);
        asyncHttpClient.get("http://47.99.42.142/message/demo/PHP/sms_send.php?phone="+register_phone.getText().toString() ,new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                System.out.println("验证码发送成功："+s);

                try {
                    JSONObject object = new JSONObject(s);
                    code = object.optString("code");
                    if (code.equals("")){
                        String message = object.optString("message");
                        Toast.makeText(RegisterActivity.this,""+message, Toast.LENGTH_SHORT).show();
                    } else {
                        CountDownButtonHelper helper = new CountDownButtonHelper(
                                send_code_bt, "重新发送", 30, 1);
                        helper.start();
                        Toast.makeText(RegisterActivity.this,"发送成功", Toast.LENGTH_SHORT).show();
                    }
                    myProgressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this,"数据解析错误", Toast.LENGTH_SHORT).show();
                    myProgressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
                System.out.println("接口访问不通或者网络不通");
                myProgressDialog.dismiss();
            }

        });
    }

    private void register(){
        if (asyncHttpClient == null){
            asyncHttpClient = new AsyncHttpClient();
        }
        RequestParams requestParams = new RequestParams();
        requestParams.put("phone",register_phone.getText().toString());
        requestParams.put("password",register_pass.getText().toString());
        System.out.println("注册结果："+requestParams.toString());
        asyncHttpClient.setTimeout(10000);
        asyncHttpClient.post(Constants.MAIN_LINK+"register",requestParams,new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                System.out.println("注册成功："+s);
                try {
                    JSONObject object = new JSONObject(s);
                    String message = object.optString("msg");
                    String code = object.optString("code");
                    if (!message.equals("")){
                        Toast.makeText(RegisterActivity.this,message, Toast.LENGTH_SHORT).show();
                    }
                    if (code.equals("0")){
                        HashMap<String, Object> loginMap = new HashMap<>();
                        loginMap.put(Information.LOGIN_USER_NAME,register_phone.getText().toString());
                        loginMap.put(Information.LOGIN_PASSWORD,register_pass.getText().toString());
                        Information.saveInformation(RegisterActivity.this,Information.LOGIN,loginMap);
                        setResult(1);
                        finish();
                    }

                    myProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this,"数据解析错误", Toast.LENGTH_SHORT).show();
                    myProgressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
                System.out.println("接口访问不通或者网络不通");
                myProgressDialog.dismiss();
            }
        });
    }

    private void setTitleHeight (){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        View view = findViewById(R.id.register_title);
        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) view.getLayoutParams();
        params.height = statusBarHeight1;
        view.setLayoutParams(params);
    }

}
