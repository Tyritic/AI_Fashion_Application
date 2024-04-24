package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        User user = DB.userDao().findUser(user_account,user_password);
        if(user!=null)
        {
            Toast.makeText(Home_Page.this,"欢迎用户"+user.getUser_nickname()+"回来",Toast.LENGTH_SHORT).show();
        }
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
        if(position==0){
            if(wardrobeFragment == null){
                wardrobeFragment = new Wardrobe_Fragment();
                fragmentTransaction.add(R.id.content, wardrobeFragment);
            }else{
                fragmentTransaction.show(wardrobeFragment);
            }
        }else if(position==1) {
            if (dressingFragment == null) {
                dressingFragment = new Dressing_Fragment();
                fragmentTransaction.add(R.id.content, dressingFragment);
            } else {
                fragmentTransaction.show(dressingFragment);
            }
        }else if (position==2){
            if (mineFragment == null) {
                mineFragment = new Mine_Fragment();
                //传递数据，将用户账号和密码包装成Bundle传递给Mine_Fragment
                Bundle bundle = new Bundle();
                bundle.putString("user_account", user_account);
                bundle.putString("user_password", user_password);
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
}