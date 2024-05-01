package com.adapter;


import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ai_fashion.R;
import com.example.ai_fashion.wardrobe_cloth;
import com.example.ai_fashion.wardrobe_trousers;
import com.example.ai_fashion.wardrobe_shoes;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    @NonNull
    private List<Uri> imageUris;
    private List<Boolean> checkedStatus;
    private boolean showCheckBoxes = false;
    public ImagesAdapter(List<Uri> imageUris, List<Boolean> checkedStatus) {
        this.imageUris = imageUris;
        this.checkedStatus = checkedStatus;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 这里假设布局文件为 image_item.xml，且其中有一个 ImageView 控件 id 为 imageView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    if (position < 0 || position >= imageUris.size()) {
        return;
    }
    holder.imageView.setImageURI(imageUris.get(position));
    holder.checkBox.setChecked(checkedStatus.get(position));
    holder.checkBox.setVisibility(showCheckBoxes ? View.VISIBLE : View.GONE);
    holder.itemView.setOnLongClickListener(v -> {
        wardrobe_cloth.backTohomePage.setVisibility(View.INVISIBLE);
        wardrobe_cloth.cloth_title.setText("删除");
        wardrobe_cloth.uploadPictures.setVisibility(View.INVISIBLE);
        wardrobe_cloth.cloth_cancel.setVisibility(View.VISIBLE);
        wardrobe_cloth.cloth_confirm.setVisibility(View.VISIBLE);


        showCheckBoxes = true;
        notifyDataSetChanged();
        return true;
    });

    holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
        checkedStatus.set(position, isChecked);
    });

}
    //获取图片列表的大小
    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
    public void hideCheckBoxes() {
        showCheckBoxes = false;
        notifyDataSetChanged();

    }
    public void deleteSelectedImages() {
        for (int i = checkedStatus.size() - 1; i >= 0; i--) {
            if (checkedStatus.get(i)) {
                Uri imageUri = imageUris.get(i);
                File file = new File(imageUri.getPath());
                if (file.exists()) {
                    file.delete();
                }
                checkedStatus.remove(i);
                imageUris.remove(i);
            }
        }
        notifyDataSetChanged();
    }


}
 /*Uri imageUri = imageUris.get(position);
        Glide.with(holder.itemView.getContext())
                .load(imageUri)
                .into(holder.imageView);
    public void setImageUris(List<Uri> imageUris) {
        this.imageUris = imageUris;
        notifyDataSetChanged();
    }*/