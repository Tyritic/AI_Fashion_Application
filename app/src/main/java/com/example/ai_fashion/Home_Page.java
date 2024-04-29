package com.example.ai_fashion;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

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
        user = DB.userDao().findUser(user_account,user_password);
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
        File icon=new File(directory,"icon");
        if (!icon.exists()){
            icon.mkdir();
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_icon);
        File defaule_icon=new File(icon,"default_icon.jpg");
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    // 创建一个FileOutputStream来写入图片
                    FileOutputStream fos = new FileOutputStream(defaule_icon);
                    // 将Bitmap压缩为JPEG格式，并写入到FileOutputStream中
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        if(user.getUser_icon()==null)
        {
            User user1 = new User();
            user1.setUser_id(user.getUser_id());
            user1.setUser_account(user.getUser_account());
            user1.setUser_password(user.getUser_password());
            user1.setUser_icon(defaule_icon.getAbsolutePath());
            user1.setUser_nickname(user.getUser_nickname());
            user1.setUser_age(user.getUser_age());
            user1.setUser_height(user.getUser_height());
            user1.setUser_weight(user.getUser_weight());
            user1.setUser_gender(user.getUser_gender());
            user1.setUser_proportion(user.getUser_proportion());
            DB.userDao().updateUser(user1);
        }
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



}