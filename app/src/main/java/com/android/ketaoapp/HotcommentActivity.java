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

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HotcommentActivity extends Activity implements PullToRefreshBase.OnRefreshListener2{

    private Context context;
    private int pageSize = 10, pageIndex = 0;
    private List<Comment> comments = new ArrayList<Comment>();
    private CommentAdapter adapter;

    private PullToRefreshListView listView;
    private FrameLayout rl_cover_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotcomment);

        context = HotcommentActivity.this;
        listView = (PullToRefreshListView) findViewById(R.id.lv_hotcomment);
        rl_cover_loading = (FrameLayout) findViewById(R.id.rl_cover_loading);

        adapter = new CommentAdapter(context);
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setShowIndicator(false);
        listView.setOnRefreshListener(this);

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

    private void getComments(){
        RequestParams params = new RequestParams();
        params.add("pageSize", String.valueOf(pageSize));
        params.add("pageIndex", String.valueOf(pageIndex));
        params.add("student_id", Define.USER_NAME);
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/evaluation/gethot", params, new JsonHttpResponseHandler() {
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
        params.add("student_id", Define.USER_NAME);
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/evaluation/gethot", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                super.onSuccess(statusCode, headers, jsonArray);
                if (jsonArray.length() != 0) {
                    pageIndex++;
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
                        if (object.has("islike")) {
                            if (object.getString("islike").equals("true")) {
                                comment.setIsLike(true);
                            } else {
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
                commentHolder.create_time = (TextView) view.findViewById(R.id.item_comment_create_time);
                commentHolder.comment = (TextView) view.findViewById(R.id.item_comment_content);
                commentHolder.ic_like = (ImageView) view.findViewById(R.id.item_comment_ic_like);
                commentHolder.tv_like = (TextView) view.findViewById(R.id.item_comment_tv_like);
                commentHolder.rl_like = (LinearLayout) view.findViewById(R.id.item_comment_rl_like);

                view.setTag(commentHolder);
            }
            else{
                commentHolder = (CommentHolder) view.getTag();
            }
            ImageLoader.getInstance().displayImage(Define.IMAGE_HOST+comments.get(p).getStudent_avatar(), commentHolder.avatar);
            commentHolder.student_name.setText(comments.get(p).getStudent_name());
            commentHolder.create_time.setText(comments.get(p).getCreate_time());
            commentHolder.comment.setText(comments.get(p).getComment());
            commentHolder.tv_like.setText(comments.get(p).getNum_like() + "");
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
        TextView student_name, create_time, tv_like, comment;
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
