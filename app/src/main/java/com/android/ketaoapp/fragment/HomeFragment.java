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
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ketaoapp.AllTypeActivity;
import com.android.ketaoapp.CommentListActivity;
import com.android.ketaoapp.CourseListActivity;
import com.android.ketaoapp.R;
import com.android.ketaoapp.SearchActivity;
import com.android.ketaoapp.SpreadListActivity;
import com.android.ketaoapp.TeaListActivity;
import com.android.ketaoapp.config.Define;
import com.android.ketaoapp.entity.Spread;
import com.android.ketaoapp.util.CommonUtil;
import com.android.ketaoapp.util.HTTPRequestUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements PullToRefreshBase.OnRefreshListener{

    private Activity context;
    private HomeAdapter adapter;

    private View view;
    private PullToRefreshListView listView;
    private RelativeLayout rl_1, rl_click_more;
    private LinearLayout rl_2, rl_3, rl_course_recommended, rl_comment_hot, rl_comment_iwant, rl_teacher_like, rl_type_all;
    private FrameLayout rl_cover_loading;
    private ImageView iv_1, iv_2, iv_3;
    private TextView title_1, title_2, title_3, time_2, time_3;

    private List<Spread> spreads = new ArrayList<Spread>();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_home);
        rl_cover_loading = (FrameLayout) view.findViewById(R.id.rl_cover_loading);

        adapter = new HomeAdapter(context, spreads);
        listView.setAdapter(adapter);
        listView.setShowIndicator(false);
        listView.setOnRefreshListener(this);

        getSpreads();

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

    private void getSpreads(){
        RequestParams params = new RequestParams();
        params.add("pageSize", "3");
        params.add("pageIndex", "0");
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/spread/getall", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                rl_cover_loading.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                }, 500);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Spread spread = new Spread();
                        spread.setId(jsonObject.getString("id"));
                        if(jsonObject.has("create_time"))
                            spread.setCreate_time(jsonObject.getString("create_time"));
                        if(jsonObject.has("event_address"))
                            spread.setEvent_address(jsonObject.getString("event_address"));
                        if(jsonObject.has("event_date"))
                            spread.setEvent_date(jsonObject.getString("event_date"));
                        if(jsonObject.has("event_describe"))
                            spread.setEvent_describe(jsonObject.getString("event_describe"));
                        if(jsonObject.has("poster_url"))
                            spread.setPoster_url(jsonObject.getString("poster_url"));
                        if(jsonObject.has("sponsor"))
                            spread.setSponsor(jsonObject.getString("sponsor"));
                        if(jsonObject.has("title"))
                            spread.setTitle(jsonObject.getString("title"));
                        spreads.add(spread);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                listView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        listView.onRefreshComplete();
                    }
                }, 500);
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
    public void onRefresh(PullToRefreshBase refreshView) {
        spreads.clear();
        listView.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (listView.isRefreshing()) {
                    listView.onRefreshComplete();
                    Toast.makeText(context, "请检查网络条件",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }, 4000);//4秒超时
        getSpreads();
    }

    class HomeAdapter extends BaseAdapter implements View.OnClickListener{

        private Context context;
        private List<Spread> spreads;

        public HomeAdapter(Context context, List<Spread> spreads) {
            this.context = context;
            this.spreads = spreads;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = View.inflate(context, R.layout.item_home, null);
            rl_1 = (RelativeLayout) view.findViewById(R.id.spread_first);
            rl_2 = (LinearLayout) view.findViewById(R.id.spread_second);
            rl_3 = (LinearLayout) view.findViewById(R.id.spread_third);
            rl_course_recommended = (LinearLayout) view.findViewById(R.id.rl_scroll_course_recommended);
            rl_comment_hot = (LinearLayout) view.findViewById(R.id.rl_scroll_comment_hot);
            rl_comment_iwant = (LinearLayout) view.findViewById(R.id.rl_scroll_comment_iwant);
            rl_teacher_like = (LinearLayout) view.findViewById(R.id.rl_scroll_teacher_like);
            rl_type_all = (LinearLayout) view.findViewById(R.id.rl_scroll_type_all);
            rl_click_more = (RelativeLayout) view.findViewById(R.id.spread__click_more);

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
            rl_click_more.setOnClickListener(this);
            rl_1.setOnClickListener(this);
            rl_2.setOnClickListener(this);
            rl_3.setOnClickListener(this);

            if (spreads.size() >= 1) {
                rl_1.setVisibility(View.VISIBLE);
                title_1.setText(spreads.get(0).getTitle());
                imageLoader.displayImage(Define.IMAGE_HOST+spreads.get(0).getPoster_url(), iv_1, Define.getImageOptions());
            }
            if (spreads.size() >= 2) {
                rl_2.setVisibility(View.VISIBLE);
                title_2.setText(spreads.get(1).getTitle());
                String time = CommonUtil.getFormatedDateTime(Long.parseLong(spreads.get(1).getCreate_time())/1000);
                time_2.setText(time);
                imageLoader.displayImage(Define.IMAGE_HOST+spreads.get(1).getPoster_url(), iv_2, Define.getImageOptions());
            }
            if (spreads.size() >= 3) {
                rl_3.setVisibility(View.VISIBLE);
                title_3.setText(spreads.get(2).getTitle());
                String time2 = CommonUtil.getFormatedDateTime(Long.parseLong(spreads.get(2).getCreate_time())/1000);
                time_3.setText(time2);
                imageLoader.displayImage(Define.IMAGE_HOST+spreads.get(2).getPoster_url(), iv_3, Define.getImageOptions());
            }

            return view;
        }

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()){
                /**
                 * 首部导航按钮
                 */
                case R.id.rl_scroll_course_recommended:
                    intent = new Intent(context, CourseListActivity.class);
                    intent.putExtra(CourseListActivity.LIST_TYPE, CourseListActivity.REC_COURSE);
                    context.startActivity(intent);
                    break;
                case R.id.rl_scroll_comment_hot:
                    intent = new Intent(context, CommentListActivity.class);
                    intent.putExtra(CourseListActivity.LIST_TYPE, CommentListActivity.HOT_COMMENT);
                    context.startActivity(intent);
                    break;
                case R.id.rl_scroll_comment_iwant:
                    context.startActivity(new Intent(context, SearchActivity.class));
                    break;
                case R.id.rl_scroll_teacher_like:
                    context.startActivity(new Intent(context, TeaListActivity.class));
                    break;
                case R.id.rl_scroll_type_all:
                    context.startActivity(new Intent(context, AllTypeActivity.class));
                    break;
                /**
                 * 推广
                 */
                case R.id.spread__click_more:
                    context.startActivity(new Intent(context, SpreadListActivity.class));
                    break;
                case R.id.spread_first:
                    break;
                case R.id.spread_second:
                    break;
                case R.id.spread_third:
                    break;

            }
        }
    }
}
