package smart2g.dyx.com.a2gplug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends Activity {

    private EditText userName_Edit , passWord_Edit;

    private Button login_bt;

    private TextView register_text;

    private CheckBox re_pass;

    private MyProgressDialog myProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.denglu_activity);
        userName_Edit = findViewById(R.id.zhanghao);
        passWord_Edit = findViewById(R.id.mima);
        login_bt = findViewById(R.id.denglu);
        register_text = findViewById(R.id.zhuce);
        re_pass = findViewById(R.id.re_pass);

        String phone = Information.getStringConfig(LoginActivity.this,Information.LOGIN,Information.LOGIN_USER_NAME);
        String pass = Information.getStringConfig(LoginActivity.this,Information.LOGIN,Information.LOGIN_PASSWORD);
        String token = Information.getStringConfig(LoginActivity.this,Information.LOGIN,Information.LOGIN_TOKEN);
        if (!token.equals("") && !pass.equals("")){
            Intent intent = new Intent(LoginActivity.this,DeviceListActivity.class);
            startActivity(intent);
            finish();
        }

        userName_Edit.setText(phone);
        passWord_Edit.setText(pass);

        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userName_Edit.getText().toString().replace(" ", "").equals("")
                        && !passWord_Edit.getText().toString().replace(" ","").equals("")){
                    myProgressDialog = MyProgressDialog.createDialog(LoginActivity.this);
                    myProgressDialog.setMessage("登录中...");
                    myProgressDialog.show();
                    login();
                }
            }
        });

        register_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1){
            String phone = Information.getStringConfig(LoginActivity.this,Information.LOGIN,Information.LOGIN_USER_NAME);
            String pass = Information.getStringConfig(LoginActivity.this,Information.LOGIN,Information.LOGIN_PASSWORD);
            userName_Edit.setText(phone);
            passWord_Edit.setText(pass);
        }
    }

    private void login(){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("phone",userName_Edit.getText().toString());
        requestParams.put("password",passWord_Edit.getText().toString());

        asyncHttpClient.post(Constants.MAIN_LINK+"login",requestParams,new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                System.out.println("login:"+s);
                try {
                    JSONObject object = new JSONObject(s);
                    int code = object.optInt("code",1);
                    if (code == 0){
                        String access_token = object.optString("access_token");
                        HashMap<String, Object> loginMap = new HashMap<>();

                        if (re_pass.isChecked()){
                            loginMap.put(Information.LOGIN_TOKEN,access_token);
                            loginMap.put(Information.LOGIN_PASSWORD,passWord_Edit.getText().toString());
                        }
                        loginMap.put(Information.LOGIN_USER_NAME,userName_Edit.getText().toString());

                        Information.saveInformation(LoginActivity.this,Information.LOGIN,loginMap);

                        Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();

                        myProgressDialog.dismiss();

                        finish();

                        Intent intent = new Intent(LoginActivity.this,DeviceListActivity.class);
                        startActivity(intent);

                    } else {
                        String msg = object.optString("msg");
                        myProgressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    myProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
                Toast.makeText(LoginActivity.this,"网络超时!请检查网络",Toast.LENGTH_SHORT).show();
                myProgressDialog.dismiss();
            }
        });
    }
}
