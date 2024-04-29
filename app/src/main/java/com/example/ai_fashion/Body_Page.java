package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;

public class Body_Page extends AppCompatActivity {

    ImageButton backTohomePage;
    EditText height_input;
    EditText weight_input;
    EditText proportion_input;
    String user_account;
    String user_password;
    String user_height;
    String user_weight;
    String user_proportion;
    String height;
    String weight;
    String proportion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_body_page);
        //获取用户账号和密码，通过上一个页面传递过来的数据
        Intent intent1 = getIntent();
        user_account = intent1.getStringExtra("user_account");
        user_password = intent1.getStringExtra("user_password");
        if(user_account==null||user_password==null)
        {
            Toast.makeText(Body_Page.this,"Mine_Fragment接收失败",Toast.LENGTH_SHORT).show();
        }
        else if(user_account!=null&&user_password!=null)
        {
            //Toast.makeText(Body_Page.this,"Mine_Fragment接收成功",Toast.LENGTH_SHORT).show();
        }
        AppDatabase DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        User user = DB.userDao().findUser(user_account,user_password);
        height_input = findViewById(R.id.height_input);
        weight_input = findViewById(R.id.weight_input);
        proportion_input = findViewById(R.id.proportion_input);
        user_height = ""+user.getUser_height();
        user_weight = ""+user.getUser_weight();
        user_proportion = ""+user.getUser_proportion();
        //如果用户没有填写信息，则设置为空
        if(user_height.equals("0.0")&&user_weight.equals("0.0"))
        {
            user_height="";
            user_weight="";
            user_proportion="";
        }
        //设置输入框的默认值为用户的信息
        height_input.setText(user_height);
        weight_input.setText(user_weight);
        proportion_input.setText(user_proportion);
        Button modify = findViewById(R.id.modify_button);
        //修改按钮点击事件
        modify.setOnClickListener(v -> {
            //获取用户输入的信息
            height = height_input.getText().toString();
            weight = weight_input.getText().toString();
            proportion = proportion_input.getText().toString();
            //判断用户是否修改了信息
            boolean not_modified=height.equals(user_height)&&weight.equals(user_weight)&&proportion.equals(user_proportion);
            if(not_modified)
            {
                Toast.makeText(Body_Page.this,"未修改",Toast.LENGTH_SHORT).show();
            }
            else
            {
                User user1 = new User();
                user1.setUser_account(user.getUser_account());
                user1.setUser_password(user_password);
                user1.setUser_age(user.getUser_age());
                user1.setUser_nickname(user.getUser_nickname());
                user1.setUser_id(user.getUser_id());
                user1.setUser_gender(user.getUser_gender());
                user1.setUser_height(Double.parseDouble(height));
                user1.setUser_weight(Double.parseDouble(weight));
                user1.setUser_proportion(Double.parseDouble(proportion));
                DB.userDao().updateUser(user1);
                Toast.makeText(Body_Page.this,"修改成功",Toast.LENGTH_SHORT).show();
            }
        });
        //返回按钮
        backTohomePage = findViewById(R.id.back_to_home_page);
        backTohomePage.setOnClickListener(v -> {
            //将用户账号和密码传递给下一个页面
            Bundle bundle=new Bundle();
            Intent intent = new Intent();
            bundle.putString("user_account",user_account);
            bundle.putString("user_password",user_password);
            if(user_account==null||user_password==null)
            {
                Toast.makeText(Body_Page.this,"Body_Page发送失败",Toast.LENGTH_SHORT).show();
            }
            else if(user_account!=null&&user_password!=null)
            {
                //Toast.makeText(Body_Page.this,"Body_Page发送成功",Toast.LENGTH_SHORT).show();
            }
            intent.setClass(this, Home_Page.class);
            intent.putExtra("fragment_flag", 2);
            intent.putExtras(bundle);
            startActivity(intent);
        });

    }
}