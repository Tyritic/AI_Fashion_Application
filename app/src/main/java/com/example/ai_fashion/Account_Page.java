package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Insert;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;

public class Account_Page extends AppCompatActivity {
    ImageButton backTohomePage;
    EditText accoount_input;
    EditText password_input;
    EditText birthday_input;
    String user_account;
    String user_password;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_page);
        Intent intent1 = getIntent();
        user_account = intent1.getStringExtra("user_account");
        user_password = intent1.getStringExtra("user_password");
        AppDatabase DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        User user = DB.userDao().findUser(user_account,user_password);
        accoount_input = findViewById(R.id.account_input);
        password_input = findViewById(R.id.password_input);
        birthday_input = findViewById(R.id.birthday_input);
        accoount_input.setText(user.getUser_account());
        password_input.setText(user.getUser_password());
        birthday_input.setText(user.getUser_birthday());
        Button modify = findViewById(R.id.modify_button);
        modify.setOnClickListener(v -> {
            String account = accoount_input.getText().toString();
            String password = password_input.getText().toString();
            String birthday = birthday_input.getText().toString();
            if(account.equals(user_account)&&password.equals(user_password))
            {
                Toast.makeText(Account_Page.this,"未修改",Toast.LENGTH_SHORT).show();
            }
            else if(DB.userDao().findUserByUsername(account)!=null)
            {
                Toast.makeText(Account_Page.this,"用户名已存在",Toast.LENGTH_SHORT).show();
            }
            else
            {
                User user1 = new User();
                user1.setUser_account(account);
                user1.setUser_password(password);
                user1.setUser_birthday(birthday);
                user1.setUser_nickname(user.getUser_nickname());
                user1.setUser_id(user.getUser_id());
                user1.setUser_gender(user.getUser_gender());
                DB.userDao().updateUser(user1);
                Toast.makeText(Account_Page.this,"修改成功",Toast.LENGTH_SHORT).show();
            }
        });
        backTohomePage = findViewById(R.id.back_to_home_page);
        backTohomePage.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setClass(this, Home_Page.class);
                intent.putExtra("fragment_flag", 2);
                startActivity(intent);
            });
    }
}