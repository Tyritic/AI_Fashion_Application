package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class Wardrobe_Fragment extends Fragment {
    String user_account;
    String user_password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();//接收从Home_Page和Account_Page传过来的Bundle
        if(bundle!=null)//判空
        {
            //Toast.makeText(getActivity(),"Wardrobe_Page接收成功",Toast.LENGTH_SHORT).show();
            user_account = bundle.getString("user_account");
            user_password = bundle.getString("user_password");
        }
        else
        {
            Toast.makeText(getActivity(),"Wardrobe_Page接收失败",Toast.LENGTH_SHORT).show();
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wardrobe, container, false);
        // Find the button and set the click listener
        Button clothButton = view.findViewById(R.id.cloth_button);
        //衣服按钮点击
        clothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start Account_Page
                Intent intent = new Intent(getActivity(), wardrobe_cloth.class);
                if(user_account!=null&&user_password!=null)
                {
                    intent.putExtra("user_account", user_account);
                    intent.putExtra("user_password", user_password);
                }
                else
                {
                    Toast.makeText(getActivity(),"空",Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
        });
        Button trousersButton = view.findViewById(R.id.trousers_button);
        //裤子按钮点击
        trousersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start Account_Page
                Intent intent = new Intent(getActivity(), wardrobe_trousers.class);
                if(user_account!=null&&user_password!=null)
                {
                    intent.putExtra("user_account", user_account);
                    intent.putExtra("user_password", user_password);
                }
                else
                {
                    Toast.makeText(getActivity(),"空",Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
        });
        Button shoesButton = view.findViewById(R.id.shoes_button);
        shoesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start Account_Page
                Intent intent = new Intent(getActivity(), wardrobe_shoes.class);
                if(user_account!=null&&user_password!=null)
                {
                    intent.putExtra("user_account", user_account);
                    intent.putExtra("user_password", user_password);
                }
                else
                {
                    Toast.makeText(getActivity(),"空",Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
        });
        return view;
    }
}