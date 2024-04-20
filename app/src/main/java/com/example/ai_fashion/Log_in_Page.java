package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.DB.AppDatabase;
public class Log_in_Page extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_page);
        Button register = findViewById(R.id.register_button);
        Button login = findViewById(R.id.login_button);
        EditText account = findViewById(R.id.input_user_account);
        EditText password = findViewById(R.id.input_user_password);
        AppDatabase DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        register.setOnClickListener(v -> {
            Intent intent = new Intent(Log_in_Page.this, Register_Page.class);
            startActivity(intent);
        });
        login.setOnClickListener(v -> {
            String user_account = account.getText().toString();
            String user_password = password.getText().toString();
            if(DB.userDao().findUser(user_account,user_password)!=null)
            {
                Toast.makeText(Log_in_Page.this,"登录成功",Toast.LENGTH_SHORT).show();
                new Thread(() -> {
                    Intent intent = new Intent(Log_in_Page.this, Home_Page.class);
                    intent.putExtra("user_account",user_account);
                    intent.putExtra("user_password",user_password);
                    startActivity(intent);
                }).start();
            }
            else if(DB.userDao().findUserByUsername(user_account)==null)
            {
                Toast.makeText(Log_in_Page.this,"用户不存在",Toast.LENGTH_SHORT).show();
            }
            else if(DB.userDao().findUser(user_account,user_password)==null&&DB.userDao().findUserByUsername(user_account)!=null)
            {
                Toast.makeText(Log_in_Page.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
