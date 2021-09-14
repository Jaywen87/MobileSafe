package com.wenjie.mobilesafe;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.icu.number.FormattedNumber;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wenjie.mobilesafe.domain.AppInfo;
import com.wenjie.mobilesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity {

    private final String TAG = "AppManagerActivity";
    private ListView lv_app_manager;
    private LinearLayout ll_loading;

    /**
     * 所有应用程序的信息
     */
    private List<AppInfo> appInfos;

    /**
     * 用户自己安装的应用程序信息
     */
    private List<AppInfo> userAppInfos;
    /**
     * 系统应用程序信息
     */
    private List<AppInfo> sysAppInfos;
    private TextView tv_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        //显示内外部可用空间大小
        final TextView tv_avail_rom = findViewById(R.id.tv_avail_rom);
        TextView tv_avail_sd = findViewById(R.id.tv_avail_sd);
        long romsize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());
        long sdsize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        tv_avail_rom.setText("内存可用空间：" + Formatter.formatFileSize(this,romsize));
        tv_avail_sd.setText("外部存储可用空间："+ Formatter.formatFileSize(this,sdsize));

        tv_status = findViewById(R.id.tv_status);

        //
        ll_loading = findViewById(R.id.ll_loading);
        lv_app_manager = findViewById(R.id.lv_app_manager);

        ll_loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
                userAppInfos = new ArrayList<AppInfo>();
                sysAppInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo : AppManagerActivity.this.appInfos){
                    if(appInfo.isUserApp()) {
                        userAppInfos.add(appInfo);
                    } else {
                        sysAppInfos.add(appInfo);
                    }
                }
                //加载listview的数据适配器
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppManagerAdapter adapter = new AppManagerAdapter();
                        lv_app_manager.setAdapter(adapter);
                        ll_loading.setVisibility(View.INVISIBLE);
                        tv_status.setText("用户应用：" + userAppInfos.size() + "个");
                    }
                });

            }
        }).start();

        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userAppInfos != null) {
                    if(firstVisibleItem > userAppInfos.size()) {
                        tv_status.setText("系统应用：" + sysAppInfos.size() + "个");
                    } else {
                        tv_status.setText("用户应用：" + userAppInfos.size() + "个");
                    }
                }
            }
        });

    }

    class  AppManagerAdapter extends BaseAdapter {

        private String tag;

        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + sysAppInfos.size() + 1;
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
            View view;
            ViewHolder viewHolder;
            AppInfo appInfo;

            if(position == 0) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextSize(18);
                tv.setText("用户应用:" + userAppInfos.size() + "个");
                return tv;
            } else if (position == userAppInfos.size() + 1 ) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextSize(18);
                tv.setText("系统应用:" + sysAppInfos.size() + "个");
                return tv;
            }

            if (position <= userAppInfos.size()) {
                appInfo = userAppInfos.get(position-1);
            } else {
                int newposition = position - userAppInfos.size() -1 - 1;
                appInfo = sysAppInfos.get(newposition);
            }

            if(convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            } else {
                viewHolder = new ViewHolder();
                view = View.inflate(AppManagerActivity.this, R.layout.list_item_appinfo,null);
                viewHolder.iv_app_icon = view.findViewById(R.id.iv_app_icon);
                viewHolder.tv_app_name = view.findViewById(R.id.tv_app_name);
                viewHolder.tv_app_location = view.findViewById(R.id.tv_app_location);
                view.setTag(viewHolder);
            }
            viewHolder.iv_app_icon.setImageDrawable(appInfo.getIcon());
            viewHolder.tv_app_name.setText(appInfo.getName());
            viewHolder.tv_app_location.setText(appInfo.isInRom()?"手里内存":"外部存储");
            return view;
        }


    }

    static class ViewHolder {
        ImageView iv_app_icon;
        TextView tv_app_name;
        TextView tv_app_location;
    }

    private long getAvailSpace(String path){
        StatFs statFs = new StatFs(path);
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
        return  availableBlocksLong * blockSizeLong ;
    }
}