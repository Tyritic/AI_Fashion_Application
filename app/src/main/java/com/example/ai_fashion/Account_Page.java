package com.example.ai_fashion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Insert;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;

public class Account_Page extends AppCompatActivity {
    ImageButton backTohomePage;
    EditText account_input;
    EditText password_input;
    EditText birthday_input;
    EditText nickname_input;
    String user_account;
    String user_password;

    String account;
    String password;
    String birthday;
    String nickname;

    String newAccount;
    String newPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_page);
        //获取用户账号和密码，通过上一个页面传递过来的数据
        Intent intent1 = getIntent();
        user_account = intent1.getStringExtra("user_account");
        user_password = intent1.getStringExtra("user_password");
        if(user_account==null||user_password==null)
        {
            Toast.makeText(Account_Page.this,"Account_Page接收失败",Toast.LENGTH_SHORT).show();
        }
        else if(user_account!=null&&user_password!=null)
        {
            //Toast.makeText(Account_Page.this,"Account_Fragment接收成功",Toast.LENGTH_SHORT).show();
        }
        AppDatabase DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        User user = DB.userDao().findUser(user_account,user_password);
        account_input = findViewById(R.id.account_input);
        password_input = findViewById(R.id.password_input);
        birthday_input = findViewById(R.id.birthday_input);
        nickname_input = findViewById(R.id.nickname_input);
        //设置输入框的默认值为用户的信息
        account_input.setText(user.getUser_account());
        password_input.setText(user.getUser_password());
        birthday_input.setText(user.getUser_age());
        nickname_input.setText(user.getUser_nickname());
        Button modify = findViewById(R.id.modify_button);
        //修改按钮点击事件
        modify.setOnClickListener(v -> {
            //获取用户输入的信息
            account = account_input.getText().toString();
            password = password_input.getText().toString();
            birthday = birthday_input.getText().toString();
            nickname= nickname_input.getText().toString();
            //判断用户是否修改了信息
            boolean not_modified=account.equals(user_account)&&password.equals(user_password)&&birthday.equals(user.getUser_age())&&nickname.equals(user.getUser_nickname());
            if(not_modified)
            {
                Toast.makeText(Account_Page.this,"未修改",Toast.LENGTH_SHORT).show();
            }
            else if(!account.equals(user_account)&&DB.userDao().findUserByUseraccount(account)!=null)
            {
                Toast.makeText(Account_Page.this,"用户名已存在",Toast.LENGTH_SHORT).show();
            }
            else
            {
                User user1 = new User();
                user1.setUser_account(account);
                user1.setUser_password(password);
                user1.setUser_birthday(birthday);
                user1.setUser_nickname(nickname);
                user1.setUser_id(user.getUser_id());
                user1.setUser_gender(user.getUser_gender());
                DB.userDao().updateUser(user1);
                Toast.makeText(Account_Page.this,"修改成功",Toast.LENGTH_SHORT).show();
            }
        });
        backTohomePage = findViewById(R.id.back_to_home_page);
        //返回按钮点击事件
        backTohomePage.setOnClickListener(v -> {
                //获取用户退出后的账号和密码
                newAccount=account_input.getText().toString();
                newPassword=account_input.getText().toString();
                //将用户账号和密码包装成传递
                Bundle bundle=new Bundle();
                bundle.putString("user_account",newAccount);
                bundle.putString("user_password",newPassword);
                if(newPassword==null||newAccount==null)
                {
                    Toast.makeText(Account_Page.this,"Account_Page发送失败",Toast.LENGTH_SHORT).show();
                }
                else if(newPassword!=null&&newAccount!=null)
                {
                    //Toast.makeText(Account_Page.this,"Account_Page发送成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(this, Home_Page.class);
                    intent.putExtra("fragment_flag", 2);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            });

    }

}