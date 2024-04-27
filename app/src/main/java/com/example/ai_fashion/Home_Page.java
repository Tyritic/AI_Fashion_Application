package com.example.ai_fashion;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
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
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

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

    private String response;
    private String address;
    private String province;
    private String city;
    private String district;
    private static final String TAG = "Home_Page";
    private static final int REQUEST_INTERNET_PERMISSION = 5555;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        String api_name="HE2404252134531124";
        String api_key="66f16e8945874a35a7cc40032eb4c7f8";
        HeConfig.init(api_name, api_key);
        HeConfig.switchToDevService();
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
                    String response = (String) msg.obj;
                    // 在这里，你可以获取到 response 的值
                    if(response==null)
                    {
                        Toast.makeText(Home_Page.this, "response是空", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject regeocode = jsonObject.getJSONObject("regeocode");
                            JSONObject addressComponent = regeocode.getJSONObject("addressComponent");
                            province = addressComponent.getString("province");
                            city = addressComponent.getString("city");
                            district = addressComponent.getString("district");
                            address = province + city + district;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(Home_Page.this,address, Toast.LENGTH_SHORT).show();
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
                    response = getAddress(longitude,latitude);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = response;
                    handler.sendMessage(message);
                }).start();
            }
        });
        //Toast.makeText(Home_Page.this, "城市：" + address, Toast.LENGTH_SHORT).show();
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
            } else {
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
        String urlString = "https://restapi.amap.com/v3/geocode/regeo?output=json&location=" + lon + "," + lat + "&key=b37606d49c5d3648e1ece38257fd057a&radius=1000&extensions=base";
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