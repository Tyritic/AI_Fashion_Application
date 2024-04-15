package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.DB.AppDatabase;
public class Home_Page extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        Button register = findViewById(R.id.register_button);
        Button login = findViewById(R.id.login_button);
        EditText account = findViewById(R.id.input_user_account);
        EditText password = findViewById(R.id.input_user_password);
        AppDatabase DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        register.setOnClickListener(v -> {
            Intent intent = new Intent(Home_Page.this, Register_Page.class);
            startActivity(intent);
        });
        login.setOnClickListener(v -> {
            String user_account = account.getText().toString();
            String user_password = password.getText().toString();
            if(DB.userDao().findUser(user_account,user_password)!=null)
            {
                Toast.makeText(Home_Page.this,"登录成功",Toast.LENGTH_SHORT).show();
                new Thread(() -> {
                    Intent intent = new Intent(Home_Page.this, Wardrobe_Page.class);
                    startActivity(intent);
                }).start();
            }
            else if(DB.userDao().findUserByUsername(user_account)==null)
            {
                Toast.makeText(Home_Page.this,"用户不存在",Toast.LENGTH_SHORT).show();
            }
            else if(DB.userDao().findUser(user_account,user_password)==null&&DB.userDao().findUserByUsername(user_account)!=null)
            {
                Toast.makeText(Home_Page.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
