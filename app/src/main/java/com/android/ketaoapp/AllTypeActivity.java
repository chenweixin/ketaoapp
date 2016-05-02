package com.android.ketaoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class AllTypeActivity extends Activity implements View.OnClickListener{

    public static final int RENWEN = 0;
    public static final int RENWEN_HEXIN = 1;
    public static final int SHEKE = 2;
    public static final int SHEKE_HEXIN = 3;
    public static final int KEJI = 4;
    public static final int KEJI_HEIN = 5;

    private Context context;

    private LinearLayout renwen, renwen_hexin, sheke, sheke_hexin, keji, keji_hexin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_type);

        context = AllTypeActivity.this;

        renwen = (LinearLayout) findViewById(R.id.all_type_renwen);
        renwen_hexin = (LinearLayout) findViewById(R.id.all_type_renwen_hexin);
        sheke = (LinearLayout) findViewById(R.id.all_type_sheke);
        sheke_hexin = (LinearLayout) findViewById(R.id.all_type_sheke_hexin);
        keji = (LinearLayout) findViewById(R.id.all_type_keji);
        keji_hexin = (LinearLayout) findViewById(R.id.all_type_keji_hexin);

        renwen.setOnClickListener(this);
        renwen_hexin.setOnClickListener(this);
        sheke.setOnClickListener(this);
        sheke_hexin.setOnClickListener(this);
        keji.setOnClickListener(this);
        keji_hexin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, CourseListActivity.class);
        intent.putExtra(CourseListActivity.LIST_TYPE, CourseListActivity.TYPE_COURSE);
        switch (v.getId()){
            case R.id.all_type_renwen:
                intent.putExtra(CourseListActivity.TYPE, RENWEN);
                break;
            case R.id.all_type_renwen_hexin:
                intent.putExtra(CourseListActivity.TYPE, RENWEN_HEXIN);
                break;
            case R.id.all_type_sheke:
                intent.putExtra(CourseListActivity.TYPE, SHEKE);
                break;
            case R.id.all_type_sheke_hexin:
                intent.putExtra(CourseListActivity.TYPE, SHEKE_HEXIN);
                break;
            case R.id.all_type_keji:
                intent.putExtra(CourseListActivity.TYPE, KEJI);
                break;
            case R.id.all_type_keji_hexin:
                intent.putExtra(CourseListActivity.TYPE, KEJI_HEIN);
                break;
        }
        startActivity(intent);
    }
}
