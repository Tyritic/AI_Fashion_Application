package com.example.ai_fashion;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home_Page extends AppCompatActivity {

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