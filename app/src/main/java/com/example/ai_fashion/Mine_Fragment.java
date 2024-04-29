package com.example.ai_fashion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.health.connect.datatypes.units.Length;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;


public class Mine_Fragment extends Fragment {
    String user_account;
    String user_password;
    String imagePath;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();//接收从Home_Page和Account_Page传过来的Bundle
        if(bundle!=null)//判空
        {
            //Toast.makeText(getActivity(),"Mine_Fragment成功接收数据",Toast.LENGTH_SHORT).show();
            user_account = bundle.getString("user_account");
            user_password = bundle.getString("user_password");
        }
        else
        {
            Toast.makeText(getActivity(),"Mine_Fragment未接收数据",Toast.LENGTH_SHORT).show();
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        // Find the button and set the click listener
        Button accountInformationButton = view.findViewById(R.id.account_information_button);
        Button bodyInformationButton = view.findViewById(R.id.body_information_button);
        Button log_out_button = view.findViewById(R.id.log_out_button);
        CircleImage circleImage = view.findViewById(R.id.head_image);
        AppDatabase DB = Room.databaseBuilder(getActivity(), AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        User user = DB.userDao().findUser(user_account,user_password);
        if(user!=null)
        {
            imagePath=user.getUser_icon();
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        circleImage.setImageBitmap(bitmap);
        log_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(),"成功退出登录",Toast.LENGTH_SHORT).show();
                //建立一个Intent对象，用于启动Login_Page
                Intent intent = new Intent(getActivity(), Log_in_Page.class);
                startActivity(intent);
            }
        });
        //账号信息点击事件
        accountInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //建立一个Intent对象，用于启动Account_Page
                Intent intent = new Intent(getActivity(), Account_Page.class);
                //将用户账号和密码传递给Account_Page
                if(user_account!=null&&user_password!=null)
                {
                    //Toast.makeText(getActivity(),"Mine_Fragment向Account_Page发送成功",Toast.LENGTH_SHORT).show();
                    intent.putExtra("user_account", user_account);
                    intent.putExtra("user_password", user_password);
                }
                else
                {
                    Toast.makeText(getActivity(),"Mine_Fragment向Account_Page发送失败",Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
                }
            });
        //身材信息点击事件
        bodyInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //建立一个Intent对象，用于启动Body_Page
                Intent intent = new Intent(getActivity(), Body_Page.class);
                if(user_account!=null&&user_password!=null)
                {
                    //Toast.makeText(getActivity(),"Mine_Fragment向Body_Page发送成功",Toast.LENGTH_SHORT).show();
                    intent.putExtra("user_account", user_account);
                    intent.putExtra("user_password", user_password);
                }
                else
                {
                    Toast.makeText(getActivity(),"Mine_Fragment向Body_Page发送失败",Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
        });
            return view;
    }
}