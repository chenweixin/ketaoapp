package com.android.ketaoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.ketaoapp.config.Define;
import com.android.ketaoapp.util.HTTPRequestUtil;
import com.android.ketaoapp.util.LocalSharePreferences;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class InitActivity extends AppCompatActivity {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    startActivity(new Intent(InitActivity.this, LoginActivity.class));
                    finish();
                    break;
                case 2:
                    loginRequest();
                    break;
            }
        }
    };

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        context = InitActivity.this;

        if(LocalSharePreferences.getName(context, "username").equals("")){
            handler.sendEmptyMessageDelayed(1, 500);
        }
        else{
            handler.sendEmptyMessageDelayed(2, 500);
        }
    }

    private void loginRequest(){
        final String username, password;
        username = LocalSharePreferences.getName(context, "username");
        password = LocalSharePreferences.getName(context, "password");

        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    if (jsonObject.getString("success").equals("true")) {
                        Define.USER_NAME = username;
                        if (jsonObject.getString("usertype").equals("student"))
                            Define.USER_TYPE = Define.USER_STUDENT;
                        else
                            Define.USER_TYPE = Define.USER_TEACHER;
                        context.startActivity(new Intent(context, MainActivity.class));
                        InitActivity.this.finish();
                    } else {
                        Toast.makeText(context, "账号密码过期，请重新登录", Toast.LENGTH_LONG).show();
                        context.startActivity(new Intent(context, LoginActivity.class));
                        InitActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
