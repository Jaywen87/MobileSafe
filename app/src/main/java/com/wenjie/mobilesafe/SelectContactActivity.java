package com.wenjie.mobilesafe;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class SelectContactActivity extends AppCompatActivity {

    private List<Map<String, String>> data;
    private String TAG = "SelectContactActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        ListView list_select_contact = (ListView)findViewById(R.id.list_contact_select);
        data = getContactInfo();
        list_select_contact.setAdapter(new SimpleAdapter(this, data,R.layout.contact_item_view,new String [] {"name","phone"},new int[] {R.id.tv_name,R.id.tv_phone}));
        list_select_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = data.get(position).get("phone");
                Intent data = new Intent();
                data.putExtra("phone",phone);
                setResult(0,data);
                finish();
            }
        });
    }

    private List<Map<String, String>> getContactInfo() {
        //创建一个ArrayList实例
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        //得到一个内容解析器
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = contentResolver.query(uri, new String [] {"contact_id"}, null, null, null);
        while(cursor.moveToNext()){
            String contact_id = cursor.getString(0);
            if(contact_id != null) {
                Map<String, String> map = new HashMap<>();
                Cursor dataCursor = contentResolver.query(uriData, new String[]{"mimetype", "data1"}, "contact_id=?", new String[]{contact_id}, null);
                while (dataCursor.moveToNext()){

                    String data1 = dataCursor.getString(1);
                    String minetype = dataCursor.getString(0);
                    if("vnd.android.cursor.item/name".equals(minetype)) {
                        map.put("name",data1);
                    } else if ("vnd.android.cursor.item/phone_v2".equals(minetype)) {
                        map.put("phone",data1);
                    }
                }
                list.add(map);
                dataCursor.close();

            }
        }
        cursor.close();
        return list;
    }

}