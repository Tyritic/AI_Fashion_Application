package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class Dressing_Fragment extends Fragment {
    String user_account;
    String user_password;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();//接收从Home_Page和Account_Page传过来的Bundle
        if(bundle!=null)//判空
        {
            //Toast.makeText(getActivity(),"Dressing_Fragment成功接收数据",Toast.LENGTH_SHORT).show();
            user_account = bundle.getString("user_account");
            user_password = bundle.getString("user_password");
        }
        else
        {
            Toast.makeText(getActivity(),"Dressing_Fragment未接收数据",Toast.LENGTH_SHORT).show();
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dressing, container, false);
        // Find the button and set the click listener
        Button recommend = view.findViewById(R.id.recommend_button);
        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //建立一个Intent对象，用于启动Login_Page
                Intent intent = new Intent(getActivity(), Recommend_Page.class);
                if(user_account!=null&&user_password!=null)
                {
                    //Toast.makeText(getActivity(),"Dressing_Fragment向Recommend_Page发送成功",Toast.LENGTH_SHORT).show();
                    intent.putExtra("user_account", user_account);
                    intent.putExtra("user_password", user_password);
                }
                else
                {
                    Toast.makeText(getActivity(),"Dressing_Fragment向Recommend_Page发送失败",Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
        });

        return view;
    }
}