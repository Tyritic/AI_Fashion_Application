package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        ImageButton backTohomePage = findViewById(R.id.backToHomePage);
        backTohomePage.setOnClickListener(v -> {
            Intent intent=new Intent(RegisterPage.this,HomePage.class);//设置切换对应activity
            startActivity(intent);//开始切换
        });
        Button back= findViewById(R.id.back_button);
        back.setOnClickListener(v -> {
            Intent intent=new Intent(RegisterPage.this,HomePage.class);//设置切换对应activity
            startActivity(intent);//开始切换
        });

    };
}