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
import android.widget.TextView;
import android.widget.Toast;

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

public class SpreadListActivity extends Activity implements PullToRefreshBase.OnRefreshListener2{

    private Context context;
    private int pageSize = 10, pageIndex = 0;

    private List<Spread> spreads = new ArrayList<Spread>();
    private SpreadAdapter adapter;

    private PullToRefreshListView listView;
    private FrameLayout rl_cover_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spread);

        rl_cover_loading = (FrameLayout) findViewById(R.id.rl_cover_loading);
        listView = (PullToRefreshListView) findViewById(R.id.lv_reccourse);

        context = this;
        adapter = new SpreadAdapter(context, spreads);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setShowIndicator(false);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);

        getSpreads(true);
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

    private void getSpreads(final boolean isDown){
        RequestParams params = new RequestParams();
        params.add("pageSize", String.valueOf(pageSize));
        params.add("pageIndex", String.valueOf(pageIndex));
        HTTPRequestUtil.get(Define.SERVER_HOST + "/mobile/spread/getall", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                if(isDown){
                    spreads.clear();
                }
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
                if(response.length() != 0){
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
                    if (spreads.size() < pageSize * pageIndex) {
                        listView.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
                    } else {
                        listView.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                }
                else{
                    listView.onRefreshComplete();
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
        getSpreads(true);
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
                getSpreads(false);
            }
        }, 500);

    }

    class SpreadAdapter extends BaseAdapter{

        private List<Spread> spreads;
        private Context context;

        public SpreadAdapter(Context context, List<Spread> spreads) {
            this.spreads = spreads;
            this.context = context;
        }

        @Override
        public int getCount() {
            return spreads.size();
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
        public View getView(int position, View v, ViewGroup parent) {
            final ViewHolder holder;
            if(v != null){
                holder = (ViewHolder) v.getTag();
            }
            else{
                v = View.inflate(context, R.layout.item_spread, null);
                holder = new ViewHolder();
                holder.title = (TextView) v.findViewById(R.id.item_spread_title);
                holder.time = (TextView) v.findViewById(R.id.item_spread_create_time);
                holder.img = (ImageView) v.findViewById(R.id.item_spread_img);
                v.setTag(holder);
            }

            holder.title.setText(spreads.get(position).getTitle());
            String time = CommonUtil.getFormatedDateTime(Long.parseLong(spreads.get(position).getCreate_time()) / 1000);
            holder.time.setText(time);
            holder.img.setImageBitmap(null);
            ImageLoader.getInstance().displayImage(Define.IMAGE_HOST+spreads.get(position).getPoster_url(), holder.img, Define.getImageOptions());

            return v;
        }
    }

    class ViewHolder{
        TextView title, time;
        ImageView img;
    }
}
