package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Welcome_Page extends AppCompatActivity {

    Handler mhandler=new Handler();//创建一个Handler对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);
        //延时1秒后跳转到登录页面
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Welcome_Page.this, Log_in_Page.class);
                startActivity(intent);
            }
        },1000);
    };
}