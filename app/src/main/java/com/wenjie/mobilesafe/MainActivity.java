package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity {

    private static String [] names = {"手机防盗","通讯卫士","软件管理",
                                      "进程管理","流量统计","手机杀毒",
                                      "缓存清理","高级工具","设置中心"};
    private static int[] ids = {
            R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
            R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
            R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gv_home_gridview = findViewById(R.id.gv_home_gridview);
        MyAdapter myAdapter = new MyAdapter();
        gv_home_gridview.setAdapter(myAdapter);
        gv_home_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 8:
                        Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                        startActivity(intent);
                }
            }
        });




    }

    private class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return names.length;
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

            View view = inflate(MainActivity.this,R.layout.list_item_home,null);
            ImageView iv_item = view.findViewById(R.id.iv_item);
            iv_item.setImageResource(ids[position]);

            TextView tv_item = view.findViewById(R.id.tv_item);
            tv_item.setText(names[position]);

            return view;
        }
    }
}