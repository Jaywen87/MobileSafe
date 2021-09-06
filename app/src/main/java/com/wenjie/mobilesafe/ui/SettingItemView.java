package com.wenjie.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wenjie.mobilesafe.R;


public class SettingItemView extends RelativeLayout {
	
	private CheckBox cb_status;
	private TextView tv_desc;
	private TextView tv_title;
	

	private void iniView(Context context) {

		View.inflate(context, R.layout.setting_item_view, this);
		cb_status = (CheckBox) this.findViewById(R.id.cb_status);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		tv_title = (TextView) this.findViewById(R.id.tv_title);

	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
	}

	public boolean isChecked(){
		return cb_status.isChecked();
	}
	

	public void setChecked(boolean checked){
		cb_status.setChecked(checked);
	}


	public void setDescText(String text){
		tv_desc.setText(text);
	}
	
	

}
