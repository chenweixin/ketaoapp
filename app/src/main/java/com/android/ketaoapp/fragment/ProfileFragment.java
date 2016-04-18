package com.android.ketaoapp.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ketaoapp.LoginActivity;
import com.android.ketaoapp.R;
import com.android.ketaoapp.config.Define;
import com.android.ketaoapp.entity.Student;
import com.android.ketaoapp.util.HTTPRequestUtil;
import com.android.ketaoapp.util.LocalSharePreferences;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private Context context;
    private Student student;

    private LinearLayout rl_logout, rl_my_collect, rl_my_eva, rl_detail;
    private FrameLayout rl_cover_loading;
    private ImageView ic_avatar;
    private TextView tv_name, tv_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        rl_logout = (LinearLayout) view.findViewById(R.id.profile_my_logout);
        rl_my_collect = (LinearLayout) view.findViewById(R.id.profile_my_collect);
        rl_my_eva = (LinearLayout) view.findViewById(R.id.profile_my_comment);
        rl_detail = (LinearLayout) view.findViewById(R.id.profile_my_detail);
        rl_cover_loading = (FrameLayout) view.findViewById(R.id.rl_cover_loading);
        ic_avatar = (ImageView) view.findViewById(R.id.profile_avatar);
        tv_name = (TextView) view.findViewById(R.id.profile_name);
        tv_id = (TextView) view.findViewById(R.id.profile_id);

        rl_logout.setOnClickListener(this);
        rl_my_collect.setOnClickListener(this);
        rl_my_eva.setOnClickListener(this);
        rl_detail.setOnClickListener(this);

        initView();

        return view;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            rl_cover_loading.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rl_cover_loading.setVisibility(View.GONE);
                        }
                    });
        }
    };

    private void initView(){
        RequestParams params = new RequestParams();
        params.put("student_id", Define.USER_NAME);
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/student/get", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                rl_cover_loading.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                }, 500);
                student = new Student();
                try {
                    student.setAvatar(response.getString("avatar_url"));
                    student.setCollege(response.getString("college"));
                    student.setId(response.getString("id"));
                    student.setMajor(response.getString("major"));
                    student.setName(response.getString("name"));
                    student.setPeriod(response.getString("period"));
                    student.setSex(response.getBoolean("sex"));
                    tv_name.setText(student.getName());
                    tv_id.setText("学号：" + student.getId());
                    ImageLoader.getInstance().displayImage(Define.IMAGE_HOST+student.getAvatar(), ic_avatar);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

        @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profile_my_logout:
                LocalSharePreferences.clear(context);
                context.startActivity(new Intent(context, LoginActivity.class));
                ((Activity)context).finish();
                break;
            case R.id.profile_my_collect:
                break;
            case R.id.profile_my_comment:
                break;
            case R.id.profile_my_detail:
                break;
        }
    }
}
