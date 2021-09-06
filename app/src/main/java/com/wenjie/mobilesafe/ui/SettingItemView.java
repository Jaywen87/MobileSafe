package com.wenjie.mobilesafe.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wenjie.mobilesafe.R;


public class SettingItemView extends RelativeLayout {

	private final String TAG = "SettingItemView";
	private CheckBox cb_status;
	private TextView tv_desc;
	private TextView tv_title;
	private String disc_off;
	private String disc_on;
	private String title;


	private void iniView(Context context) {

		View.inflate(context, R.layout.setting_item_view, this);
		cb_status = (CheckBox) this.findViewById(R.id.cb_status);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		if(title != null) {
			tv_title.setText(title);
		}
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.SettingItemView);
		disc_off = typedArray.getString(R.styleable.SettingItemView_disc_off);
		disc_on = typedArray.getString(R.styleable.SettingItemView_disc_on);
		title = typedArray.getString(R.styleable.SettingItemView_title);
		typedArray.recycle();
		iniView(context);
	}

	public boolean isChecked(){
		return cb_status.isChecked();
	}
	

	public void setChecked(boolean checked){
		cb_status.setChecked(checked);
		tv_desc.setText(checked?disc_on:disc_off);
	}


	public void setDescText(String text){
		tv_desc.setText(text);
	}
	
	

}
