package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomePage extends AppCompatActivity {

    Handler mhandler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(WelcomePage.this,HomePage.class);
                startActivity(intent);
            }
        },1000);
    };
}