package com.example.ai_fashion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class Wardrobe_Fragment extends Fragment {
    String user_account;
    String user_password;
    //初始化组件
    Button button_clothes;
    Button button_trousers;
    Button button_shoes;
    public static final int REQUEST_STORAGE_PERMISSION = 6666;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //接收从Home_Page和Account_Page传过来的Bundle
        Bundle bundle = getArguments();
        if(bundle!=null)//判空
        {
            user_account = bundle.getString("user_account");
            user_password = bundle.getString("user_password");
        }
        else
        {
            Toast.makeText(getActivity(),"Wardrobe_Page接收失败",Toast.LENGTH_SHORT).show();
        }

        //检查是否具有存储权限
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求存储权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }

        //将fragment_wardrobe.xml布局文件加载到当前fragment中
        View view = inflater.inflate(R.layout.fragment_wardrobe, container, false);
        //初始化组件
        button_clothes = view.findViewById(R.id.cloth_button);
        button_trousers = view.findViewById(R.id.trousers_button);
        button_shoes = view.findViewById(R.id.shoes_button);
        //衣服按钮点击
        button_clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个Intent对象，启动wardrobe_cloth页面，传递用户账号和密码
                Intent intent = new Intent(getActivity(), wardrobe_clothes.class);
                if(user_account!=null&&user_password!=null)
                {
                    intent.putExtra("user_account", user_account);
                    intent.putExtra("user_password", user_password);
                }
                else
                {
                    Toast.makeText(getActivity(),"Wardrobe_Fragment向wardrobe_cloth传递失败",Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
        });

        //裤子按钮点击
        button_trousers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个Intent对象，启动wardrobe_trousers页面，传递用户账号和密码
                Intent intent = new Intent(getActivity(), wardrobe_trousers.class);
                if(user_account!=null&&user_password!=null)
                {
                    intent.putExtra("user_account", user_account);
                    intent.putExtra("user_password", user_password);
                }
                else
                {
                    Toast.makeText(getActivity(),"Wardrobe_Fragment向wardrobe_trousers传递失败",Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
        });

        //鞋子按钮点击
        button_shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个Intent对象，启动wardrobe_shoes页面，传递用户账号和密码
                Intent intent = new Intent(getActivity(), wardrobe_shoes.class);
                if(user_account!=null&&user_password!=null)
                {
                    intent.putExtra("user_account", user_account);
                    intent.putExtra("user_password", user_password);
                }
                else
                {
                    Toast.makeText(getActivity(),"Wardrobe_Fragment向wardrobe_shoes传递失败",Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
        });
        return view;
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已经被授予
                Toast.makeText(getActivity(), "已授予存储权限", Toast.LENGTH_SHORT).show();
            } else
            {
                // 权限被拒绝
                Toast.makeText(getActivity(), "未授予存储权限", Toast.LENGTH_SHORT).show();
            }
        }
    }
}