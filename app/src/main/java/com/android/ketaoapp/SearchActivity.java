package com.android.ketaoapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends Activity {

    private Context context;

    private ListView listView;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = SearchActivity.this;

        listView = (ListView) findViewById(R.id.lv_search_record);

        adapter = new SearchAdapter();
        listView.setAdapter(adapter);
    }

    class SearchAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 5;
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
            final SearchHolder holder;
            if(position == 4){
                view = View.inflate(context, R.layout.item_delete_record, null);
            }
            else{
                if(view == null || view.getTag() == null){
                    view = View.inflate(context, R.layout.item_search, null);
                    holder = new SearchHolder();
                    holder.search = (TextView) view.findViewById(R.id.item_search_record);
                }
                else{
                    holder = (SearchHolder) view.getTag();
                }
            }

            return view;
        }
    }

    class SearchHolder{
        TextView search;
    }
}
