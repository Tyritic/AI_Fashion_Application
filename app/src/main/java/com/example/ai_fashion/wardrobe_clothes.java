package com.example.ai_fashion;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.JavaBean.User;
import com.Utils.ImageProcessor;
import com.adapter.Clothes_Images_Adapter;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class wardrobe_clothes extends AppCompatActivity
{
    private String serverUrl = "http://10.196.27.132:8010";
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private Dialog mDialog;
    String user_account;
    String user_password;
    User user;
    public static final int REQUSET_CAMERA_PERMISSION  = 5555;
    public static ImageButton backTohomePage;
    public static TextView cloth_title;
    public static ImageButton uploadPictures;

    //cycleView更改
    public static TextView cloth_cancel;
    public static TextView cloth_confirm;
    AppDatabase DB;
    private List<Uri> imageUris = new ArrayList<>();
    private List<Boolean> checkedStatus=new ArrayList<>();
    Clothes_Images_Adapter clothesImagesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_cloth);

        //检查是否具有相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求存储权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUSET_CAMERA_PERMISSION);
        }
        EdgeToEdge.enable(this);

        //初始化组件
        cloth_title = findViewById(R.id.cloth_title);
        backTohomePage = findViewById(R.id.cloth_back_to_home_page);
        uploadPictures = findViewById(R.id.cloth_add_image);
        DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        cloth_cancel = findViewById(R.id.cloth_cancel);
        cloth_confirm = findViewById(R.id.cloth_confirm);

        //获取用户账号和密码，通过上一个页面传递过来的数据
        Intent intent1 = getIntent();
        user_account = intent1.getStringExtra("user_account");
        user_password = intent1.getStringExtra("user_password");
        user = DB.userDao().findUser(user_account,user_password);

        //返回主页的点击事件
        backTohomePage.setOnClickListener(v -> {
            //创建一个Intent对象，启动Home_Page页面，传递用户账号和密码
            Bundle bundle=new Bundle();
            Intent intent = new Intent();
            if(user_account!=null&&user_password!=null)
            {
                bundle.putString("user_account",user_account);
                bundle.putString("user_password",user_password);
            }
            else
            {
                Toast.makeText(wardrobe_clothes.this,"wardrobe_cloth向Home_Page发送失败",Toast.LENGTH_SHORT).show();
            }
            intent.setClass(this, Home_Page.class);
            intent.putExtra("fragment_flag", 0);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        //上传图片的点击事件
        uploadPictures.setOnClickListener(v -> {
            showDialog();
        });

        //取消按钮的点击事件
        cloth_cancel.setVisibility(View.INVISIBLE);
        cloth_cancel.setOnClickListener(v -> {
            clothesImagesAdapter.hideCheckBoxes();
            backTohomePage.setVisibility(View.VISIBLE);
            cloth_title.setText("衣服");
            uploadPictures.setVisibility(View.VISIBLE);
            cloth_cancel.setVisibility(View.INVISIBLE);
            cloth_confirm.setVisibility(View.INVISIBLE);

        });

        //确认按钮的点击事件
        cloth_confirm.setVisibility(View.INVISIBLE);
        cloth_confirm.setOnClickListener(v -> {
            clothesImagesAdapter.deleteSelectedImages();
            backTohomePage.setVisibility(View.VISIBLE);
            cloth_title.setText("衣服");
            clothesImagesAdapter.hideCheckBoxes();
            uploadPictures.setVisibility(View.VISIBLE);
            cloth_cancel.setVisibility(View.INVISIBLE);
            cloth_confirm.setVisibility(View.INVISIBLE);
        });
    }

    //onStart()方法
    //导入图片到recyclerView
    //布局中recyclerView实例化
    @Override
    protected void onStart() {
        super.onStart();
        //导入图片到recyclerView
        if(imageUris.isEmpty()) {
            for (int i = 0; i < getNum(); i++) {
                String string = "" + getPath() + "/clothes_" + i + ".jpg";
                Uri uri = Uri.fromFile(new File(string));
                imageUris.add((uri));
                checkedStatus.add(false);
            }
        }
        //布局中recyclerView实例化
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //将适配器初始化构造并实例化
        clothesImagesAdapter = new Clothes_Images_Adapter(imageUris,checkedStatus);
        //将实例化的适配器设置给recyclerView
        recyclerView.setAdapter(clothesImagesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //一行多个测试
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
    }

    //初始化分享对话框
    private void initShareDialog() {
        mDialog = new Dialog(this, R.style.dialogStyle);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);            //点击框外，框退出
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);      //位于底部
        //window.setWindowAnimations(R.style.dialog_share);    //弹出动画
        View inflate = View.inflate(this, R.layout.dialog_retrieve_password, null);
        //取消按钮
        inflate.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();      //消失，退出
                }
            }
        });

        //相册按钮
        inflate.findViewById(R.id.dialog_photo_library).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用本地相册
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 允许选择多张图片
                startActivityForResult(intent, PICK_IMAGE);    //访问本地相册
                //访问选中的图片
                if (mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();      //消失，退出
                }
            }
        });

        //相机按钮
        inflate.findViewById(R.id.dialog_take_photos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PHOTO);    //访问相机
                //访问拍摄的图片
                if (mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();      //消失，退出
                }
            }
        });
        window.setContentView(inflate);
        //横向充满
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
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
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    Uri selectedImage = data.getData();
                    //切入口获取到Uri，还未作出处理
                    ImageProcessor imageProcessor = new ImageProcessor();
                    String jsonString=imageProcessor.encodeImageUriToBase64(this,selectedImage);
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put("image", jsonString); // 将 encodedImage 字符串存储在 "image" 键下
                    Gson gson = new Gson();
                    String jsonstring = gson.toJson(dataMap);
                    imageProcessor.uploadImageAsync(serverUrl, jsonstring, new ImageProcessor.ImageProcessorListener() {
                        @Override
                        public void onUploadSuccess(byte[] processedImageBytes) {
                            byte[] temp=processedImageBytes;
                        }

                        @Override
                        public void onUploadFailure(Exception e) {
                            // 处理上传失败的情况
                            e.printStackTrace();
                            // ...
                        }
                    });
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
            } else if (data.getData() != null) {
                // 用户只选择了一张图片
                Uri selectedImage = data.getData();
                ImageProcessor imageProcessor = new ImageProcessor();
                String jsonString=imageProcessor.encodeImageUriToBase64(this,selectedImage);
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("image", jsonString); // 将 encodedImage 字符串存储在 "image" 键下
                Gson gson = new Gson();
                String jsonstring = gson.toJson(dataMap);
                imageProcessor.uploadImageAsync(serverUrl, jsonstring, new ImageProcessor.ImageProcessorListener() {
                    @Override
                    public void onUploadSuccess(byte[] processedImageBytes) {
                        byte[] temp=processedImageBytes;
                    }

                    @Override
                    public void onUploadFailure(Exception e) {
                        // 处理上传失败的情况
                        e.printStackTrace();
                        // ...
                    }
                });
                try {
                    // 获取图片的输入流
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    // 将输入流解码为Bitmap
                    Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream);
                    // 保存图片
                    saveImage(selectedBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == TAKE_PHOTO && resultCode == RESULT_OK && data != null)
        {
            // 获取拍摄的图片
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); // 使用PNG格式，100表示无压缩
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            // 将Bitmap转换为Base64字符串
            String encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
            //切入口获取到Uri，还未作出处理
            ImageProcessor imageProcessor = new ImageProcessor();
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("image", encodedString); // 将 encodedImage 字符串存储在 "image" 键下
            Gson gson = new Gson();
            String jsonstring = gson.toJson(dataMap);

            imageProcessor.uploadImageAsync(serverUrl, jsonstring, new ImageProcessor.ImageProcessorListener() {
                @Override
                public void onUploadSuccess(byte[] processedImageBytes) {
                    byte[] temp=processedImageBytes;
                }

                @Override
                public void onUploadFailure(Exception e) {
                    // 处理上传失败的情况
                    e.printStackTrace();
                    // ...
                }
            });
            if (imageBitmap != null) {
                saveImage(imageBitmap);
            }
        }
        //上传图片后同步到RecyclerView
        for(int i=imageUris.size();i<getNum();i++) {
            String string= ""+getPath() +"/clothes_"+i+".jpg";
            Uri uri = Uri.fromFile(new File(string));
            imageUris.add((uri));
            checkedStatus.add(false);
        }
    }

    //显示对话框
    private void showDialog() {
        if (mDialog ==null){
            initShareDialog();
        }
        mDialog.show();
    }

    //获取图片数量
    private int getNum()
    {
        String user_frame_name=""+user.getUser_id();
        // 获取你之前创建的文件夹的路径
        File directory = getFilesDir();
        // 访问多级目录
        File user_frame = new File(directory, user_frame_name);
        File wardrobe = new File(user_frame, "wardrobe");
        File clothes = new File(wardrobe, "clothes");
        File[] files = clothes.listFiles();
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
        File wardrobe = new File(user_frame, "wardrobe");
        File clothes = new File(wardrobe, "clothes");
        return clothes.getPath();
    }

    // 保存图片
    private void saveImage(Bitmap bitmap) {
        int num=getNum();
        // 获取本地保存路径
        File clothes = new File(getPath());
        File imageFile = new File(clothes, "clothes_"+num+".jpg");
        try {
            // 创建一个FileOutputStream来写入图片
            FileOutputStream fos = new FileOutputStream(imageFile);
            // 将Bitmap压缩为JPEG格式，并写入到FileOutputStream中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Log.d("Image Save", "Image saved to " + imageFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //更改
    /*
    public void updateImageUris(List<Uri> newImageUris) {
        imageUris.addAll(newImageUris); // 或者用新的列表替换旧的列表
        adapter.setImageUris(imageUris); // 更新适配器中的图片列表
    }
    */

}
