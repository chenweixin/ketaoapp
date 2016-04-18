package com.android.ketaoapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.*;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.ketaoapp.R;

public class EvaluationView extends LinearLayout implements OnClickListener{

	private ImageView iv_1, iv_2, iv_3, iv_4, iv_5;

	private OnEvaluationClickListener onEvaluationClickListener;

	public void setOnEvaluationClickListener(OnEvaluationClickListener onEvaluationClickListener){
		this.onEvaluationClickListener = onEvaluationClickListener;
	}
	/**
	 * 星星点击接口
	 * @author Administrator
	 *
	 */
	public interface OnEvaluationClickListener{
		public void onClick(int position);
	}

	public EvaluationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		View.inflate(context, R.layout.my_evaluation, this);
		
		iv_1 = (ImageView) findViewById(R.id.iv_1);
		iv_2 = (ImageView) findViewById(R.id.iv_2);
		iv_3 = (ImageView) findViewById(R.id.iv_3);
		iv_4 = (ImageView) findViewById(R.id.iv_4);
		iv_5 = (ImageView) findViewById(R.id.iv_5);
		
		iv_1.setOnClickListener(this);
		iv_2.setOnClickListener(this);
		iv_3.setOnClickListener(this);
		iv_4.setOnClickListener(this);
		iv_5.setOnClickListener(this);
		
		String number = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res-auto",
				"likenumber");
		if(number != null)
		{
			int likenumber = Integer.parseInt(number);
			setLike(likenumber);
		}
	}
	
	public void setLike(int likenumber)
	{
		switch(likenumber)
		{
		case 1:
			iv_1.setImageResource(R.mipmap.evaluation_on);
			iv_2.setImageResource(R.mipmap.evaluation_off);
			iv_3.setImageResource(R.mipmap.evaluation_off);
			iv_4.setImageResource(R.mipmap.evaluation_off);
			iv_5.setImageResource(R.mipmap.evaluation_off);
			break;
		case 2:
			iv_1.setImageResource(R.mipmap.evaluation_on);
			iv_2.setImageResource(R.mipmap.evaluation_on);
			iv_3.setImageResource(R.mipmap.evaluation_off);
			iv_4.setImageResource(R.mipmap.evaluation_off);
			iv_5.setImageResource(R.mipmap.evaluation_off);
			break;
		case 3:
			iv_1.setImageResource(R.mipmap.evaluation_on);
			iv_2.setImageResource(R.mipmap.evaluation_on);
			iv_3.setImageResource(R.mipmap.evaluation_on);
			iv_4.setImageResource(R.mipmap.evaluation_off);
			iv_5.setImageResource(R.mipmap.evaluation_off);
			break;
		case 4:
			iv_1.setImageResource(R.mipmap.evaluation_on);
			iv_2.setImageResource(R.mipmap.evaluation_on);
			iv_3.setImageResource(R.mipmap.evaluation_on);
			iv_4.setImageResource(R.mipmap.evaluation_on);
			iv_5.setImageResource(R.mipmap.evaluation_off);
			break;
		case 5:
			iv_1.setImageResource(R.mipmap.evaluation_on);
			iv_2.setImageResource(R.mipmap.evaluation_on);
			iv_3.setImageResource(R.mipmap.evaluation_on);
			iv_4.setImageResource(R.mipmap.evaluation_on);
			iv_5.setImageResource(R.mipmap.evaluation_on);
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.iv_1:
			if(onEvaluationClickListener != null){
				onEvaluationClickListener.onClick(1);
				setLike(1);
			}
			break;
		case R.id.iv_2:
			if(onEvaluationClickListener != null){
				onEvaluationClickListener.onClick(2);
				setLike(2);
			}
			break;
		case R.id.iv_3:
			if(onEvaluationClickListener != null){
				onEvaluationClickListener.onClick(3);
				setLike(3);
			}
			break;
		case R.id.iv_4:
			if(onEvaluationClickListener != null){
				onEvaluationClickListener.onClick(4);
				setLike(4);
			}
			break;
		case R.id.iv_5:
			if(onEvaluationClickListener != null){
				onEvaluationClickListener.onClick(5);
				setLike(5);
			}
			break;
		}
	}

}
