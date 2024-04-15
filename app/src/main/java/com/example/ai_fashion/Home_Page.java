package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.DataBase.DataBase;

public class Home_Page extends AppCompatActivity {

    Button register;
    Button log_in;
    EditText mEditTextUsername;
    EditText mEditTextPassword;
    DataBase DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        register=(Button)findViewById(R.id.register_button);
        log_in=(Button)findViewById(R.id.login_button);
        mEditTextUsername = findViewById(R.id.input_user_name);
        mEditTextPassword = findViewById(R.id.input_user_password);
        DB = Room.databaseBuilder(this, DataBase.class, "DataBase")
                // 默认不允许在主线程中连接数据库   强制在主线程中处理
                .allowMainThreadQueries()
                .build();
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
                String username = mEditTextUsername.getText().toString();
                String password = mEditTextPassword.getText().toString();
                //设置登录逻辑
                if (DB.userDao().findUser(username, password) == null) {
                    //在页面输出错误信息
                    mEditTextUsername.setError("用户名或密码错误");
                }
                else {
                    Intent intent=new Intent(Home_Page.this, Welcome_Page.class);//设置切换对应activity
                    startActivity(intent);//开始切换
                }
            }
        });
    }
}