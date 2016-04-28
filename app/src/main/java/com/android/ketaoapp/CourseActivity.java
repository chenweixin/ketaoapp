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
import android.widget.TextView;

import com.android.ketaoapp.config.Define;
import com.android.ketaoapp.entity.Comment;
import com.android.ketaoapp.entity.Course;
import com.android.ketaoapp.util.CommonUtil;
import com.android.ketaoapp.util.HTTPRequestUtil;
import com.android.ketaoapp.view.EvaluationView;
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

public class CourseActivity extends Activity implements PullToRefreshBase.OnRefreshListener2{

    private Context context;
    private int pageSize = 10, pageIndex = 0;
    private Course course;
    boolean iscomment = false;
    private List<Comment> comments = new ArrayList<Comment>();

    private PullToRefreshListView listView;
    private CourseAdapter adapter;
    private LinearLayout rl_comment, rl_collect;
    private FrameLayout rl_cover_loading;
    private TextView tv_comment, tv_collect;
    private ImageView ic_collect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        context = CourseActivity.this;
        course = (Course) getIntent().getSerializableExtra("course");
        iscomment = getIntent().getBooleanExtra("iscomment", false);

        listView = (PullToRefreshListView) findViewById(R.id.lv_course);
        rl_comment = (LinearLayout) findViewById(R.id.course_rl_eva);
        rl_collect = (LinearLayout) findViewById(R.id.course_rl_collect);
        tv_comment = (TextView) findViewById(R.id.course_num_eva);
        tv_collect = (TextView) findViewById(R.id.course_num_collect);
        ic_collect = (ImageView) findViewById(R.id.course_ic_collect);
        rl_cover_loading = (FrameLayout) findViewById(R.id.rl_cover_loading);

        adapter = new CourseAdapter(context, course);
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setShowIndicator(false);
        listView.setOnRefreshListener(this);
        if(iscomment){
            listView.getRefreshableView().setSelection(2);
        }

        if(course.isevaluate()){
            tv_comment.setText("修改评论");
        }
        if(course.iscollect()){
            tv_collect.setText("已关注");
            ic_collect.setImageResource(R.mipmap.ic_treehole_hehe_item_press);
        }
        rl_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("course_id", course.getId());
                intent.putExtra("isevaluate", course.isevaluate());
                context.startActivity(intent);
            }
        });
        rl_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.put("course_id", course.getId());
                params.put("student_id", Define.USER_NAME);
                if(course.iscollect()){
                    ic_collect.setImageResource(R.mipmap.ic_treehole_hehe);
                    tv_collect.setText("关注");
                    cancleCollect(params);
                } else {
                    ic_collect.setImageResource(R.mipmap.ic_treehole_hehe_item_press);
                    tv_collect.setText("已关注");
                    collect(params);
                }
            }
        });

        getComments();
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

    private void collect(final RequestParams params){
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/coscollect/add", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                course.setIscollect(true);
            }
        });
    }

    private void cancleCollect(final RequestParams params){
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/coscollect/cancle", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                course.setIscollect(false);
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
        getComments();
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
                addComments();
            }
        }, 500);
    }

    private void getComments(){
        RequestParams params = new RequestParams();
        params.add("pageSize", String.valueOf(pageSize));
        params.add("pageIndex", String.valueOf(pageIndex));
        params.add("studentid", Define.USER_NAME);
        params.add("courseid", course.getId());
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/evaluation/get", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                super.onSuccess(statusCode, headers, jsonArray);
                rl_cover_loading.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                }, 500);
                comments.clear();
                for(int i = 0; i < jsonArray.length(); i++){
                    try {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Comment comment = new Comment();
                        comment.setId(object.getString("id"));
                        if(object.has("student")){
                            comment.setStudent_id(object.getJSONObject("student").getString("id"));
                            comment.setStudent_avatar(object.getJSONObject("student").getString("avatar_url"));
                            comment.setStudent_name(object.getJSONObject("student").getString("name"));
                        }
                        if(object.has("create_time"))
                            comment.setCreate_time(object.getString("create_time"));
                        if(object.has("comment"))
                            comment.setComment(object.getString("comment"));
                        if(object.has("num_like"))
                            comment.setNum_like(object.getInt("num_like"));
                        if(object.has("score"))
                            comment.setScore(object.getInt("score"));
                        if(object.has("islike")){
                            if(object.getString("islike").equals("true"))
                                comment.setIsLike(true);
                            else
                                comment.setIsLike(false);
                        }

                        comments.add(comment);
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
                if (comments.size() < pageSize * pageIndex) {
                    listView.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
                } else {
                    listView.setMode(PullToRefreshBase.Mode.BOTH);
                }
            }
        });
    }
    private void addComments(){
        RequestParams params = new RequestParams();
        params.add("pageSize", String.valueOf(pageSize));
        params.add("pageIndex", String.valueOf(pageIndex));
        params.add("studentid", Define.USER_NAME);
        params.add("courseid", course.getId());
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/evaluation/get", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                super.onSuccess(statusCode, headers, jsonArray);
                if(jsonArray.length() != 0){
                    pageIndex++;
                }
                for(int i = 0; i < jsonArray.length(); i++){
                    try {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Comment comment = new Comment();
                        comment.setId(object.getString("id"));
                        if(object.has("student")){
                            comment.setStudent_id(object.getJSONObject("student").getString("id"));
                            comment.setStudent_avatar(object.getJSONObject("student").getString("avatar_url"));
                            comment.setStudent_name(object.getJSONObject("student").getString("name"));
                        }
                        if(object.has("create_time"))
                            comment.setCreate_time(object.getString("create_time"));
                        if(object.has("comment"))
                            comment.setComment(object.getString("comment"));
                        if(object.has("num_like"))
                            comment.setNum_like(object.getInt("num_like"));
                        if(object.has("score"))
                            comment.setScore(object.getInt("score"));
                        if(object.has("islike")){
                            if(object.getString("islike").equals("true")){
                                comment.setIsLike(true);
                            }
                            else{
                                comment.setIsLike(false);
                            }
                        }

                        comments.add(comment);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                listView.onRefreshComplete();
            }
        });
    }

    class CourseAdapter extends BaseAdapter{

        private Context context;
        private Course course;

        public CourseAdapter(Context context, Course course) {
            this.context = context;
            this.course = course;
        }

        @Override
        public int getCount() {
            return comments.size() + 2;
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
            if(position == 0){
                view = View.inflate(context, R.layout.item_course, null);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.item_course_name);
                holder.type_and_credit = (TextView) view.findViewById(R.id.item_course_type_and_credit);
                holder.create_time = (TextView) view.findViewById(R.id.item_course_create_time);
                holder.type = (TextView) view.findViewById(R.id.item_course_type);
                holder.teacher_name = (TextView) view.findViewById(R.id.item_course_teacher_name);
                holder.teacher_id = (TextView) view.findViewById(R.id.item_course_teacher_id);
                holder.credit = (TextView) view.findViewById(R.id.item_course_credit);
                holder.location = (TextView) view.findViewById(R.id.item_course_location);
                holder.introduction = (TextView) view.findViewById(R.id.item_course_introduction);
                holder.top_comment_on = (TextView) view.findViewById(R.id.item_course_top_comment_on);
                holder.top_comment_off = (TextView) view.findViewById(R.id.item_course_top_comment_off);
                holder.top_comment_bar = (EvaluationView) view.findViewById(R.id.item_course_top_comment_bar);
                if(course != null){
                    holder.name.setText(course.getName());
                    holder.type_and_credit.setText(Define.COURSE_TYPES[course.getType()]+" | "+course.getCredit()+"分");
                    holder.create_time.setText("创建时间:"+ CommonUtil.getDate(Long.parseLong(course.getCreate_time())/1000));
                    holder.type.setText(Define.COURSE_TYPES[course.getType()]);
                    holder.credit.setText(course.getCredit()+"");
                    holder.teacher_name.setText(course.getTeacher_name());
                    holder.teacher_id.setText("(id:"+course.getTeacher_id()+")");
                    holder.location.setText(course.getLocation());
                    if(course.getIntroduction() != null)
                        holder.introduction.setText(course.getIntroduction());
                    if(course.getNum_evaluate() == 0){
                        holder.top_comment_on.setVisibility(View.GONE);
                        holder.top_comment_off.setVisibility(View.VISIBLE);
                    }
                    else{
                        DecimalFormat df = new DecimalFormat("#.00");
                        holder.top_comment_on.setText(df.format(course.getAvg_score()));
                        holder.top_comment_bar.setLike((int) (course.getAvg_score()/2));
                    }
                }
                return view;
            }
            else if(position == 1){
                view = View.inflate(context, R.layout.item_comment_top, null);

                LinearLayout rl_comment_on, rl_comment_off;
                EvaluationView comment_bar;
                TextView avg_score;
                comment_bar = (EvaluationView) view.findViewById(R.id.item_course_comment_bar);
                rl_comment_on = (LinearLayout) view.findViewById(R.id.item_course_comment_on);
                rl_comment_off = (LinearLayout) view.findViewById(R.id.item_course_comment_off);
                avg_score = (TextView) view.findViewById(R.id.item_course_avg_score);

                if(course.getNum_evaluate() == 0){
                    rl_comment_off.setVisibility(View.VISIBLE);
                    rl_comment_on.setVisibility(View.GONE);
                }
                else{
                    DecimalFormat df = new DecimalFormat("#.00");
                    avg_score.setText(df.format(course.getAvg_score()));
                    comment_bar.setLike((int) (course.getAvg_score()/2));
                }
            }
            else{
                final int p = position - 2;
                final CommentHolder commentHolder;
                if(view == null || view.getTag() == null){
                    view = View.inflate(context, R.layout.item_comment, null);
                    commentHolder = new CommentHolder();

                    commentHolder.avatar = (ImageView) view.findViewById(R.id.item_comment_avatar);
                    commentHolder.student_name = (TextView) view.findViewById(R.id.item_comment_name);
                    commentHolder.create_time = (TextView) view.findViewById(R.id.item_comment_create_time);
                    commentHolder.comment = (TextView) view.findViewById(R.id.item_comment_content);
                    commentHolder.ic_like = (ImageView) view.findViewById(R.id.item_comment_ic_like);
                    commentHolder.tv_like = (TextView) view.findViewById(R.id.item_comment_tv_like);
                    commentHolder.rl_like = (LinearLayout) view.findViewById(R.id.item_comment_rl_like);
                    commentHolder.comment_bar = (EvaluationView) view.findViewById(R.id.item_comment_bar);

                    view.setTag(commentHolder);
                }
                else{
                    commentHolder = (CommentHolder) view.getTag();
                }
                ImageLoader.getInstance().displayImage(Define.IMAGE_HOST + comments.get(p).getStudent_avatar(), commentHolder.avatar);
                commentHolder.student_name.setText(comments.get(p).getStudent_name());
                commentHolder.create_time.setText(CommonUtil.getFormatedDateTime(Long.parseLong(comments.get(p).getCreate_time()) / 1000));
                commentHolder.comment.setText(comments.get(p).getComment());
                commentHolder.tv_like.setText(comments.get(p).getNum_like() + "");
                commentHolder.comment_bar.setLike(comments.get(p).getScore());
                if(comments.get(p).isLike()){
                    commentHolder.ic_like.setImageResource(R.mipmap.statusdetail_comment_icon_like_highlighted);
                }
                else{
                    commentHolder.ic_like.setImageResource(R.mipmap.statusdetail_comment_icon_like);
                }
                commentHolder.rl_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestParams params = new RequestParams();
                        params.put("evaluation_id", comments.get(p).getId());
                        params.put("student_id", Define.USER_NAME);
                        if(comments.get(p).isLike()){
                            commentHolder.ic_like.setImageResource(R.mipmap.statusdetail_comment_icon_like);
                            comments.get(p).setNum_like(comments.get(p).getNum_like() - 1);
                            commentHolder.tv_like.setText(comments.get(p).getNum_like() + "");
                            cancleLike(params, p);
                        }
                        else{
                            commentHolder.ic_like.setImageResource(R.mipmap.statusdetail_comment_icon_like_highlighted);
                            comments.get(p).setNum_like(comments.get(p).getNum_like() + 1);
                            commentHolder.tv_like.setText(comments.get(p).getNum_like() + "");
                            like(params, p);
                        }
                    }
                });
            }

            return view;
        }

    }

    class ViewHolder{
        TextView name, type_and_credit, create_time, type, credit, teacher_name, teacher_id,
                location, introduction, avg_score, top_comment_on, top_comment_off;

        EvaluationView top_comment_bar;
    }

    class CommentHolder{
        ImageView avatar, ic_like;
        TextView student_name, create_time, tv_like, comment;
        EvaluationView comment_bar;
        LinearLayout rl_like;
    }


    private void like(final RequestParams params, final int position){
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/evalike/add", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                comments.get(position).setIsLike(true);
            }
        });
    }
    private void cancleLike(final RequestParams params, final int position){
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/evalike/cancle", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                comments.get(position).setIsLike(false);
            }
        });
    }
}
