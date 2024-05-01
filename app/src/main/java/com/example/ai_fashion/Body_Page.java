package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;

import java.util.regex.Pattern;

public class Body_Page extends AppCompatActivity {

    ImageButton ImageButton_backTohomePage;
    EditText mEditTextHeight;
    EditText mEditTextWeight;
    EditText mEditTextProportion;
    Button button_modify;
    AppDatabase DB;
    String user_account;
    String user_password;
    String user_height;
    String user_weight;
    String user_proportion;
    String height;
    String weight;
    String proportion;
    User user;
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
        DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        user = DB.userDao().findUser(user_account,user_password);
        //初始化组件
        mEditTextHeight = findViewById(R.id.height_input);
        mEditTextWeight = findViewById(R.id.weight_input);
        mEditTextProportion = findViewById(R.id.proportion_input);
        button_modify = findViewById(R.id.modify_button);
        ImageButton_backTohomePage = findViewById(R.id.back_to_home_page);

        //获取用户的信息
        user_height = ""+user.getUser_height();
        user_weight = ""+user.getUser_weight();
        user_proportion = ""+user.getUser_proportion();

        //如果用户没有填写信息，则设置为未填写
        if(user_height.equals("0.0")&&user_weight.equals("0.0"))
        {
            user_height="未填写";
            user_weight="未填写";
            user_proportion="未填写";
        }

        //设置输入框的默认值为用户的信息并加上单位
        if(user_height.equals("未填写"))
        {
            mEditTextHeight.setText(user_height);
        }
        else
        {
            mEditTextHeight.setText(user_height+"cm");
        }
        if(user_weight.equals("未填写"))
        {
            mEditTextWeight.setText(user_weight);
        }
        else
        {
            mEditTextWeight.setText(user_weight+"kg");
        }
        if(user_proportion.equals("未填写"))
        {
            mEditTextProportion.setText(user_proportion);
        }
        else
        {
            mEditTextProportion.setText(user_proportion);
        }

        //修改按钮点击事件
        button_modify.setOnClickListener(v -> {
            //获取用户输入的信息
            height = mEditTextHeight.getText().toString();
            weight = mEditTextWeight.getText().toString();
            proportion = mEditTextProportion.getText().toString();

            //去掉单位
            height = height.replace("cm","");
            weight = weight.replace("kg","");

            //如果用户没有填写信息，则设置为0.0
            if(height.equals("未填写"))
            {
                height="0.0";
            }
            if(weight.equals("未填写"))
            {
                weight="0.0";
            }
            if(proportion.equals("未填写"))
            {
                proportion="0.0";
            }
            if(height.isEmpty() || weight.isEmpty() || proportion.isEmpty())
            {
                Toast.makeText(Body_Page.this,"请填写完整信息",Toast.LENGTH_SHORT).show();
                return;
            }
            if(!Pattern.matches("^[0-9]*\\.?[0-9]+$", height))
            {
                mEditTextHeight.setError("请输入正确的身高");
                return;
            }
            if(!Pattern.matches("^[0-9]*\\.?[0-9]+$", weight))
            {
                mEditTextWeight.setError("请输入正确的体重");
                return;
            }
            if(!Pattern.matches("^0\\.[0-9]+$", proportion))
            {
                mEditTextProportion.setError("请输入正确的身材比例");
                return;
            }
            //判断用户是否修改了信息
            boolean not_modified=height.equals(user_height)&&weight.equals(user_weight)&&proportion.equals(user_proportion);
            if(not_modified)
            {
                Toast.makeText(Body_Page.this,"未修改",Toast.LENGTH_SHORT).show();
            }
            else
            {
                user.setUser_height(Double.parseDouble(height));
                user.setUser_weight(Double.parseDouble(weight));
                user.setUser_proportion(Double.parseDouble(proportion));
                DB.userDao().updateUser(user);
                Toast.makeText(Body_Page.this,"修改成功",Toast.LENGTH_SHORT).show();
            }
        });

        //返回按钮
        ImageButton_backTohomePage.setOnClickListener(v -> {
            //将用户账号和密码传递给下一个页面
            Bundle bundle=new Bundle();
            Intent intent = new Intent();
            bundle.putString("user_account",user_account);
            bundle.putString("user_password",user_password);
            if(user_account==null||user_password==null)
            {
                Toast.makeText(Body_Page.this,"Body_Page发送失败",Toast.LENGTH_SHORT).show();
            }
            intent.setClass(this, Home_Page.class);
            intent.putExtra("fragment_flag", 2);
            intent.putExtras(bundle);
            startActivity(intent);
        });

    }
}