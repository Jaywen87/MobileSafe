package com.wenjie.mobilesafe;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.wenjie.mobilesafe.utils.StreamTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final int ENTER_HOME = 0;
    private static final int SHOW_UDPDATE_DIALOG = 1;
    private static final int URL_ERROR = 2;
    private static final int NETWORK_ERROR = 3;
    private static final int JSON_ERROR = 4;
    private Handler handler;
    private String description;
    private String apkurl;
    private TextView tv_update_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);

        TextView tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText(getString(R.string.app_version) + getVersionName());

        tv_update_info = findViewById(R.id.tv_update_info);

        // ??????????????????????????????
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f,1.0f);
        alphaAnimation.setDuration(1000);
        findViewById(R.id.cl_splash_view).startAnimation(alphaAnimation);

        // ????????????
        copyDB();

        //????????????????????????
        //installShortCut();
        if(sp.getBoolean("update",false))
        {
            checkUpdate();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    enterHome();
                }
            }).start();
        }


        handler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch(msg.what) {
                    case ENTER_HOME:
                        enterHome();
                        break;

                    case SHOW_UDPDATE_DIALOG:
                        Log.i(TAG, "handleMessage: ????????????????????????");
                        showUpdateDialog();
                        break;

                    case URL_ERROR:
                        Toast.makeText(SplashActivity.this,"url ??????",Toast.LENGTH_SHORT).show();
                        enterHome();
                        break;

                    case NETWORK_ERROR:
                        Toast.makeText(getApplicationContext(),"????????????",Toast.LENGTH_SHORT).show();
                        enterHome();
                        break;

                    case JSON_ERROR:
                        Toast.makeText(SplashActivity.this,"JSON????????????",Toast.LENGTH_SHORT).show();
                        enterHome();
                        break;

                    default:
                        break;
                }
            }
        };

    }

    /**
     * ??????????????????
     */
    private void installShortCut() {
        //???????????? ?????????3??????????????????1.?????????2.?????? 3.????????????

        //?????????????????????????????????
        Intent shortIntent = new Intent();
        shortIntent.setAction("android.intent.action.MAIN");//android.intent.action.MAIN
        shortIntent.addCategory("android.intent.category.LAUNCHER");//android.intent.category.LAUNCHER
        shortIntent.setClassName(getPackageName(),"com.wenjie.mobilesafe.SplashActivity");

        //??????????????????
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "????????????");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(this,R.mipmap.ic_launcher) );
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortIntent);
        sendBroadcast(intent);
    }

    /**
     * path ???address.db????????????????????????data/data/????????????/files/address.db
     */
    private void copyDB() {
        try {
            File file = new File(getFilesDir(),"address.db");
            if (file.exists() && file.length() > 0) {
                Log.i(TAG, "copyDB: ?????????????????????????????????");
            } else {
                InputStream is  = getAssets().open("address.db");
                FileOutputStream fos = new FileOutputStream(file);
                byte [] buffer  = new  byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer,0,len);
                }
                is.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("????????????");
        builder.setMessage(description);
        builder.setCancelable(false);
        builder.setNegativeButton("????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                enterHome();
            }
        });
        builder.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    FinalHttp finalHttp = new FinalHttp();
                    finalHttp.download(apkurl, Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobilesafe2.0.apk", new AjaxCallBack<File>() {
                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);
                            int  progrees = (int) (current * 100 / count);
                            tv_update_info.setText("???????????????"+ progrees + "%");

                        }

                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);
                            installAPK(file);
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            t.printStackTrace();
                            Toast.makeText(SplashActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                            super.onFailure(t, errorNo, strMsg);
                        }

                        private void installAPK(File file) {
                            enterHome();
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
                            startActivity(intent);
                        }
                    });


                }


            }
        });
        builder.show();
    }

//    private static class MyHandler extends Handler{
//       WeakReference<Activity> activityWeakReference;
//        MyHandler(Activity activity) {
//            activityWeakReference = new WeakReference<Activity>(activity);
//        }
//    }

    private void enterHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //??????????????????,????????????home???????????????????????????????????????????????????
        finish();
    }


    private void checkUpdate() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message message = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    URL url = new URL(getString(R.string.serverurl));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(4000);
                    int code = conn.getResponseCode();
                    if(code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamTools.readFromStream(is);
                        Log.i(TAG, "run: ???????????????" +  result);
                        // json??????
                        JSONObject jsonObject = new JSONObject(result);
                        String version = jsonObject.getString("version");
                        description = jsonObject.getString("description");
                        apkurl = jsonObject.getString("apkurl");
                        if(getVersionName().equals(version)){
                            message.what = ENTER_HOME;
                        } else {
                            message.what = SHOW_UDPDATE_DIALOG;
                        }
                    }

                } catch (MalformedURLException e) {
                    message.what = URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    message.what = NETWORK_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    message.what = JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    long dTime = endTime - startTime;
                    handler.sendMessageDelayed(message, dTime < 1500 ? 1500 - dTime : 0);
                }
            }
        }).start();
    }

    /**
     * ?????????????????????????????????
     */
    private String  getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return  packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }


}
