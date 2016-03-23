 package com.android.ketaoapp.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ketaoapp.R;

 public class MyTabView  extends LinearLayout implements View.OnClickListener{

	private onTabSelectListener listener;
	
	private List<ImageView> views ;

 	private List<LinearLayout> layout_views;

 	private List<TextView> text_views;
	
	private int currentPosition;

	private List<Integer> selectImages = new ArrayList<Integer>();
	
	private List<Integer> unSelectImages = new ArrayList<Integer>();
	
	
	public MyTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	
	}
	public void init(){
		
		View contentView =  LayoutInflater.from(getContext()).inflate(R.layout.my_tab, this);
		views = new ArrayList<ImageView>();
		views.add((ImageView) contentView.findViewById(R.id.rb_1));
		views.add((ImageView) contentView.findViewById(R.id.rb_2));
		views.add((ImageView) contentView.findViewById(R.id.rb_3));

		layout_views = new ArrayList<LinearLayout>();
		layout_views.add((LinearLayout) contentView.findViewById(R.id.rl_1));
		layout_views.add((LinearLayout) contentView.findViewById(R.id.rl_2));
		layout_views.add((LinearLayout) contentView.findViewById(R.id.rl_3));

		text_views = new ArrayList<TextView>();
		text_views.add((TextView) contentView.findViewById(R.id.tab_text_1));
		text_views.add((TextView) contentView.findViewById(R.id.tab_text_2));
		text_views.add((TextView) contentView.findViewById(R.id.tab_text_3));
		
		unSelectImages.add(R.mipmap.tabbar_home_normal);
		unSelectImages.add(R.mipmap.tabbar_message_normal);
		unSelectImages.add(R.mipmap.tabbar_profile_normal);
		
		selectImages.add(R.mipmap.tabbar_home_pressed);
		selectImages.add(R.mipmap.tabbar_message_pressed);
		selectImages.add(R.mipmap.tabbar_profile_pressed);

		for(LinearLayout rb:layout_views){
			rb.setOnClickListener(this);
		}
		
		Activity activity = (Activity) getContext();
		if(!(activity instanceof onTabSelectListener)){
			throw new IllegalStateException("Activity must implement TabSelectView OnTableSelectListener.");
		}
		listener = (onTabSelectListener) activity;
	}
	
	
	public interface onTabSelectListener{
		public void onTabSelect(int position);
	}


	
	@Override
	public void onClick(View v) {
		
		currentPosition = getSelectPosition(v);
		setPosition(currentPosition);
		
		listener.onTabSelect(currentPosition);
		
	}
	
	private void setPosition(int position) {
		
		for(int i=0;i<views.size();i++){
			if(position==i){
				views.get(i).setImageDrawable(getResources().getDrawable(selectImages.get(i)));
				text_views.get(i).setTextColor(getResources().getColor(R.color.blue));
			}else{
				views.get(i).setImageDrawable(getResources().getDrawable(unSelectImages.get(i)));
				text_views.get(i).setTextColor(getResources().getColor(R.color.tab_text_color));
			}
		}
	}
	
	
	private int getSelectPosition(View v) {
		for(int i =0;i<layout_views.size();i++){
			if(layout_views.get(i)==v){
				return i;
			}
		}
		return 0;
	}
	
	

}
