package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;


public class Mine_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        // Find the button and set the click listener
        Button accountInformationButton = view.findViewById(R.id.account_information_button);
            accountInformationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start Account_Page
                    Intent intent = new Intent(getActivity(), Account_Page.class);
                    startActivity(intent);
                }
            });
            return view;
    }
}