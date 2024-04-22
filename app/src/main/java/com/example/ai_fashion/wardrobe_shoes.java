package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class wardrobe_shoes extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wardrobe_shoes);
        ImageButton backTohomePage = findViewById(R.id.shoes_back_to_home_page);
        backTohomePage.setOnClickListener(v -> {
            Intent intent=new Intent(wardrobe_shoes.this, Home_Page.class);//设置切换对应activity
            startActivity(intent);//开始切换
        });
    }
}
