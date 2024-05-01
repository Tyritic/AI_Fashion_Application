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
    //请求定位权限
    public static final int REQUEST_LOCATION_PERMISSION = 5555;
    //用户账号和密码
    String user_account;
    String user_password;
    //初始化组件
    Button button_register;
    Button botton_login;
    EditText mEditTextAccount;
    EditText mEditTextPassword;
    AppDatabase DB;//数据库
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_page);
        
        // 检查是否具有定位权限
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求定位权限
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_PHONE_STATE}, REQUEST_LOCATION_PERMISSION);
        }
        
        //获取用户账号和密码，通过上一个页面传递过来的数据
        Intent intent1 = getIntent();
        user_account = intent1.getStringExtra("user_account");
        user_password = intent1.getStringExtra("user_password");
        
        //初始化组件
        button_register = findViewById(R.id.register_button);
        botton_login = findViewById(R.id.login_button);
        mEditTextAccount = findViewById(R.id.input_user_account);
        mEditTextPassword = findViewById(R.id.input_user_password);
        if(user_account!=null&&user_password!=null) {
            mEditTextAccount.setText(user_account);
            mEditTextPassword.setText(user_password);
        }

       //创建数据库
        DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();//允许在主线程中查询数据库
        
        //注册按钮的点击事件
        button_register.setOnClickListener(v -> {
            //跳转到注册页面
            Intent intent = new Intent(Log_in_Page.this, Register_Page.class);
            startActivity(intent);
        });
        
        //登录按钮的点击事件
        botton_login.setOnClickListener(v -> {
            //获取用户输入的账号和密码
            String user_account = mEditTextAccount.getText().toString();
            String user_password = mEditTextPassword.getText().toString();
            User user = DB.userDao().findUser(user_account,user_password);
            if(user!=null)
            {
                //登录成功，输出提示信息
                Toast.makeText(Log_in_Page.this,"欢迎用户"+user.getUser_nickname()+"回来",Toast.LENGTH_SHORT).show();
                //跳转到主页面
                new Thread(() -> {
                    //建立一个Intent对象，用于启动Home_Page,并传递用户账号和密码
                    Intent intent = new Intent(Log_in_Page.this, Home_Page.class);
                    intent.putExtra("user_account",user_account);
                    intent.putExtra("user_password",user_password);
                    startActivity(intent);
                }).start();
            }
            //登录失败，输出提示信息
            else if(DB.userDao().findUserByUseraccount(user_account)==null)//用户不存在
            {
                Toast.makeText(Log_in_Page.this,"用户不存在",Toast.LENGTH_SHORT).show();
            }
            //用户存在但是密码错误
            else if(DB.userDao().findUser(user_account,user_password)==null&&DB.userDao().findUserByUseraccount(user_account)!=null)
            {
                Toast.makeText(Log_in_Page.this,"密码错误",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
