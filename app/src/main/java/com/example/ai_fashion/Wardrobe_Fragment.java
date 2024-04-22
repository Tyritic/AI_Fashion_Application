package com.example.ai_fashion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


public class Wardrobe_Fragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wardrobe, container, false);
        // Find the button and set the click listener
        Button clouthButton = view.findViewById(R.id.cloth_button);
        clouthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start Account_Page
                Intent intent = new Intent(getActivity(), wardrobe_cloth.class);
                startActivity(intent);
            }
        });
        Button trousersButton = view.findViewById(R.id.trousers_button);
        trousersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start Account_Page
                Intent intent = new Intent(getActivity(), wardrobe_trousers.class);
                startActivity(intent);
            }
        });
        Button shoesButton = view.findViewById(R.id.shoes_button);
        shoesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start Account_Page
                Intent intent = new Intent(getActivity(), wardrobe_shoes.class);
                startActivity(intent);
            }
        });
        return view;
    }
}