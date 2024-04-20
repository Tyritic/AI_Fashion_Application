package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Insert;

public class Account_Page extends AppCompatActivity {
    ImageButton backTohomePage;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_page);
        backTohomePage = findViewById(R.id.back_to_home_page);
        backTohomePage.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setClass(this, Home_Page.class);
                intent.putExtra("fragment_flag", 2);
                startActivity(intent);
            });
    }
}