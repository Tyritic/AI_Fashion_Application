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

public class Body_Page extends AppCompatActivity {

    //初始化组件
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

                //更新本地数据库
                DB.userDao().updateUser(user);

                //发送用户信息到服务器
                new Thread(() -> {
                    boolean success=sendUserToServer(user);
                    if(success)
                    {
                        //更新本地数据库
                        DB.userDao().updateUser(user);
                        runOnUiThread(() -> {
                            Toast.makeText(Body_Page.this,"修改成功",Toast.LENGTH_SHORT).show();
                        });
                    }
                    else
                    {
                        runOnUiThread(() -> {
                            Toast.makeText(Body_Page.this,"修改失败",Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();

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

    //发送用户信息到服务器，并返回修改结果
    public boolean sendUserToServer(User user) {
        // 创建一个Gson对象
        Gson gson = new Gson();

        // 将对象转换为JSON格式的字符串
        String json = gson.toJson(user);

        // 创建RequestBody对象，它包含了要发送的数据
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        // 创建Request对象，它表示了一个HTTP请求
        Request request = new Request.Builder()
                .url("http://10.196.5.214:8010")
                .post(body)
                .build();

        // 创建OkHttpClient对象，它表示了一个HTTP客户端
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