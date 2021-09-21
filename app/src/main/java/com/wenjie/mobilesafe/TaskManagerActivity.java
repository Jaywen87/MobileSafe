package com.wenjie.mobilesafe;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wenjie.mobilesafe.domain.TaskInfo;
import com.wenjie.mobilesafe.engine.TaskInfoProvider;
import com.wenjie.mobilesafe.utils.SystemInfoUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerActivity extends AppCompatActivity {

    private LinearLayout ll_loading;
    private ListView lv_task_manager;
    private List<TaskInfo> allTaskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> sysTasKInfos;
    private TaskManagerAdapter mAdapter;
    private TextView tv_status;
    private TextView tv_process_count;
    private TextView tv_mem_info;
    private int ruuningProcessCount;
    private long availMem;
    private long totalMem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        tv_process_count = findViewById(R.id.tv_process_count);
        tv_mem_info = findViewById(R.id.tv_mem_info);
        ll_loading = findViewById(R.id.ll_loading);
        tv_status = findViewById(R.id.tv_status);
        lv_task_manager = findViewById(R.id.lv_task_manager);
        lv_task_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userTaskInfos != null && sysTasKInfos != null) {
                    if(view.getFirstVisiblePosition() <= userTaskInfos.size()) {
                        tv_status.setText("用户进程:" + userTaskInfos.size() + "个");
                    } else {
                        tv_status.setText("系统进程:" + sysTasKInfos.size() + "个");
                    }
                }
            }
        });
        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskInfo taskInfo;
                ViewHolder holder;
                if (position == 0) { //用户进程的标签
                    return;
                } else if (position == userTaskInfos.size() + 1) {
                    return;
                } else if (position < userTaskInfos.size() + 1) {
                    taskInfo = userTaskInfos.get(position - 1);
                } else {
                    taskInfo = sysTasKInfos.get(position -1 - userTaskInfos.size() - 1);
                }
                if(taskInfo.getPackagename().equals("com.wenjie.mobilesafe")) {
                    return;
                }
                holder = (ViewHolder) view.getTag();
                if (taskInfo.isChecked()) {
                    taskInfo.setChecked(false);
                    holder.cb_status.setChecked(false);
                } else {
                    taskInfo.setChecked(true);
                    holder.cb_status.setChecked(true);
                }
            }
        });

        fillData();

    }

    private void setTitle() {
        ruuningProcessCount = SystemInfoUtils.getRuuningProcessCount(this);
        tv_process_count.setText("运行中的进程：" + ruuningProcessCount);
        availMem = SystemInfoUtils.getAvailMem(this);
        totalMem = SystemInfoUtils.getTotalMem(this);
        tv_mem_info.setText("剩余/总内存："+ Formatter.formatFileSize(this, availMem) + "/" + Formatter.formatFileSize(this, totalMem));
    }

    private void fillData() {
        setTitle();
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                userTaskInfos = new ArrayList<>();
                sysTasKInfos  = new ArrayList<>();
                allTaskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
                for (TaskInfo taskInfo : allTaskInfos) {
                    if (taskInfo.isUserTask()) {
                        userTaskInfos.add(taskInfo);
                    } else {
                        sysTasKInfos.add(taskInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);
                        if(mAdapter == null) {
                            mAdapter = new TaskManagerAdapter();
                            lv_task_manager.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }.start();

    }

    /**
     * 进入设置界面
     * @param view
     */
    public void enterSetting(View view) {
        Intent intent = new Intent(this,TaskSettingsActivity.class);
        //startActivity(intent);
        startActivityForResult(intent,0);
    }

    /**
     * 杀死进程
     * @param view
     */
    public void killAll(View view) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<TaskInfo> romovedTaskInfos = new ArrayList<>();
        for (TaskInfo info : allTaskInfos) {
            if (info.isChecked()) {
                am.killBackgroundProcesses(info.getPackagename());
                if (info.isUserTask()) {
                    userTaskInfos.remove(info);
                } else {
                    sysTasKInfos.remove(info);
                }
                romovedTaskInfos.add(info);
                ruuningProcessCount--;
                availMem += info.getMemsize();
            }
        }
        allTaskInfos.removeAll(romovedTaskInfos);
        tv_process_count.setText("运行中的进程：" + ruuningProcessCount);
        tv_mem_info.setText("剩余/总内存："+ Formatter.formatFileSize(this, availMem) + "/" + Formatter.formatFileSize(this,totalMem));
        mAdapter.notifyDataSetChanged();
        //fillData();
    }

    /**
     * 反选
     * @param view
     */
    public void selectOppo(View view) {
        for (TaskInfo info : allTaskInfos) {
            if(info.getPackagename().equals("com.wenjie.mobilesafe")) {
                continue;
            }
            info.setChecked(!info.isChecked());
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 全选
     * @param view
     */
    public void selectAll(View view) {
        for (TaskInfo info : allTaskInfos) {
            if(info.getPackagename().equals("com.wenjie.mobilesafe")) {
                continue;
            }
            info.setChecked(true);
        }
        mAdapter.notifyDataSetChanged();
    }


    private class TaskManagerAdapter  extends BaseAdapter{

        @Override
        public int getCount() {
            SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
            boolean showSystem = sp.getBoolean("showSystem", false);
            if (showSystem) {
                return userTaskInfos.size() + 1 + sysTasKInfos.size() + 1;
            } else {
                return userTaskInfos.size() + 1;
            }

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
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskInfo taskInfo;
            View view;
            ViewHolder holder;
            if (position == 0) { //用户进程的标签
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextSize(18);
                tv.setText("用户进程:" + userTaskInfos.size() + "个");
                return tv;
            } else if (position == userTaskInfos.size() + 1) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextSize(18);
                tv.setText("系统进程:" + sysTasKInfos.size() + "个");
                return tv;
            } else if (position < userTaskInfos.size() + 1) {
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                taskInfo = sysTasKInfos.get(position -1 - userTaskInfos.size() - 1);
            }

            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                holder = new ViewHolder();
                view= View.inflate(getApplicationContext(),R.layout.list_item_taskinfo,null);
                holder.iv_task_icon = view.findViewById(R.id.iv_task_icon);
                holder.tv_task_name = view.findViewById(R.id.tv_task_name);
                holder.tv_task_memsize = view.findViewById(R.id.tv_task_memsize);
                holder.cb_status = view.findViewById(R.id.cb_status);
                view.setTag(holder);
            }

            holder.iv_task_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_task_name.setText(taskInfo.getName());
            holder.tv_task_memsize.setText("占用内存："+ Formatter.formatFileSize(getApplicationContext(),taskInfo.getMemsize()));
            holder.cb_status.setChecked(taskInfo.isChecked());
            if(taskInfo.getPackagename().equals("com.wenjie.mobilesafe")) {
                holder.cb_status.setVisibility(View.INVISIBLE);
            } else {
                holder.cb_status.setVisibility(View.VISIBLE);
            }
            return view;
        }

    }
    static class ViewHolder {
        ImageView iv_task_icon;
        TextView tv_task_name;
        TextView  tv_task_memsize;
        CheckBox cb_status;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter.notifyDataSetChanged();
    }
}