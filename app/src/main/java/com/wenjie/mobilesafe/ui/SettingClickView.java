package com.wenjie.mobilesafe.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wenjie.mobilesafe.R;


public class SettingClickView extends RelativeLayout {

	private final String TAG = "SettingClickView";
	private ImageView iv_status;
	private TextView tv_desc;
	private TextView tv_title;
	private String disc_off;
	private String disc_on;
	private String title;


	private void iniView(Context context) {

		View.inflate(context, R.layout.setting_click_view, this);
		iv_status = (ImageView) this.findViewById(R.id.iv_status);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		if(title != null) {
			tv_title.setText(title);
		}
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.TextView);
		disc_off = typedArray.getString(R.styleable.TextView_disc_off);
		disc_on = typedArray.getString(R.styleable.TextView_disc_on);
		title = typedArray.getString(R.styleable.TextView_title);
		typedArray.recycle();
		iniView(context);
	}

	public void setDescText(String text){
		tv_desc.setText(text);
	}

	public void setTitleText(String text){
		tv_title.setText(text);
	}
	
	

}
