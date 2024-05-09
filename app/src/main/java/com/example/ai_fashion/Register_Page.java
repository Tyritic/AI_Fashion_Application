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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register_Page extends AppCompatActivity {
    //初始化组件
    EditText mEditTextAccount;
    EditText mEditTextPassword;
    EditText mEditTextNickname;
    EditText mEditTextAge;
    EditText mEditTextConfirmPassword;
    ImageButton ImageButton_backTohomePage;
    Button button_register;
    String user_gender;
    AppDatabase DB;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        //初始化组件
        ImageButton_backTohomePage = findViewById(R.id.backToHomePage);
        mEditTextAccount = findViewById(R.id.account);
        mEditTextPassword = findViewById(R.id.password);
        mEditTextNickname = findViewById(R.id.nickname);
        mEditTextAge = findViewById(R.id.age);
        mEditTextConfirmPassword = findViewById(R.id.confirm_password);
        button_register = findViewById(R.id.register_button);
        radioGroup = findViewById(R.id.radioGroup);

        //设置性别选择
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

        //初始化数据库
        DB= Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();//允许在主线程中查询数据库

        //注册按钮的点击事件
        button_register.setOnClickListener(v -> {
            //获取用户输入的信息
            String user_account = mEditTextAccount.getText().toString();
            String user_password = mEditTextPassword.getText().toString();
            String user_nickname = mEditTextNickname.getText().toString();
            String user_age = mEditTextAge.getText().toString();
            String confirm_password = mEditTextConfirmPassword.getText().toString();

            //判断性别是否为空
            boolean isNull=user_gender==null;
            if(isNull)
            {
                Toast.makeText(Register_Page.this, "请选择性别", Toast.LENGTH_SHORT).show();
                return;
            }

            //发送请求前的预检查
            //判断用户输入的信息是否为空
            if(user_account.isEmpty() || user_password.isEmpty() || user_nickname.isEmpty() || user_age.isEmpty()||user_gender.isEmpty()||confirm_password.isEmpty())
            {
                //在界面输出错误信息
                Toast.makeText(Register_Page.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }
            //检查两次密码是否一致
            else if(!user_password.equals(confirm_password))
            {
                //在界面输出错误信息
                mEditTextConfirmPassword.setError("两次密码不一致");
                return;
            }
            //检查年龄是否为数字
            else if(!Pattern.matches("^[0-9]*$", user_age))
            {
                //在界面输出错误信息
                mEditTextAge.setError("请填写正确的年龄");
                return;
            }

            //使用新线程发送请求
            new Thread(() -> {
                User user = new User(user_nickname,user_account, user_password,user_age, user_gender);
                boolean success = sendUserToServer(user);
                runOnUiThread(() -> {
                    if(success) {
                        // 注册成功，跳转到登录页面
                        Intent intent = new Intent(Register_Page.this, Log_in_Page.class);
                        intent.putExtra("user_account",user_account);
                        intent.putExtra("user_password",user_password);
                        startActivity(intent);
                    } else {
                        // 注册失败，显示错误信息
                        mEditTextAccount.setError("账号已存在");
                    }
                });
            }).start();

        });

        //返回按钮的点击事件
        ImageButton_backTohomePage.setOnClickListener(v -> {
            Intent intent=new Intent(Register_Page.this, Log_in_Page.class);//设置切换对应activity
            startActivity(intent);//开始切换
        });

    };

    //发送用户信息到服务器，并返回注册结果
    public boolean sendUserToServer(User user) {
        // 创建一个Gson对象
        Gson gson = new Gson();

        // 将对象转换为JSON格式的字符串
        String json = gson.toJson(user);

        // 创建一个RequestBody对象，它包含了要发送的数据
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        // 创建一个Request对象，它表示了一个HTTP请求
        Request request = new Request.Builder()
                .url("http://10.196.5.214:8010")
                .post(body)
                .build();

        // 创建一个OkHttpClient对象，它表示了一个HTTP客户端
        OkHttpClient client = new OkHttpClient();

        boolean success = false;

        // 使用OkHttpClient发送HTTP请求
        try {
            // 发送请求并获取服务器的响应
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // 请求成功，解析服务器返回的数据
                JSONObject jsonObject = new JSONObject(response.body().string());
                // 解析服务器返回的数据中的message字段
                String message = jsonObject.getString("message");
                System.out.println(message);
                if(message.equals("success")) {
                    success = true;
                }
                else if(message.equals("Account already exists")) {
                    success = false;
                }
            }
            else {
                // 请求失败，打印错误信息
                System.out.println("request failed: " + response.message());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return success;
    }

}
