package com.android.ketaoapp.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.ketaoapp.R;

/**
 * TODO: document your custom view class.
 */
public class DetailTitleView extends RelativeLayout implements View.OnClickListener {

    private TextView tv_title;

    private ImageView iv_left;
    private ImageView iv_right;


    public interface OnTitleRightButtonClickListener {
        void onRightButtonClick(View v);
    }

    public interface OnTitleLeftButtonClickListener {
        void onLeftButtonClick(View v);
    }

    /**
     * 右边按钮监听
     */
    private OnTitleRightButtonClickListener rightBtnListener;

    /**
     * 左边按钮监听
     */
    private OnTitleLeftButtonClickListener leftBtnListener;

    public DetailTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.detail_titleview, this);
        iv_left = (ImageView) this.findViewById(R.id.iv_left);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ImageView);
        int left_src = typedArray.getResourceId(R.styleable.ImageView_left_src,
                0);
        if (left_src != 0) {
            iv_left.setImageResource(left_src);
            iv_left.setVisibility(View.VISIBLE);
            iv_left.setOnClickListener(this);
        }

        tv_title = (TextView) this.findViewById(R.id.tv_title);
        String item_title = attrs.getAttributeValue(
                "http://schemas.android.com/apk/res-auto",
                "item_title");
        if (item_title != null) {
            tv_title.setText(item_title);
            tv_title.setVisibility(View.VISIBLE);
        }

        iv_right = (ImageView) this.findViewById(R.id.iv_right);

        int right_src = typedArray.getResourceId(
                R.styleable.ImageView_right_src, 0);
        if (right_src != 0) {
            iv_right.setImageResource(right_src);
            iv_right.setVisibility(View.VISIBLE);
            iv_right.setOnClickListener(this);
        }
    }

    /**
     * 左边按钮默认为返回，如果左边按钮只有返回操作，设置为true，则不需要再设置leftBtnListener。
     * 如果左边按钮不是返回操作，则设置为false，另外设置leftBtnListener。
     */
//    private boolean backBtnClickable = true;
//
//    public boolean isBackBtnClickable() {
//        return backBtnClickable;
//    }
//
//    public void setBackBtnClickable(boolean backBtnClickable) {
//        this.backBtnClickable = backBtnClickable;
//    }

    public void setRightBtnListener(
            OnTitleRightButtonClickListener rightBtnListener) {
        this.rightBtnListener = rightBtnListener;
    }

    public void setLeftBtnListener(
            OnTitleLeftButtonClickListener leftBtnListener) {
        this.leftBtnListener = leftBtnListener;
    }

    public void setItemTitle(String title)
    {
        tv_title.setText(title);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (R.id.iv_left == id) {

            if (null != leftBtnListener) {
                // 加这个监听为了按返回键时可以处理想要处理的动作
                leftBtnListener.onLeftButtonClick(v);
            }
            Context context = v.getContext();
            if (context instanceof Activity) {
                ((Activity) context).onBackPressed();
            }
        } else if (R.id.iv_right == id) {
            if (null != rightBtnListener)
                rightBtnListener.onRightButtonClick(v);
        }
    }
}
