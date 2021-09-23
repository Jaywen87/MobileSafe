package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wenjie.mobilesafe.utils.MD5Utils;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity {

    private static String [] names = {"手机防盗","通讯卫士","应用管理",
                                      "进程管理","流量统计","手机杀毒",
                                      "缓存清理","高级工具","设置中心"};
    private static int[] ids = {
            R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
            R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
            R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
    };

    private SharedPreferences sp;
    private EditText et_setup_password;
    private EditText et_setup_confirm;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        GridView gv_home_gridview = findViewById(R.id.gv_home_gridview);
        MyAdapter myAdapter = new MyAdapter();
        gv_home_gridview.setAdapter(myAdapter);
        gv_home_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0://进入防盗追踪
                        showLostFindDialog();
                        break;
                    case 1://进入通讯卫士 黑名单拦截界面
                        intent = new Intent(MainActivity.this, CallSmsSafeActivity.class);
                        startActivity(intent);
                        break;
                    case 2://进入应用管理
                        intent = new Intent(MainActivity.this, AppManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 3://进入进程管理
                        intent = new Intent(MainActivity.this, TaskManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 7://进入高级工具
                        intent = new Intent(MainActivity.this,AtoolsActivity.class);
                        startActivity(intent);
                        break;
                    case 8://进入设置中心
                        intent = new Intent(MainActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });




    }

    private void showLostFindDialog() {
        //判断是否设置过密码
        if(isSetupPwd()){
            //已经设置密码了，弹出的是输入对话框
            showEnterDialog();
        }else{
            //没有设置密码，弹出的是设置密码对话框
            showSetupPwdDialog();
        }

    }

    private void showEnterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this, R.layout.dialog_enter_password,null);
        et_setup_password = view.findViewById(R.id.et_setup_password);
        Button ok_btn = view.findViewById(R.id.ok_btn);
        Button cancer_btn = view.findViewById(R.id.cancer_btn);
        ok_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String password2 = sp.getString("password","");
                String password = et_setup_password.getText().toString().trim();
                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this,"密码为空",Toast.LENGTH_SHORT).show();
                    return;
                } else if (password2.equals(MD5Utils.md5Password(password))) {
                    Intent intent = new Intent(MainActivity.this, LostFindActivity.class);
                    alertDialog.dismiss();
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this,"密码错误！",Toast.LENGTH_SHORT).show();
                    et_setup_password.setText("");
                    return;
                }

            }
        });
        cancer_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.show();


    }

    private void showSetupPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this,R.layout.dialog_setup_password,null);
        et_setup_password = view.findViewById(R.id.et_setup_password);
        et_setup_confirm = view.findViewById(R.id.et_setup_confirm);
        Button ok_btn = view.findViewById(R.id.ok_btn);
        Button cancer_btn = view.findViewById(R.id.cancer_btn);
        ok_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_setup_password.getText().toString().trim();
                String confirm_password = et_setup_confirm.getText().toString().trim();

                if(TextUtils.isEmpty(password) | TextUtils.isEmpty(confirm_password)) {
                    Toast.makeText(MainActivity.this,"密码为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.equals(confirm_password)) {
                    SharedPreferences.Editor edit = sp.edit();

                    edit.putString("password",MD5Utils.md5Password(password));
                    edit.apply();
                    alertDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, LostFindActivity.class);
                    startActivity(intent);
                } else  {
                    Toast.makeText(MainActivity.this,"密码不一致",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        cancer_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.show();

    }

    public Boolean isSetupPwd() {
        String  password = sp.getString("password",null);
        return !TextUtils.isEmpty(password);
        //return !password.isEmpty();
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