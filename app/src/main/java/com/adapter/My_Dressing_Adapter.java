package com.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.JavaBean.Outfit;
import com.example.ai_fashion.R;

import java.util.List;

public class My_Dressing_Adapter extends RecyclerView.Adapter<My_Dressing_Adapter.ViewHolder> {

    @NonNull
    private List<Outfit> mOutfits;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_cloth;
        ImageView image_trousers;
        ImageView image_shoes;
        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_cloth = itemView.findViewById(R.id.cloth_image);
            image_trousers= itemView.findViewById(R.id.trouser_image);
            image_shoes = itemView.findViewById(R.id.shoes_image);
            text = itemView.findViewById(R.id.my_dressing_text);
        }
    }

    public My_Dressing_Adapter(List<Outfit> mOutfits) {
        this.mOutfits = mOutfits;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_dressing, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Outfit outfit = mOutfits.get(position);
        holder.image_cloth.setImageURI(outfit.getImageCloth());
        holder.image_trousers.setImageURI(outfit.getImageTrousers());
        holder.image_shoes.setImageURI(outfit.getImageShoes());
        holder.text.setText(outfit.getText());
    }

    @Override
    public int getItemCount() {
        if(mOutfits != null){
            return mOutfits.size();
        }
        return 0;
    }
}

