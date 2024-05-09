package com.example.ai_fashion;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    User user;
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
            //创建一个新线程，用于登录与处理服务器交互
            new Thread(() -> {
                User user = new User(user_account, user_password);
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
                try {
                    Response response = client.newCall(request).execute();
                    //跳转到主页面
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // 使用OkHttpClient发送HTTP请求
                try {
                    // 发送请求并获取服务器的响应
                    Response response = client.newCall(request).execute();

                    // 请求成功，解析服务器返回的数据
                    if (response.isSuccessful()) {
                        // 打印服务器返回的数据
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        System.out.println(jsonObject);
                        // 解析服务器返回的数据中的message字段
                        String message = jsonObject.getString("message");
                        System.out.println(message);
                        if(message.equals("Login successful")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            // 解析服务器返回的数据中的data字段并创建User对象
                            int user_id = data.getInt("user_id");
                            String user_nickname = data.getString("user_nickname");
                            String user_age = data.getString("user_age");
                            String user_gender = data.getString("user_gender");
                            String user_icon = data.getString("user_icon");
                            Double user_height = data.getDouble("user_height");
                            Double user_weight = data.getDouble("user_weight");
                            Double user_proportion = data.getDouble("user_proportion");
                            User user1 = new User(user_id, user_nickname, user_account, user_password, user_age, user_gender, user_height, user_weight, user_proportion, user_icon);

                            //若第一次登录则将用户信息存入数据库
                            if(DB.userDao().findUserByUserid(user_id)==null)
                            {
                                DB.userDao().insertUser(user1);
                            }

                            //创建用户文件夹
                            createUserFolder(user1.getUser_id());

                            //下载默认头像
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_icon);
                            File icon=getIconFolder(user1.getUser_id());
                            File defaule_icon=new File(icon,"default_icon.jpg");
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(defaule_icon);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                fileOutputStream.flush();
                                fileOutputStream.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //如果用户没有设置头像，则设置默认头像
                            if(user1.getUser_icon().equals("0"))
                            {
                                user1.setUser_icon(defaule_icon.getAbsolutePath());
                                DB.userDao().updateUser(user1);
                            }

                            //跳转到主页面
                            runOnUiThread(() -> {
                                Toast.makeText(Log_in_Page.this,"欢迎用户"+user1.getUser_nickname()+"回来",Toast.LENGTH_SHORT).show();
                                //跳转到主页面
                                Intent intent = new Intent(Log_in_Page.this, Home_Page.class);
                                intent.putExtra("user_account",user_account);
                                intent.putExtra("user_password",user_password);
                                startActivity(intent);
                            });
                        }
                        else if(message.equals("Invalid password")) {
                            System.out.println("Invalid password");
                            runOnUiThread(() -> {
                                Toast.makeText(Log_in_Page.this,"密码错误",Toast.LENGTH_SHORT).show();
                            });
                        }
                        else if(message.equals("Account does not exist")) {
                            System.out.println("Account does not exist");
                            runOnUiThread(() -> {
                                Toast.makeText(Log_in_Page.this,"用户不存在",Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                    else {
                        // 请求失败，打印错误信息
                        System.out.println("request failed: " + response.message());
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    //创建用户文件夹
    private void createUserFolder(int user_id)
    {
        //创建用户文件夹
        String filename=""+user_id;
        File directory=new File(getFilesDir(),filename);
        if (!directory.exists()){
            directory.mkdir();
        }
        File wardrobe=new File(directory,"wardrobe");
        if (!wardrobe.exists()){
            wardrobe.mkdir();
        }
        File clothes=new File(wardrobe,"clothes");
        if (!clothes.exists()){
            clothes.mkdir();
        }
        File trousers=new File(wardrobe,"trousers");
        if (!trousers.exists()){
            trousers.mkdir();
        }
        File shoes=new File(wardrobe,"shoes");
        if (!shoes.exists()){
            shoes.mkdir();
        }
        File dressing=new File(directory,"dressing");
        if (!dressing.exists()){
            dressing.mkdir();
        }
        File icon=new File(directory,"icon");
        if (!icon.exists()){
            icon.mkdir();
        }
    }

    //获取头像文件夹
    private File getIconFolder(int user_id)
    {
        String filename=""+user_id;
        File directory=new File(getFilesDir(),filename);
        File icon=new File(directory,"icon");
        return icon;
    }




}
