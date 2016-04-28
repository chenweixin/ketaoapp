package com.android.ketaoapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.ketaoapp.config.Define;
import com.android.ketaoapp.entity.Teacher;
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

import cz.msebera.android.httpclient.Header;

public class TeaListActivity extends Activity implements PullToRefreshBase.OnRefreshListener2{

    private Context context;
    private int pageSize = 10, pageIndex = 0;
    private List<Teacher> teachers = new ArrayList<Teacher>();

    private TeacherAdapter adapter;
    private PullToRefreshListView listView;
    private FrameLayout rl_cover_loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wel_tea);

        context = TeaListActivity.this;

        rl_cover_loading = (FrameLayout) findViewById(R.id.rl_cover_loading);
        listView = (PullToRefreshListView) findViewById(R.id.lv_wel_teacher);
        adapter = new TeacherAdapter(context);
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setShowIndicator(false);
        listView.setOnRefreshListener(this);
        getTeachers(true);
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

    private void getTeachers(final boolean isDown){
        RequestParams params = new RequestParams();
        params.add("pageSize", String.valueOf(pageSize));
        params.add("pageIndex", String.valueOf(pageIndex));
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/teacher/getwelcome", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                super.onSuccess(statusCode, headers, jsonArray);
                if(isDown){
                    teachers.clear();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Teacher teacher = new Teacher();
                        teacher.setId(object.getString("id"));
                        if(object.has("college"))
                            teacher.setCollege(object.getString("college"));
                        if(object.has("avatar_url"))
                            teacher.setAvatar(object.getString("avatar_url"));
                        if(object.has("name"))
                            teacher.setName(object.getString("name"));
                        if(object.has("sex"))
                            teacher.setSex(object.getBoolean("sex"));
                        teachers.add(teacher);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(jsonArray.length() != 0){
                    pageIndex++;
                }
                adapter.notifyDataSetChanged();
                if(isDown) {
                    rl_cover_loading.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(0);
                        }
                    }, 500);
                    listView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            listView.onRefreshComplete();
                        }
                    }, 500);
                    if (teachers.size() < pageSize * pageIndex) {
                        listView.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
                    } else {
                        listView.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                }
                else{
                    listView.onRefreshComplete();
                }
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        listView.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (listView.isRefreshing()) {
                    listView.onRefreshComplete();
                }
            }
        }, 4000);//4秒超时
        pageIndex = 0;
        getTeachers(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        listView.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (listView.isRefreshing()) {
                    listView.onRefreshComplete();
                }
            }
        }, 4000);//4秒超时
        listView.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                getTeachers(false);
            }
        }, 500);
    }

    class TeacherAdapter extends BaseAdapter{

        private Context context;

        public TeacherAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return teachers.size();
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
            final ViewHolder holder;
            if(view == null || view.getTag() == null){
                view = View.inflate(context, R.layout.item_teacher, null);
                holder = new ViewHolder();
                holder.id = (TextView) view.findViewById(R.id.item_teacher_id);
                holder.name = (TextView) view.findViewById(R.id.item_teacher_name);
                holder.avatar = (ImageView) view.findViewById(R.id.item_teacher_avatar);
                holder.college = (TextView) view.findViewById(R.id.item_teacher_college);

                view.setTag(holder);
            }
            else{
                holder = (ViewHolder) view.getTag();
            }
            ImageLoader.getInstance().displayImage(Define.IMAGE_HOST+teachers.get(position).getAvatar(), holder.avatar);
            holder.id.setText("(id: "+teachers.get(position).getId()+")");
            holder.name.setText(teachers.get(position).getName());
            holder.college.setText(teachers.get(position).getCollege());

            return view;
        }
    }

    class ViewHolder{
        ImageView avatar;
        TextView name, id, college;
    }
}
