package com.android.ketaoapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.ketaoapp.config.Define;
import com.android.ketaoapp.util.HTTPRequestUtil;
import com.android.ketaoapp.util.LocalSharePreferences;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends Activity {

    private EditText edt_username, edt_pwd;
    private Button btn_login;

    private ProgressDialog dialog;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;

        edt_username = (EditText) findViewById(R.id.et_username);
        edt_pwd = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_login.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       if(dialog.isShowing()){
                           dialog.dismiss();
                           Toast.makeText(context, "登录超时", Toast.LENGTH_LONG).show();
                       }
                    }
                }, 4000);
                loginRequest();
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialog.dismiss();
        }
    };

    private void loginRequest(){
        final String username, password;
        username = edt_username.getEditableText().toString();
        password = edt_pwd.getEditableText().toString();
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            Toast.makeText(context, "请输入用户名和密码", Toast.LENGTH_LONG).show();
            return ;
        }

        dialog = dialog.show(context, null, "正在登录...");
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                handler.sendEmptyMessage(0);
                try {
                    if(jsonObject.getString("success").equals("true")){
                        LocalSharePreferences.commintName(context, "username", username);
                        LocalSharePreferences.commintName(context, "password", password);
                        Define.USER_NAME = username;
                        if(jsonObject.getString("usertype").equals("student"))
                            Define.USER_TYPE = Define.USER_STUDENT;
                        else
                            Define.USER_TYPE = Define.USER_TEACHER;
                        context.startActivity(new Intent(context, MainActivity.class));
                        LoginActivity.this.finish();
                    }
                    else{
                        Toast.makeText(context, "用户名或密码错误", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
