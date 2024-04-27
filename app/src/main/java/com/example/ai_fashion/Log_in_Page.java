package com.example.ai_fashion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;

public class Log_in_Page extends AppCompatActivity {
    public static final int REQUEST_LOCATION_PERMISSION = 5555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_page);
        //初始化组件
        Button register = findViewById(R.id.register_button);
        Button login = findViewById(R.id.login_button);
        EditText account = findViewById(R.id.input_user_account);
        EditText password = findViewById(R.id.input_user_password);
        // 检查是否具有定位权限
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求定位权限
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_PHONE_STATE}, REQUEST_LOCATION_PERMISSION);
        }
        AppDatabase DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();//允许在主线程中查询数据库
        //注册按钮的点击事件
        register.setOnClickListener(v -> {
            //跳转到注册页面
            Intent intent = new Intent(Log_in_Page.this, Register_Page.class);
            startActivity(intent);
        });
        //登录按钮的点击事件
        login.setOnClickListener(v -> {
            //获取用户输入的账号和密码
            String user_account = account.getText().toString();
            String user_password = password.getText().toString();
            User user = DB.userDao().findUser(user_account,user_password);
            if(user!=null)
            {
                //登录成功，输出提示信息
                Toast.makeText(Log_in_Page.this,"欢迎用户"+user.getUser_nickname()+"回来",Toast.LENGTH_SHORT).show();
                //Toast.makeText(Log_in_Page.this,"登录成功",Toast.LENGTH_SHORT).show();
                //跳转到主页面
                new Thread(() -> {
                    Intent intent = new Intent(Log_in_Page.this, Home_Page.class);
                    intent.putExtra("user_account",user_account);
                    intent.putExtra("user_password",user_password);
                    startActivity(intent);
                }).start();
            }
            else if(DB.userDao().findUserByUseraccount(user_account)==null)
            {
                Toast.makeText(Log_in_Page.this,"用户不存在",Toast.LENGTH_SHORT).show();
            }
            else if(DB.userDao().findUser(user_account,user_password)==null&&DB.userDao().findUserByUseraccount(user_account)!=null)
            {
                Toast.makeText(Log_in_Page.this,"密码错误",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
