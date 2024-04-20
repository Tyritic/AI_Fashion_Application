package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
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
        backTohomePage = findViewById(R.id.back_to_home_page);
        backTohomePage.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setClass(this, Home_Page.class);
                intent.putExtra("fragment_flag", 2);
                startActivity(intent);
            });
    }
}