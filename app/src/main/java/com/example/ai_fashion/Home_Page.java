package com.example.ai_fashion;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;
import com.Utils.LocationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//目录结果
//├── user_id
//        └── wardrobe
//           └── clothes
//               ├── clothes_id.png
//           └── trousers
//               ├── trousers_id.png
//           └── shoes
//               ├── shoes_id.png
//        └── dressing
//           └── dressing_id
//               ├── clothes_id.png
//               ├── trousers_id.png
//               ├── shoes_id.png
public class Home_Page extends AppCompatActivity {

    private String user_account;
    private String user_password;
    private BottomNavigationView bottomNavigationView;
    private Wardrobe_Fragment wardrobeFragment;
    private Dressing_Fragment dressingFragment;
    private Mine_Fragment mineFragment;
    private String latitude;
    private String longitude;

    private String location_response;
    private String weather_response;
    private String address;
    private String province;
    private String city;
    private String district;
    private String township;
    private String adcode;
    private final String api_key="b37606d49c5d3648e1ece38257fd057a";
    private final String location_url_head="https://restapi.amap.com/v3/geocode/regeo?output=json&location=";
    private final String weather_url_head="https://restapi.amap.com/v3/weather/weatherInfo?city=";
    private static final int REQUEST_INTERNET_PERMISSION = 5555;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        //检查是否具有网络权限
        if (checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求网络权限
            requestPermissions(new String[]{android.Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
        }
        // 获取定位信息
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String location_response = (String) msg.obj;
                    // 在这里，你可以获取到 response 的值
                    if(location_response==null)
                    {
                        Toast.makeText(Home_Page.this, "location_response是空", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try {
                            JSONObject location_json = new JSONObject(location_response);
                            JSONObject regeocode = location_json.getJSONObject("regeocode");
                            JSONObject addressComponent = regeocode.getJSONObject("addressComponent");
                            province = addressComponent.getString("province");
                            city = addressComponent.getString("city");
                            if(city.equals("[]"))city="";
                            district = addressComponent.getString("district");
                            township = addressComponent.getString("township");
                            adcode = addressComponent.getString("adcode");
                            address = province + city + district+township;
                            Toast.makeText(Home_Page.this,address, Toast.LENGTH_SHORT).show();
                            new Thread(() -> {
                                weather_response = getWeather(adcode);
                                if(weather_response==null)
                                {
                                    Looper.prepare();
                                    Toast.makeText(Home_Page.this, "weather_response是空", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else
                                {
                                    //System.out.println(weather_response);
                                    try {
                                        JSONObject weather_json = new JSONObject(weather_response);
                                        JSONObject lives = weather_json.getJSONArray("lives").getJSONObject(0);
                                        String weather = lives.getString("weather");
                                        String temperature = lives.getString("temperature");
                                        Looper.prepare();
                                        Toast.makeText(Home_Page.this, "天气："+weather+" 温度："+temperature+"°C", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        LocationUtils.getInstance(this).getLocation(new LocationUtils.LocationCallBack() {
            @Override
            public void setLocation(Location location) {
                if (location != null){
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                }
                else {
                    Toast.makeText(Home_Page.this, "location是空", Toast.LENGTH_SHORT).show();
                }
                if(latitude==null||longitude==null)
                {
                    Toast.makeText(Home_Page.this,"未获取到经纬度",Toast.LENGTH_SHORT).show();
                }
                 new Thread(() -> {
                    location_response = getAddress(longitude, latitude);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = location_response;
                    handler.sendMessage(message);
                }).start();
            }
        });
        //Toast.makeText(Home_Page.this,adcode, Toast.LENGTH_SHORT).show();
//        new Thread(() -> {
//            weather_response = getWeather(adcode);
//            Message message = new Message();
//            message.what = 1;
//            message.obj = weather_response;
//            handler.sendMessage(message);
//        }).start();
            //初始化
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        selectFragment(0);
        AppDatabase DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        //获取用户账号和密码，通过上一个页面传递过来的数据
        Intent intent = getIntent();
        user_account = intent.getStringExtra("user_account");
        user_password = intent.getStringExtra("user_password");
//        if(user_password==null||user_account==null)
//        {
//            Toast.makeText(Home_Page.this,"Home_Page未接收到数据",Toast.LENGTH_SHORT).show();
//        }
//        else if(user_account!=null&&user_password!=null)
//        {
//            Toast.makeText(Home_Page.this,"Home_Page成功接收到数据",Toast.LENGTH_SHORT).show();
//        }
        User user = DB.userDao().findUser(user_account,user_password);
        Gson gson = new Gson();
        String user_json = gson.toJson(user);

        //Toast.makeText(Home_Page.this,user_json,Toast.LENGTH_SHORT).show();
        String filename=""+user.getUser_id();
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
        //点击事件
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.navigation_wardrobe){
                    selectFragment(0);
                } else if (menuItem.getItemId() ==R.id.navigation_dressing) {
                    selectFragment(1);
                } else if (menuItem.getItemId() ==R.id.navigation_mine) {
                    selectFragment(2);
                }
                return false;
            }
        });
        int fragment_flag = getIntent().getIntExtra("fragment_flag", 0);
        if(fragment_flag == 0) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_wardrobe);
        }
        else if(fragment_flag == 1) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_dressing);
        }
        else if(fragment_flag == 2) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_mine);
        }
    }
    private void selectFragment(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);
        //导航栏状态
        if(position==0)
        {
            if(wardrobeFragment == null)
            {
                wardrobeFragment = new Wardrobe_Fragment();
                //传递数据，将用户账号和密码包装成Bundle传递给Wardrobe_Fragment
                Intent intent=getIntent();
                String default_account=intent.getStringExtra("user_account");
                String default_password=intent.getStringExtra("user_password");
                Bundle bundle = new Bundle();
                if(default_account==null||default_password==null)
                {
                    //Toast.makeText(Home_Page.this,"Home_Page向Wardrobe_Fragment发送失败",Toast.LENGTH_SHORT).show();
                }
                else if(default_account!=null&&default_password!=null)
                {
                    bundle.putString("user_account", default_account);
                    bundle.putString("user_password", default_password);
                    //Toast.makeText(Home_Page.this,"Home_Page向Wardrobe_Fragment发送成功",Toast.LENGTH_SHORT).show();
                }
                wardrobeFragment.setArguments(bundle);
                fragmentTransaction.add(R.id.content, wardrobeFragment);
            }
            else
            {
                fragmentTransaction.show(wardrobeFragment);
            }
        }
        else if(position==1)
        {
            if (dressingFragment == null)
            {
                dressingFragment = new Dressing_Fragment();
                //传递数据，将用户账号和密码包装成Bundle传递给Wardrobe_Fragment
                Intent intent=getIntent();
                String default_account=intent.getStringExtra("user_account");
                String default_password=intent.getStringExtra("user_password");
                Bundle bundle = new Bundle();
                if(default_account==null||default_password==null)
                {
                    Toast.makeText(Home_Page.this,"Home_Page向Dressing_Fragment发送失败",Toast.LENGTH_SHORT).show();
                }
                else if(default_account!=null&&default_password!=null)
                {
                    bundle.putString("user_account", default_account);
                    bundle.putString("user_password", default_password);
                    //Toast.makeText(Home_Page.this,"Home_Page向Dressing_Fragment发送成功",Toast.LENGTH_SHORT).show();
                }
                dressingFragment.setArguments(bundle);
                fragmentTransaction.add(R.id.content, dressingFragment);
            }
            else
            {
                fragmentTransaction.show(dressingFragment);
            }
        }
        else if (position==2)
        {
            if (mineFragment == null)
            {
                mineFragment = new Mine_Fragment();
                //传递数据，将用户账号和密码包装成Bundle传递给Mine_Fragment
                Bundle bundle = new Bundle();
                if(user_account==null||user_password==null)
                {
                    //Toast.makeText(Home_Page.this,"Home_Page向Mine_Fragment发送失败",Toast.LENGTH_SHORT).show();
                }
                else if(user_account!=null&&user_password!=null)
                {
                    bundle.putString("user_account", user_account);
                    bundle.putString("user_password", user_password);
                    //Toast.makeText(Home_Page.this,"Home_Page向Mine_Fragment发送成功",Toast.LENGTH_SHORT).show();
                }
                mineFragment.setArguments(bundle);
                fragmentTransaction.add(R.id.content, mineFragment);
            }
            else
            {
                fragmentTransaction.show(mineFragment);
            }
        }
        //提交
        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if(wardrobeFragment != null){
            fragmentTransaction.hide(wardrobeFragment);
        }
        if(dressingFragment != null){
            fragmentTransaction.hide(dressingFragment);
        }
        if(mineFragment != null){
            fragmentTransaction.hide(mineFragment);
        }
    }

    public String getAddress(String lon, String lat) {
        String urlString = location_url_head + lon + "," + lat + "&key="+api_key+"&radius=1000&extensions=base";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            if (connection.getResponseCode() == 200)
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getWeather (String adcode) {
        String urlString = weather_url_head + adcode + "&key="+api_key+"&output=json&extensions=base";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            if (connection.getResponseCode() == 200)
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}