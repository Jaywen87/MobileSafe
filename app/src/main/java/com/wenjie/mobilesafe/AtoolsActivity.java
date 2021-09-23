package com.wenjie.mobilesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wenjie.mobilesafe.utils.SmsUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class AtoolsActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 点击事件，进入号码归属地查询
     * @param view
     */
    public void numberQuery(View view) {
        Intent intent = new Intent(AtoolsActivity.this, NumberAddressQueryActivity.class);
        startActivity(intent);
    }

    /**
     * 点击事件，短信备份
     * @param view
     */
    public void smsBackup(View view) {
        progressDialog = new ProgressDialog(AtoolsActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在备份短信");
        progressDialog.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    SmsUtils.backupSms(AtoolsActivity.this, new SmsUtils.BackupCallBack() {
                        @Override
                        public void beforeBackup(int max) {
                            progressDialog.setMax(max);
                        }

                        @Override
                        public void onSmsBackup(int progress) {
                            progressDialog.setProgress(progress);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this,"备份成功",Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this,"备份失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    progressDialog.dismiss();
                }
            }
        }.start();

    }

    /**
     * 点击事件，短信还原
     * @param view
     */
    public void smsRestore(View view) {
        progressDialog = null;
        progressDialog = new ProgressDialog(AtoolsActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("短信正在还原");
        progressDialog.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    SmsUtils.restoreSms(AtoolsActivity.this, false, new SmsUtils.RestoreCallBack() {
                        @Override
                        public void beforeRestore(int max) {
                            progressDialog.setMax(max);
                        }

                        @Override
                        public void onSmsRestore(int progress) {
                            progressDialog.setProgress(progress);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this,"短信还原成功",Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this,"短信失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    progressDialog.dismiss();
                }
            }
        }.start();
    }

}