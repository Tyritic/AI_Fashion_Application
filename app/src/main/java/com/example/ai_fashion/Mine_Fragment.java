package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class Mine_Fragment extends Fragment {

    String user_account;
    String user_password;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();//从Activity传过来的Bundle
        if(bundle!=null){
            user_account = bundle.getString("user_account");
            user_password = bundle.getString("user_password");
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        // Find the button and set the click listener
        Button accountInformationButton = view.findViewById(R.id.account_information_button);
            accountInformationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start Account_Page
                    Intent intent = new Intent(getActivity(), Account_Page.class);
                    intent.putExtra("user_account", user_account);
                    intent.putExtra("user_password", user_password);
                    startActivity(intent);
                }
            });
            return view;
    }
}