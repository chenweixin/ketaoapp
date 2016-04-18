package com.android.ketaoapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.ketaoapp.config.Define;
import com.android.ketaoapp.util.HTTPRequestUtil;
import com.android.ketaoapp.view.DetailTitleView;
import com.android.ketaoapp.view.EvaluationView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CommentActivity extends Activity{

    private DetailTitleView titleView;
    private EvaluationView evaluationView;
    private EditText edt_comment;

    private Context context;

    private int current_position = -1;
    private String course_id;
    private boolean isevaluate;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        context = CommentActivity.this;
        course_id = getIntent().getStringExtra("course_id");
        isevaluate = getIntent().getBooleanExtra("isevaluate", false);

        titleView = (DetailTitleView) findViewById(R.id.comment_title_view);
        evaluationView = (EvaluationView) findViewById(R.id.comment_evaluation);
        edt_comment = (EditText) findViewById(R.id.et_comment_content);

        titleView.setRightBtnListener(new DetailTitleView.OnTitleRightButtonClickListener() {
            @Override
            public void onRightButtonClick(View v) {
                if(current_position == -1)
                {
                    Toast.makeText(context, "请评分..", Toast.LENGTH_SHORT).show();
                    return;
                }
                evaluation(current_position, edt_comment.getText().toString());
            }
        });
        evaluationView.setOnEvaluationClickListener(new EvaluationView.OnEvaluationClickListener() {
            @Override
            public void onClick(int position) {
                current_position = position;
            }
        });

        init();

    }

    private void init(){
        if(isevaluate){
            RequestParams params = new RequestParams();
            params.put("courseid", course_id);
            params.put("studentid", Define.USER_NAME);
            HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/evaluation/geteva", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    super.onSuccess(statusCode, headers, jsonObject);
                    try {
                        if(jsonObject.has("score"))
                            evaluationView.setLike(Integer.parseInt(jsonObject.getString("score")));
                        if(jsonObject.has("comment"))
                            edt_comment.setText(jsonObject.getString("comment"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialog.dismiss();
        }
    };

    private void evaluation(int score, String comment){
        dialog = dialog.show(context, null, "正在保存");
        RequestParams params = new RequestParams();
        params.put("course_id", course_id);
        params.put("student_id", Define.USER_NAME);
        params.put("comment", comment);
        params.put("score", String.valueOf(score));
        if(isevaluate){
            HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/evaluation/update", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    ((Activity) context).finish();
                }
            });
        }
        else{
            HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/evaluation/add", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    ((Activity) context).finish();
                }
            });
        }

    }
}