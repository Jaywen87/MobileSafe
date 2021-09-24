package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wenjie.mobilesafe.db.dao.BlackNumberDao;
import com.wenjie.mobilesafe.domain.BlackNumberInfo;

import java.util.List;
import java.util.Random;

public class CallSmsSafeActivity extends AppCompatActivity {

    private static final String TAG = "CallSmsSafeActivity";
    private ListView lv_callsms_safe;
    private BlackNumberDao dao;
    private BlackNumberAdapter mAdapter;
    private List<BlackNumberInfo> infos;
    private AlertDialog alertDialog;
    private EditText et_black_number;
    private CheckBox cb_phone;
    private CheckBox cb_sms;
    private LinearLayout ll_loading;
    private int offset = 0;
    private int maxNumber = 20;
    private boolean isPaginationShow = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);
        lv_callsms_safe = findViewById(R.id.lv_callsms_safe);
        ll_loading = findViewById(R.id.ll_loading);
        dao = new BlackNumberDao(this);;
        fillData();
        //listview注册一个滚动事件的监听器
        lv_callsms_safe.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        //判断当前位置listview滚动位置
                        //获取当前最后一个可见条目的位置
                        int lastposition = lv_callsms_safe.getLastVisiblePosition();
                        if (lastposition == infos.size() -1 ) {
                            offset += maxNumber;
                            fillData();
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸滚动状态
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://惯性滑行状态
                        break;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


    }

    private void fillData() {
        //ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //ll_loading.setLayoutParams(layoutParams);
        ll_loading.setVisibility(View.VISIBLE);

        new Thread(){
            @Override
            public void run() {
                if ((infos == null) || isPaginationShow) {
                    infos = dao.findPart(offset,maxNumber);
                } else {
                    infos.addAll(dao.findPart(offset,maxNumber));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);
                        if (mAdapter == null) {
                            mAdapter = new BlackNumberAdapter();
                            lv_callsms_safe.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }.start();
    }

    public void addBlackNumber(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
        alertDialog = builder.create();
        View contentView = View.inflate(this, R.layout.dialog_add_blacknumber, null);
        et_black_number = contentView.findViewById(R.id.et_black_number);
        cb_phone = contentView.findViewById(R.id.cb_phone);
        cb_sms = contentView.findViewById(R.id.cb_sms);
        Button ok_btn = contentView.findViewById(R.id.ok_btn);
        Button cancer_btn = contentView.findViewById(R.id.cancer_btn);
        alertDialog.setView(contentView, 0, 0, 0, 0);
        alertDialog.show();
        cancer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mode = null;
                String blackNumber = et_black_number.getText().toString().trim();
                if (TextUtils.isEmpty(blackNumber)) {
                    Toast.makeText(CallSmsSafeActivity.this,"号码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                } else if (!cb_phone.isChecked() && !cb_sms.isChecked()) {
                    Toast.makeText(CallSmsSafeActivity.this, "请选择拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (cb_phone.isChecked() && cb_sms.isChecked()) {
                        mode = "3";
                    } else if (cb_sms.isChecked()) {
                        mode = "2";
                    } else if (cb_phone.isChecked()) {
                        mode = "1";
                    }
                }
                if (dao.add(blackNumber, mode)) {
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.setNumber(blackNumber);
                    blackNumberInfo.setMode(mode);
                    infos.add(0,blackNumberInfo);
                    mAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(CallSmsSafeActivity.this, "此号码已经存在黑名单中", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    private class BlackNumberAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return infos.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view;

            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
                holder = new ViewHolder();
                holder.tv_number = view.findViewById(R.id.tv_black_number);
                holder.tv_mode = view.findViewById(R.id.tv_block_mode);
                holder.iv_delete = view.findViewById(R.id.iv_delete);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            holder.tv_number.setText(infos.get(position).getNumber());
            String mode = infos.get(position).getMode();
            if ("1".equals(mode)) {
                holder.tv_mode.setText("电话拦截");
            } else if ("2".equals(mode)) {
                holder.tv_mode.setText("短息拦截");
            } else {
                holder.tv_mode.setText("全部拦截");
            }
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
                    builder.setMessage("确定要删除此条记录吗？");
                    builder.setTitle("警告");
                    builder.setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dao.delete(infos.get(position).getNumber());
                            infos.remove(position);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.show();
                }
            });
            return view;
        }
    }

    static class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
        ImageView iv_delete;
    }
}