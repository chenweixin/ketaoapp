package com.android.ketaoapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ketaoapp.R;
import com.android.ketaoapp.ReccourseActivity;
import com.android.ketaoapp.config.Define;
import com.android.ketaoapp.entity.Spread;
import com.android.ketaoapp.util.HTTPRequestUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private Activity context;

    private View view;
    private RelativeLayout rl_1;
    private LinearLayout rl_2, rl_3, rl_course_recommended, rl_comment_hot, rl_comment_iwant, rl_teacher_like, rl_type_all;
    private ImageView iv_1, iv_2, iv_3;
    private TextView title_1, title_2, title_3, time_2, time_3;

    private List<Spread> spreads = new ArrayList<Spread>();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();
        view =  inflater.inflate(R.layout.fragment_home, container, false);

        //初始化控件
        initView();

        //展示推广内容
        initSpread();

        return view;
    }

    private void initView(){
        rl_1 = (RelativeLayout) view.findViewById(R.id.spread_first);
        rl_2 = (LinearLayout) view.findViewById(R.id.spread_second);
        rl_3 = (LinearLayout) view.findViewById(R.id.spread_third);
        rl_course_recommended = (LinearLayout) view.findViewById(R.id.rl_scroll_course_recommended);
        rl_comment_hot = (LinearLayout) view.findViewById(R.id.rl_scroll_comment_hot);
        rl_comment_iwant = (LinearLayout) view.findViewById(R.id.rl_scroll_comment_iwant);
        rl_teacher_like = (LinearLayout) view.findViewById(R.id.rl_scroll_teacher_like);
        rl_type_all = (LinearLayout) view.findViewById(R.id.rl_scroll_type_all);

        iv_1 = (ImageView) view.findViewById(R.id.spread_first_img);
        iv_2 = (ImageView) view.findViewById(R.id.spread_second_img);
        iv_3 = (ImageView) view.findViewById(R.id.spread_third_img);

        title_1 = (TextView) view.findViewById(R.id.spread_first_title);
        title_2 = (TextView) view.findViewById(R.id.spread_second_title);
        title_3 = (TextView) view.findViewById(R.id.spread_third_title);

        time_2 = (TextView) view.findViewById(R.id.spread_second_create_time);
        time_3 = (TextView) view.findViewById(R.id.spread_third_create_time);

        rl_1.setVisibility(View.GONE);
        rl_2.setVisibility(View.GONE);
        rl_3.setVisibility(View.GONE);

        rl_course_recommended.setOnClickListener(this);
        rl_comment_hot.setOnClickListener(this);
        rl_comment_iwant.setOnClickListener(this);
        rl_teacher_like.setOnClickListener(this);
        rl_type_all.setOnClickListener(this);
    }

    private void initSpread(){
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/spread/getall", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Spread spread = new Spread();
                        spread.setId(jsonObject.getString("id"));
                        spread.setCreate_time(jsonObject.getString("create_time"));
                        spread.setEvent_address(jsonObject.getString("event_address"));
                        spread.setEvent_date(jsonObject.getString("event_date"));
                        spread.setEvent_describe(jsonObject.getString("event_describe"));
                        spread.setPoster_url(jsonObject.getString("poster_url"));
                        spread.setSponsor(jsonObject.getString("sponsor"));
                        spread.setTitle(jsonObject.getString("title"));
                        spreads.add(spread);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (response.length() >= 1) {
                    rl_1.setVisibility(View.VISIBLE);
                    title_1.setText(spreads.get(0).getTitle());
                    imageLoader.displayImage(spreads.get(0).getPoster_url(), iv_1);
                }
                if (response.length() >= 2) {
                    rl_2.setVisibility(View.VISIBLE);
                    title_2.setText(spreads.get(1).getTitle());
                    time_2.setText(spreads.get(1).getCreate_time());
                    imageLoader.displayImage(spreads.get(1).getPoster_url(), iv_2);
                }
                if (response.length() >= 3) {
                    rl_3.setVisibility(View.VISIBLE);
                    title_3.setText(spreads.get(2).getTitle());
                    time_3.setText(spreads.get(2).getCreate_time());
                    imageLoader.displayImage(spreads.get(2).getPoster_url(), iv_3);
                }
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        rl_course_recommended = (LinearLayout) view.findViewById(R.id.rl_scroll_course_recommended);
        rl_comment_hot = (LinearLayout) view.findViewById(R.id.rl_scroll_comment_hot);
        rl_comment_iwant = (LinearLayout) view.findViewById(R.id.rl_scroll_comment_iwant);
        rl_teacher_like = (LinearLayout) view.findViewById(R.id.rl_scroll_teacher_like);
        rl_type_all = (LinearLayout) view.findViewById(R.id.rl_scroll_type_all);
        switch (v.getId()){
            case R.id.rl_scroll_course_recommended:
                context.startActivity(new Intent(context, ReccourseActivity.class));
                break;
            case R.id.rl_scroll_comment_hot:
                break;
            case R.id.rl_scroll_comment_iwant:
                break;
            case R.id.rl_scroll_teacher_like:
                break;
            case R.id.rl_scroll_type_all:
                break;
        }
    }
}
