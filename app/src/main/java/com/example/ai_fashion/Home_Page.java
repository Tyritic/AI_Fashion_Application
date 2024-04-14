package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Home_Page extends AppCompatActivity {

    Button register;
    Button log_in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        register=(Button)findViewById(R.id.register_button);
        log_in=(Button)findViewById(R.id.login_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home_Page.this, Register_Page.class);//设置切换对应activity
                startActivity(intent);//开始切换
            }
        });
        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home_Page.this, Wardrobe_Page.class);//设置切换对应activity
                startActivity(intent);//开始切换
            }
        });
    }
}