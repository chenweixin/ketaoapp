package com.android.ketaoapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ketaoapp.config.Define;
import com.android.ketaoapp.entity.Course;
import com.android.ketaoapp.util.HTTPRequestUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class ReccourseActivity extends Activity implements PullToRefreshBase.OnRefreshListener2{

    private Context context;
    private int pageSize = 10, pageIndex = 0;

    private List<Course> courses = new ArrayList<Course>();
    private CourseAdapter adapter;

    private PullToRefreshListView listView;
    private FrameLayout rl_cover_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reccourse);

        listView = (PullToRefreshListView) findViewById(R.id.lv_reccourse);
        rl_cover_loading = (FrameLayout) findViewById(R.id.rl_cover_loading);

        context = this;
        adapter = new CourseAdapter(context, courses);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setShowIndicator(false);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);

        getCourses();
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

    private void getCourses(){
        RequestParams params = new RequestParams();
        params.add("pageSize", String.valueOf(pageSize));
        params.add("pageIndex", String.valueOf(pageIndex));
        params.add("student_id", Define.USER_NAME);
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/course/getranking", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                courses.clear();
                rl_cover_loading.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                }, 500);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Course course = new Course();
                        course.setId(jsonObject.getString("id"));
                        if(jsonObject.has("name"))
                            course.setName(jsonObject.getString("name"));
                        if(jsonObject.has("teacher_id"))
                            course.setTeacher_id(jsonObject.getString("teacher_id"));
                        if(jsonObject.has("teacher_name"))
                            course.setTeacher_name(jsonObject.getString("teacher_name"));
                        if(jsonObject.has("location"))
                            course.setLocation(jsonObject.getString("location"));
                        if(jsonObject.has("credit"))
                            course.setCredit(Integer.parseInt(jsonObject.getString("credit")));
                        if(jsonObject.has("type"))
                            course.setType(Integer.parseInt(jsonObject.getString("type")));
                        if(jsonObject.has("create_time"))
                            course.setCreate_time(jsonObject.getString("create_time"));
                        if(jsonObject.has("num_collect"))
                            course.setNum_collect(Integer.parseInt(jsonObject.getString("num_collect")));
                        if(jsonObject.has("num_evaluate"))
                            course.setNum_evaluate(Integer.parseInt(jsonObject.getString("num_evaluate")));
                        if(jsonObject.has("score"))
                            course.setScore(Integer.parseInt(jsonObject.getString("score")));
                        if(jsonObject.has("avg_score"))
                            course.setAvg_score(Double.parseDouble(jsonObject.getString("avg_score")));
                        if(jsonObject.has("introduction"))
                            course.setIntroduction(jsonObject.getString("introduction"));
                        if(jsonObject.has("iscollect"))
                        {
                            if(jsonObject.getString("iscollect").equals("true")){
                                course.setIscollect(true);
                            }
                            else{
                                course.setIscollect(false);
                            }
                        }
                        if(jsonObject.has("isevaluate")){
                            if(jsonObject.getString("isevaluate").equals("true")){
                                course.setIsevaluate(true);
                            }
                            else{
                                course.setIsevaluate(false);
                            }
                        }
                        courses.add(course);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                pageIndex++;
                listView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        listView.onRefreshComplete();
                    }
                }, 500);
                if (courses.size() < pageSize * pageIndex) {
                    listView.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
                } else {
                    listView.setMode(PullToRefreshBase.Mode.BOTH);
                }
                super.onSuccess(statusCode, headers, response);
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

    private void addCourses(){
        RequestParams params = new RequestParams();
        params.add("pageSize", String.valueOf(pageSize));
        params.add("pageIndex", String.valueOf(pageIndex));
        params.add("student_id", Define.USER_NAME);
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/course/getranking", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                if(response.length() != 0){
                    pageIndex++;
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Course course = new Course();
                        course.setId(jsonObject.getString("id"));
                        if(jsonObject.has("name"))
                            course.setName(jsonObject.getString("name"));
                        if(jsonObject.has("teacher_id"))
                            course.setTeacher_id(jsonObject.getString("teacher_id"));
                        if(jsonObject.has("teacher_name"))
                            course.setTeacher_name(jsonObject.getString("teacher_name"));
                        if(jsonObject.has("location"))
                            course.setLocation(jsonObject.getString("location"));
                        if(jsonObject.has("credit"))
                            course.setCredit(Integer.parseInt(jsonObject.getString("credit")));
                        if(jsonObject.has("type"))
                            course.setType(Integer.parseInt(jsonObject.getString("type")));
                        if(jsonObject.has("create_time"))
                            course.setCreate_time(jsonObject.getString("create_time"));
                        if(jsonObject.has("num_collect"))
                            course.setNum_collect(Integer.parseInt(jsonObject.getString("num_collect")));
                        if(jsonObject.has("num_evaluate"))
                            course.setNum_evaluate(Integer.parseInt(jsonObject.getString("num_evaluate")));
                        if(jsonObject.has("score"))
                            course.setScore(Integer.parseInt(jsonObject.getString("score")));
                        if(jsonObject.has("avg_score"))
                            course.setAvg_score(Double.parseDouble(jsonObject.getString("avg_score")));
                        if(jsonObject.has("iscollect"))
                        {
                            if(jsonObject.getString("iscollect").equals("true")){
                                course.setIscollect(true);
                            }
                            else{
                                course.setIscollect(false);
                            }
                        }
                        courses.add(course);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                listView.onRefreshComplete();
                super.onSuccess(statusCode, headers, response);
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
        getCourses();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//        if(spreads.size() < pageSize * pageIndex){
//            listView.onRefreshComplete();
//            return;
//        }
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
                addCourses();
            }
        }, 500);

    }

    class CourseAdapter extends BaseAdapter{

        private List<Course> courses;
        private Context context;

        public CourseAdapter(Context context, List<Course> courses) {
            this.courses = courses;
            this.context = context;
        }

        @Override
        public int getCount() {
            return courses.size();
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
        public View getView(final int position, View v, final ViewGroup parent) {
            final ViewHolder holder;
            if(v != null){
                holder = (ViewHolder) v.getTag();
            }
            else{
                v = View.inflate(context, R.layout.item_reccourse, null);
                holder = new ViewHolder();
                holder.name = (TextView) v.findViewById(R.id.item_reccourse_name);
                holder.teacher_name = (TextView) v.findViewById(R.id.item_reccourse_teacher_name);
                holder.type = (TextView) v.findViewById(R.id.item_reccourse_course_type);
                holder.score = (TextView) v.findViewById(R.id.item_reccourse_avg_score);
                holder.num_eva = (TextView) v.findViewById(R.id.item_reccourse_num_eva);
                holder.num_collect = (TextView) v.findViewById(R.id.item_reccourse_num_collect);
                holder.rl_detail = (RelativeLayout) v.findViewById(R.id.item_reccourse_rl_detail);
                holder.rl_eva = (LinearLayout) v.findViewById(R.id.item_reccourse_rl_eva);
                holder.rl_collect = (LinearLayout) v.findViewById(R.id.item_reccourse_rl_collect);
                holder.num_collect_ic = (ImageView) v.findViewById(R.id.item_reccourse_num_collect_ic);
                v.setTag(holder);
            }
            holder.name.setText(courses.get(position).getName());
            holder.teacher_name.setText(courses.get(position).getTeacher_name());
            holder.type.setText(Define.COURSE_TYPES[courses.get(position).getType()]);
            holder.num_eva.setText(String.valueOf(courses.get(position).getNum_evaluate()));
            holder.num_collect.setText(String.valueOf(courses.get(position).getNum_collect()));
            if(courses.get(position).iscollect())
                holder.num_collect_ic.setImageResource(R.mipmap.ic_treehole_hehe_item_press);
            else
                holder.num_collect_ic.setImageResource(R.mipmap.ic_treehole_hehe);

            if(courses.get(position).getAvg_score() == 0){
                holder.score.setText("暂无评分");
            }
            else{
                DecimalFormat df = new DecimalFormat("#.00");
                holder.score.setText(df.format(courses.get(position).getAvg_score()));
            }

            holder.rl_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CourseActivity.class);
                    intent.putExtra("course", courses.get(position));
                    intent.putExtra("iscomment", false);
                    context.startActivity(intent);
                }
            });
            holder.rl_collect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestParams params = new RequestParams();
                    params.put("course_id", courses.get(position).getId());
                    params.put("student_id", Define.USER_NAME);
                    if(courses.get(position).iscollect()){
                        holder.num_collect_ic.setImageResource(R.mipmap.ic_treehole_hehe);
                        courses.get(position).setNum_collect(courses.get(position).getNum_collect() - 1);
                        holder.num_collect.setText(courses.get(position).getNum_collect() + "");
                        cancleCollect(params, position);
                    } else {
                        holder.num_collect_ic.setImageResource(R.mipmap.ic_treehole_hehe_item_press);
                        courses.get(position).setNum_collect(courses.get(position).getNum_collect() + 1);
                        holder.num_collect.setText(courses.get(position).getNum_collect() + "");
                        collect(params, position);
                    }
                }
            });
            holder.rl_eva.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CourseActivity.class);
                    intent.putExtra("course", courses.get(position));
                    intent.putExtra("iscomment", true);
                    context.startActivity(intent);
                }
            });

            return v;
        }
    }

    class ViewHolder{
        TextView name, teacher_name, type, score, num_eva, num_collect;
        RelativeLayout rl_detail;
        LinearLayout rl_eva, rl_collect;
        ImageView num_collect_ic;
    }

    private void collect(final RequestParams params, final int position){
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/coscollect/add", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        courses.get(position).setIscollect(true);
                    }
                });
    }

    private void cancleCollect(final RequestParams params, final int position){
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/coscollect/cancle", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                courses.get(position).setIscollect(false);
            }
        });
    }
}
