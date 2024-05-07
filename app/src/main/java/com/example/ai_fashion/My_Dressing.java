package com.example.ai_fashion;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.Outfit;
import com.JavaBean.User;
import com.JavaBean.Style;
import com.Utils.ImageProcessor;
import com.adapter.Clothes_Images_Adapter;
import com.adapter.My_Dressing_Adapter;
import com.adapter.Shoes_Images_Adapter;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class My_Dressing extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    String user_account;
    String user_password;
    User user;
    AppDatabase DB;
    public static ImageButton add_outfit;
    public static ImageButton my_dressing_backTohomePage;
    private List<Outfit> my_outfits = new ArrayList<>();
    My_Dressing_Adapter my_dressing_adapter;
    public static final int REQUSET_CAMERA_PERMISSION  = 5555;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_dressing);
        //检查是否具有存储权限
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求存储权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUSET_CAMERA_PERMISSION);
        }

        //获取用户账号和密码，通过上一个页面传递过来的数据
        Intent intent1 = getIntent();
        user_account = intent1.getStringExtra("user_account");
        user_password = intent1.getStringExtra("user_password");
        if(user_account==null||user_password==null)
        {
            Toast.makeText(My_Dressing.this,"My_Dressing接收失败",Toast.LENGTH_SHORT).show();
        }
        DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        user = DB.userDao().findUser(user_account,user_password);

        add_outfit=findViewById(R.id.add_my_dressing);
        add_outfit.setOnClickListener(v -> {
                //调用本地相册
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 允许选择多张图片
                startActivityForResult(intent, PICK_IMAGE);    //访问本地相册
                //访问选中的图片
        });

        //返回到主页
        my_dressing_backTohomePage=findViewById(R.id.my_dressing_back_to_home_page);
        my_dressing_backTohomePage.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            Intent intent = new Intent();
            bundle.putString("user_account",user_account);
            bundle.putString("user_password",user_password);
            intent.setClass(this, Home_Page.class);
            intent.putExtra("fragment_flag", 1);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        /*RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        /*for (int i = 0; i < 20; i++) {
            Outfit outfit = new Outfit("data"+i,R.mipmap.ic_launcher);
            my_outfits.add(outfit);
        }
        My_Dressing_Adapter my_Dressing_Adapter = new My_Dressing_Adapter(my_outfits);
        recyclerView.setAdapter(my_Dressing_Adapter);
        //上传图片后同步到RecyclerView
        for(int i=my_outfits.size();i<getNum();i++) {
            String string_cloth= ""+getPath() +"/dressing"+i+"/clothe"+".jpg";
            Uri uri_cloth = Uri.fromFile(new File(string_cloth));
            String string_trousers= ""+getPath() +"/dressing"+i+"/trousers"+".jpg";
            Uri uri_trousers = Uri.fromFile(new File(string_trousers));
            String string_shoes= ""+getPath() +"/dressing"+i+"/shoes"+".jpg";
            Uri uri_shoes = Uri.fromFile(new File(string_shoes));
            Outfit outfit=new Outfit("我的穿搭"+i,(uri_cloth),(uri_trousers),(uri_shoes));
            my_outfits.add(outfit);
        }*/
    }
    @Override
    protected void onStart() {
        super.onStart();
        //导入图片到recyclerView
        if(my_outfits.isEmpty()) {
            for(int i=0;i<getNum();i++) {
                String string_cloth= ""+getPath() +"/dressing"+i+"/clothes"+".png";
                Uri uri_cloth = Uri.fromFile(new File(string_cloth));
                String string_trousers= ""+getPath() +"/dressing"+i+"/trousers"+".png";
                Uri uri_trousers = Uri.fromFile(new File(string_trousers));
                String string_shoes= ""+getPath() +"/dressing"+i+"/shoes"+".png";
                Uri uri_shoes = Uri.fromFile(new File(string_shoes));
                Outfit outfit=new Outfit("我的穿搭"+(i+1),uri_cloth,uri_trousers,uri_shoes);
                my_outfits.add(outfit);
            }
        }
        //布局中recyclerView实例化
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        //将适配器初始化构造并实例化
        my_dressing_adapter = new My_Dressing_Adapter(my_outfits);
        //将实例化的适配器设置给recyclerView
        recyclerView.setAdapter(my_dressing_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //一行多个测试
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
    }
    //获取图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null)
        {
            if (data.getClipData() != null) {
                // 用户选择了多张图片
                ClipData clipData = data.getClipData();
                if(clipData.getItemCount()==3) {
                    int num=getNum();
                    // 获取本地保存路径
                    File dressing = new File(getPath());
                    File dressing_i =new File(dressing,"dressing"+num);
                    dressing_i.mkdir();

                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        Uri selectedImage = data.getData();

                        //切入口获取到Uri，还未作出处理
                        ImageProcessor imageProcessor = new ImageProcessor();
                        String jsonString = imageProcessor.encodeImageUriToBase64(this, selectedImage);
                        Map<String, String> dataMap = new HashMap<>();
                        dataMap.put("image", jsonString); // 将 encodedImage 字符串存储在 "image" 键下
                        Gson gson = new Gson();
                        String jsonstring = gson.toJson(dataMap);

                        // 处理每一张图片
                        try {
                            // 获取图片的输入流
                            InputStream imageStream = getContentResolver().openInputStream(imageUri);
                            // 将输入流解码为Bitmap
                            Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream);
                            // 保存图片
                            saveImage(selectedBitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    /*Style style=new Style(user.getUser_id()
                            ,dressing_i.getPath()+"/clothes"+".png"
                            ,dressing_i.getPath()+"/trousers"+".png"
                            ,dressing_i.getPath()+"/shoes"+".png");
                    DB.styleDao().insertStyle(style);*/
                    //上传图片后同步到RecyclerView
                    for(int i=my_outfits.size();i<getNum();i++) {
                        String string_cloth= ""+getPath() +"/dressing"+i+"/clothes"+".png";
                        Uri uri_cloth = Uri.fromFile(new File(string_cloth));
                        String string_trousers= ""+getPath() +"/dressing"+i+"/trousers"+".png";
                        Uri uri_trousers = Uri.fromFile(new File(string_trousers));
                        String string_shoes= ""+getPath() +"/dressing"+i+"/shoes"+".png";
                        Uri uri_shoes = Uri.fromFile(new File(string_shoes));
                        Outfit outfit=new Outfit("我的穿搭"+(i+1),uri_cloth,uri_trousers,uri_shoes);
                        my_outfits.add(outfit);

                    }
                }else
                    Toast.makeText(this,"上传失败，请选择三张图片。",Toast.LENGTH_SHORT).show();
            }

        }
    }
    // 保存图片
    private void saveImage(Bitmap bitmap) {
        int num=getNum();
        // 获取本地保存路径
        File dressing = new File(getPath());
        File dressing_i =new File(dressing,"dressing"+(num-1));

        File imageFile = new File(dressing_i, "clothes"+".png");
        if(!imageFile.exists()) {
            try {
                // 创建一个FileOutputStream来写入图片
                FileOutputStream fos = new FileOutputStream(imageFile);
                // 将Bitmap压缩为JPEG格式，并写入到FileOutputStream中
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                Log.d("Image Save", "Image saved to " + imageFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            imageFile = new File(dressing_i, "trousers" + ".png");
            if (!imageFile.exists()) {
                try {
                    // 创建一个FileOutputStream来写入图片
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    // 将Bitmap压缩为JPEG格式，并写入到FileOutputStream中
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    Log.d("Image Save", "Image saved to " + imageFile.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                imageFile = new File(dressing_i, "shoes" + ".png");
                if (!imageFile.exists()) {
                    try {
                        // 创建一个FileOutputStream来写入图片
                        FileOutputStream fos = new FileOutputStream(imageFile);
                        // 将Bitmap压缩为JPEG格式，并写入到FileOutputStream中
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                        Log.d("Image Save", "Image saved to " + imageFile.getAbsolutePath());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
    //获取图片数量
    private int getNum()
    {
        String user_frame_name=""+user.getUser_id();
        // 获取你之前创建的文件夹的路径
        File directory = getFilesDir();
        // 访问多级目录
        File user_frame = new File(directory, user_frame_name);
        File dressing = new File(user_frame, "dressing");
        File[] files = dressing.listFiles();
        if (files != null) {
            return files.length;
        }
        return 0;
    }

    //获取本地保存路径
    private String getPath()
    {
        String user_frame_name=""+user.getUser_id();
        // 获取你之前创建的文件夹的路径
        File directory = getFilesDir();
        // 访问多级目录
        File user_frame = new File(directory, user_frame_name);
        File dressing = new File(user_frame, "dressing");
        return dressing.getPath();
    }
}