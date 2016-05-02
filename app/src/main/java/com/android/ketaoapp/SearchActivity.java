package com.android.ketaoapp;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.ketaoapp.config.Define;
import com.android.ketaoapp.entity.Search;
import com.android.ketaoapp.util.DataBaseHelper;
import com.android.ketaoapp.util.DoInDataBaseUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity {

    private Context context;
    private SQLiteDatabase db;
    private DataBaseHelper helper;

    private ListView listView;
    private SearchAdapter adapter;
    private EditText search_bar;
    private TextView tv_cancle;

    private List<Search> searches;
    private List<Search> searches_db = new ArrayList<>();
    private String str_search = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = SearchActivity.this;

        listView = (ListView) findViewById(R.id.lv_search_record);
        search_bar = (EditText) findViewById(R.id.edt_search);
        tv_cancle = (TextView) findViewById(R.id.search_cancle);


        helper = new DataBaseHelper(this, "ketao.db");
        db = helper.getWritableDatabase();
        searches = DoInDataBaseUtil.getSearch(db, Define.USER_NAME);
        searches_db.addAll(searches);

        if(searches.size() != 0){
            searches.add(null);
        }

        adapter = new SearchAdapter();
        listView.setAdapter(adapter);

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
            }
        });

        search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (search_bar.getText().toString().equals("")) {
                        ((Activity) context).finish();
                    } else {
                        long time = System.currentTimeMillis() / 1000;
                        Search search = new Search();
                        search.setUser_id(Define.USER_NAME);
                        search.setTimestamp((int) time);
                        search.setSearch(search_bar.getText().toString());
                        db = helper.getWritableDatabase();
                        DoInDataBaseUtil.insertSearchRecord(db, search);
                        ((Activity) context).finish();
                    }
                    return true;
                }
                return false;
            }
        });

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searches.clear();
                String str = s.toString();
                str_search = str;
                for(int i = 0;i < searches_db.size(); i++){
                    if(searches_db.get(i).getSearch().contains(str)){
                        searches.add(searches_db.get(i));
                    }
                }
                searches.add(null);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == searches.size() - 1){
                    searches.clear();
                    adapter.notifyDataSetChanged();
                    db = helper.getWritableDatabase();
                    DoInDataBaseUtil.deleteSearchRecord(db, Define.USER_NAME);

                }
            }
        });
    }

    class SearchAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return searches.size();
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
            if(position == searches.size() - 1){
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
                if(search_bar.getText().toString().equals("")){
                    holder.search.setText(searches.get(position).getSearch());
                }
                else{
                    int start = searches.get(position).getSearch().indexOf(str_search);
                    int end = start + str_search.length();
                    SpannableStringBuilder styled = new SpannableStringBuilder(searches.get(position).getSearch());
                    styled.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)),
                            start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.search.setText(styled);
                }

            }

            return view;
        }
    }

    class SearchHolder{
        TextView search;
    }
}
