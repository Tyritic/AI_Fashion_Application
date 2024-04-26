package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;

import java.util.regex.Pattern;

public class Register_Page extends AppCompatActivity {
    EditText mEditTextAccount;
    EditText mEditTextPassword;
    EditText mEditTextNickname;
    EditText mEditTextAge;
    EditText mEditTextConfirmPassword;
    Button back;
    ImageButton backTohomePage;
    String user_gender;
    Button register;
    AppDatabase DB;
    RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        backTohomePage = findViewById(R.id.backToHomePage);
        mEditTextAccount = findViewById(R.id.account);
        mEditTextPassword = findViewById(R.id.password);
        mEditTextNickname = findViewById(R.id.nickname);
        mEditTextAge = findViewById(R.id.age);
        mEditTextConfirmPassword = findViewById(R.id.confirm_password);
        back= findViewById(R.id.back_button);
        register = findViewById(R.id.register_button);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.manBtn)
                {
                    user_gender ="man";
                }
                else if(i==R.id.womanBtn)
                {
                    user_gender ="woman";
                }
            }
        });
        DB= Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        register.setOnClickListener(v -> {
            String user_account = mEditTextAccount.getText().toString();
            String user_password = mEditTextPassword.getText().toString();
            String user_nickname = mEditTextNickname.getText().toString();
            String user_age = mEditTextAge.getText().toString();
            String confirm_password = mEditTextConfirmPassword.getText().toString();
            boolean isNull=user_gender==null;
            if(isNull)
            {
                Toast.makeText(Register_Page.this, "请选择性别", Toast.LENGTH_SHORT).show();
                return;
            }
            //设置注册逻辑
            User user = new User(user_nickname,user_account, user_password,user_age, user_gender);
            if(DB.userDao().findUserByUseraccount(user_account) != null)
            {
                //在界面输出错误信息
                mEditTextAccount.setError("用户名已存在");
                return;
            }
            else if(user_account.isEmpty() || user_password.isEmpty() || user_nickname.isEmpty() || user_age.isEmpty()||user_gender.isEmpty()||confirm_password.isEmpty())
            {
                //在界面输出错误信息
                Toast.makeText(Register_Page.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(!user_password.equals(confirm_password))
            {
                //在界面输出错误信息
                mEditTextConfirmPassword.setError("两次密码不一致");
                return;
            }
            else if(!Pattern.matches("^[0-9]*$", user_age))
            {
                //在界面输出错误信息
                mEditTextAge.setError("年龄只能为数字");
                return;
            }
            else
            {
                //在界面输出注册成功信息
                DB.userDao().insertUser(user);
                Toast.makeText(Register_Page.this, "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Register_Page.this, Log_in_Page.class);//设置切换对应activity
                startActivity(intent);//开始切换
            }
        });
        backTohomePage.setOnClickListener(v -> {
            Intent intent=new Intent(Register_Page.this, Log_in_Page.class);//设置切换对应activity
            startActivity(intent);//开始切换
        });

        back.setOnClickListener(v -> {
            Intent intent=new Intent(Register_Page.this, Log_in_Page.class);//设置切换对应activity
            startActivity(intent);//开始切换
        });

    };
}