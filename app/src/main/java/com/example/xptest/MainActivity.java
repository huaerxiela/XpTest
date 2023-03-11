package com.example.xptest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText setPhone, setWeb, setProxy;
    private Button btnUpdate;
    private SharedPreferences sharedPreferences;
    private TextView getPhone, getWeb, getProxy, getIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        showView();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = setPhone.getText().toString().trim();
                String web = setWeb.getText().toString().trim();
                String proxy = setProxy.getText().toString().trim();

                String ip = "";
                SharedPreferences.Editor edt = sharedPreferences.edit();
                edt.putString("phone", phone);
                edt.putString("web", web);
                edt.putString("proxy", proxy);
                edt.putString("ip", ip);
                edt.apply();
                showView();
                Toast.makeText(MainActivity.this, "更新配置成功", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void showView() {
        setPhone.setText(sharedPreferences.getString("phone", ""));
        setWeb.setText(sharedPreferences.getString("web", "xxxxx:5620"));
        setProxy.setText(sharedPreferences.getString("proxy", ""));

        getPhone.setText(sharedPreferences.getString("phone", ""));
        getWeb.setText(sharedPreferences.getString("web", "xxxxx:5620"));
        getProxy.setText(sharedPreferences.getString("proxy", ""));
        getIp.setText(sharedPreferences.getString("ip", ""));
    }

    @SuppressLint("WorldReadableFiles")
    private void initView() {
        setPhone = findViewById(R.id.set_phone);
        setWeb = findViewById(R.id.set_web);
        setProxy = findViewById(R.id.set_proxy);
        btnUpdate = findViewById(R.id.btn_update);
        //  ip相关
        sharedPreferences = getSharedPreferences("TestSetting", MODE_WORLD_READABLE);
        SharedPreferences.Editor edt = sharedPreferences.edit();
        String ip = "";
        edt.putString("ip", ip);
        edt.apply();

        getPhone = findViewById(R.id.get_phone);
        getWeb = findViewById(R.id.get_web);
        getProxy = findViewById(R.id.get_proxy);
        getIp = findViewById(R.id.get_ip);

    }
}