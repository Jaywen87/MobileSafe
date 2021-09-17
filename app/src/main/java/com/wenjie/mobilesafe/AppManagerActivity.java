package com.wenjie.mobilesafe;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.number.FormattedNumber;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wenjie.mobilesafe.domain.AppInfo;
import com.wenjie.mobilesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

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
    /**
     * 弹出窗体
     */
    private PopupWindow popupWindow;

    /**
     * 应用程序信息
     */
    private AppInfo appInfo;
    private AppManagerAdapter mAdapter;
    private TextView tv_avail_rom;
    private TextView tv_avail_sd;
    //android.os.FileObserver
    //FileObserver


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        //显示内外部可用空间大小
        tv_avail_rom = findViewById(R.id.tv_avail_rom);
        tv_avail_sd = findViewById(R.id.tv_avail_sd);

        tv_status = findViewById(R.id.tv_status);

        //
        ll_loading = findViewById(R.id.ll_loading);
        lv_app_manager = findViewById(R.id.lv_app_manager);

        ll_loading.setVisibility(View.VISIBLE);

        //填充数据
        fillData();

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
                dismissPopupWindow();
            }
        });

        /**
         * 设置listview的点击事件
         */
        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    return;
                } else if (position == userAppInfos.size() + 1) {
                    return;
                } else if (position <= userAppInfos.size()) {
                    int newPosition = position - 1;
                    appInfo = userAppInfos.get(newPosition);
                } else {
                    int newPosition = position - 1 - userAppInfos.size() -1;
                    appInfo = sysAppInfos.get(newPosition);
                }

                //首先取消弹出窗口
                dismissPopupWindow();

                View contentView = View.inflate(getApplicationContext(), R.layout.popup_app_item, null);
                LinearLayout ll_start = contentView.findViewById(R.id.ll_start);
                LinearLayout ll_uninstall = contentView.findViewById(R.id.ll_uninstall);
                LinearLayout ll_share = contentView.findViewById(R.id.ll_share);

                ll_start.setOnClickListener(AppManagerActivity.this);
                ll_uninstall.setOnClickListener(AppManagerActivity.this);
                ll_share.setOnClickListener(AppManagerActivity.this);

                popupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                int[] outLocation = new int[2];
                view.getLocationInWindow(outLocation);
                popupWindow.showAtLocation(parent,Gravity.LEFT | Gravity.TOP,outLocation[0],outLocation[1]);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
                alphaAnimation.setDuration(500);
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.3f,1.0f,0.3f,1.0f, Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(500);
                AnimationSet animationSet = new AnimationSet(false);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(scaleAnimation);
                contentView.startAnimation(animationSet);

            }
        });

    }

    /**
     * 填充数据
     */
    private void fillData() {
        //打开app管理界面和卸载应用程序之后需重新获取剩余存储空间
        long romsize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());
        long sdsize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        tv_avail_rom.setText("内存可用空间：" + Formatter.formatFileSize(AppManagerActivity.this, romsize));
        tv_avail_sd.setText("外部存储可用空间："+ Formatter.formatFileSize(AppManagerActivity.this, sdsize));

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
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mAdapter = new AppManagerAdapter();
                            lv_app_manager.setAdapter(mAdapter);
                            tv_status.setText("用户应用：" + userAppInfos.size() + "个");
                        }
                        ll_loading.setVisibility(View.INVISIBLE);
                    }
                });

            }
        }).start();
    }

    private void dismissPopupWindow() {
        if(popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    public void onClick(View v) {
        dismissPopupWindow();
        switch (v.getId()) {
            case R.id.ll_uninstall:
                if (appInfo.isUserApp()) {
                    uninstallApplication();
                } else {
                    Toast.makeText(AppManagerActivity.this,"系统应用无法卸载",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_start:
                startApplication();
                break;
            case R.id.ll_share:
                shareApplication();
                break;
            default:
                break;
        }

    }

    private void shareApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"推荐你使用一款好用的应用名叫："+ appInfo.getName());
        startActivity(intent);
    }

    private void uninstallApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:"+ appInfo.getPacakagename()));
        startActivityForResult(intent,0);
    }

    private void startApplication() {
        //此种方法 有些是已经启动了和 有些是不能启动的Activity，就不能在启动，就会报错，
       /* Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(appInfo.getPacakagename());
        startActivity(intent);*/
        PackageManager pm  = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(appInfo.getPacakagename());
        if (intent != null) {
            //Log.i(TAG, "startApplication:  Action：" + intent.getAction() + "Type：" + intent.getType() + "Categories" + intent.getCategories().toString());
            startActivity(intent);
        } else {
            Toast.makeText(AppManagerActivity.this, "此应用无法启动", Toast.LENGTH_SHORT).show();
        }
    }

    class  AppManagerAdapter extends BaseAdapter {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissPopupWindow();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fillData();
    }
}