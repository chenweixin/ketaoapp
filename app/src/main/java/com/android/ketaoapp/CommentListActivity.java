package com.android.ketaoapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.android.ketaoapp.view.DetailTitleView;
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

public class CommentListActivity extends Activity implements PullToRefreshBase.OnRefreshListener2{
    public static final String LIST_TYPE = "LIST_TYPE";
    public static final int HOT_COMMENT = 0;
    public static final int MY_COMMENT = 1;

    private Context context;
    private int list_type = 0;//0:热门评论 1:我的评论
    private int pageSize = 10, pageIndex = 0;
    private String request_url;

    private List<Comment> comments = new ArrayList<Comment>();
    private CommentAdapter adapter;

    private PullToRefreshListView listView;
    private DetailTitleView titleView;
    private FrameLayout rl_cover_loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);

        context = CommentListActivity.this;
        list_type = getIntent().getIntExtra(LIST_TYPE, 0);

        listView = (PullToRefreshListView) findViewById(R.id.lv_comment);
        rl_cover_loading = (FrameLayout) findViewById(R.id.rl_cover_loading);
        titleView = (DetailTitleView) findViewById(R.id.comment_title_view);

        adapter = new CommentAdapter(context);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setShowIndicator(false);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);

        switch (list_type){
            case HOT_COMMENT:
                request_url = Define.SERVER_HOST + "/mobile/evaluation/gethot";
                titleView.setItemTitle("热门评论");
                break;
            case MY_COMMENT:
                request_url = Define.SERVER_HOST + "/mobile/evaluation/getmy";
                titleView.setItemTitle("我的评论");
                break;
        }
        getComments(true);
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
        getComments(true);
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
                getComments(false);
            }
        }, 500);
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

    private void getComments(final boolean isDown){
        RequestParams params = new RequestParams();
        params.add("pageSize", String.valueOf(pageSize));
        params.add("pageIndex", String.valueOf(pageIndex));
        params.add("student_id", Define.USER_NAME);
        HTTPRequestUtil.get(request_url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                super.onSuccess(statusCode, headers, jsonArray);
                if(isDown){
                    comments.clear();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Comment comment = new Comment();
                        comment.setId(object.getString("id"));
                        if (object.has("student")) {
                            comment.setStudent_id(object.getJSONObject("student").getString("id"));
                            comment.setStudent_avatar(object.getJSONObject("student").getString("avatar_url"));
                            comment.setStudent_name(object.getJSONObject("student").getString("name"));
                        }
                        if (object.has("create_time"))
                            comment.setCreate_time(object.getString("create_time"));
                        if (object.has("comment"))
                            comment.setComment(object.getString("comment"));
                        if (object.has("num_like"))
                            comment.setNum_like(object.getInt("num_like"));
                        if (object.has("score"))
                            comment.setScore(object.getInt("score"));
                        if(object.has("course")){
                            JSONObject jsonObject = (JSONObject) object.get("course");
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
                            comment.setCourse(course);

                        }
                        if (object.has("islike")) {
                            if (object.getString("islike").equals("true"))
                                comment.setIsLike(true);
                            else
                                comment.setIsLike(false);
                        }

                        comments.add(comment);
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
                    if (comments.size() < pageSize * pageIndex) {
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

    class CommentAdapter extends BaseAdapter {

        private Context context;

        public CommentAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return comments.size();
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
            final CommentHolder commentHolder;
            final int p = position;
            if(view == null || view.getTag() == null){
                view = View.inflate(context, R.layout.item_comment, null);
                commentHolder = new CommentHolder();

                commentHolder.avatar = (ImageView) view.findViewById(R.id.item_comment_avatar);
                commentHolder.student_name = (TextView) view.findViewById(R.id.item_comment_name);
                commentHolder.comment = (TextView) view.findViewById(R.id.item_comment_content);
                commentHolder.create_time = (TextView) view.findViewById(R.id.item_comment_create_time);
                commentHolder.ic_like = (ImageView) view.findViewById(R.id.item_comment_ic_like);
                commentHolder.tv_like = (TextView) view.findViewById(R.id.item_comment_tv_like);
                commentHolder.rl_like = (LinearLayout) view.findViewById(R.id.item_comment_rl_like);
                commentHolder.course_name  = (TextView) view.findViewById(R.id.item_comment_course_name);

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
            commentHolder.course_name.setVisibility(View.VISIBLE);
            commentHolder.course_name.setText("来自 "+comments.get(p).getCourse().getName());
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

            return view;
        }

    }

    class CommentHolder{
        ImageView avatar, ic_like;
        TextView student_name, create_time, tv_like, comment, course_name;
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
