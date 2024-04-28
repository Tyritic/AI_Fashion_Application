package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Recommend_Page extends AppCompatActivity {
    private String user_account;
    private String user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recommend_page);
        Intent intent1 = getIntent();
        user_account = intent1.getStringExtra("user_account");
        user_password = intent1.getStringExtra("user_password");
        if(user_account==null||user_password==null)
        {
            Toast.makeText(Recommend_Page.this,"Recommend_Fragment接收失败",Toast.LENGTH_SHORT).show();
        }
        else if(user_account!=null&&user_password!=null)
        {
            //Toast.makeText(Recommend_Page.this,"Recommend_Fragment接收成功",Toast.LENGTH_SHORT).show();
        }
        ImageButton back_button = findViewById(R.id.back_to_home_page);
        back_button.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            Intent intent = new Intent();
            bundle.putString("user_account",user_account);
            bundle.putString("user_password",user_password);
            if(user_account==null||user_password==null)
            {
                Toast.makeText(Recommend_Page.this,"Recommend_Page发送失败",Toast.LENGTH_SHORT).show();
            }
            else if(user_account!=null&&user_password!=null)
            {
                //Toast.makeText(Recommend_Page.this,"Recommend_Page发送成功",Toast.LENGTH_SHORT).show();
            }
            intent.setClass(Recommend_Page.this, Home_Page.class);
            intent.putExtra("fragment_flag", 1);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
}