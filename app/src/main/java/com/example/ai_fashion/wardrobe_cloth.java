package com.example.ai_fashion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.adapter.ImagesAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class wardrobe_cloth extends AppCompatActivity
{
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
    private List<Uri> imageUris = new ArrayList<>();
    private List<Boolean> checkedStatus=new ArrayList<>();
    ImagesAdapter imagesAdapter;
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
        cloth_title = findViewById(R.id.cloth_title);
        //获取用户账号和密码，通过上一个页面传递过来的数据
        AppDatabase DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        Intent intent1 = getIntent();
        user_account = intent1.getStringExtra("user_account");
        user_password = intent1.getStringExtra("user_password");
//        if(user_account!=null)
//        {
//            Toast.makeText(wardrobe_cloth.this,"用户名："+user_account,Toast.LENGTH_SHORT).show();
//        }
//        else
//            Toast.makeText(wardrobe_cloth.this,"用户名："+user_account,Toast.LENGTH_SHORT).show();
        user = DB.userDao().findUser(user_account,user_password);
        backTohomePage = findViewById(R.id.cloth_back_to_home_page);
        backTohomePage.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            Intent intent = new Intent();
            if(user_account!=null&&user_password!=null)
            {
                //Toast.makeText(wardrobe_cloth.this,"wardrobe_cloth向Home_Page发送成功",Toast.LENGTH_SHORT).show();
                bundle.putString("user_account",user_account);
                bundle.putString("user_password",user_password);
            }
            else
            {
                Toast.makeText(wardrobe_cloth.this,"wardrobe_cloth向Home_Page发送失败",Toast.LENGTH_SHORT).show();
            }
            intent.setClass(this, Home_Page.class);
            intent.putExtra("fragment_flag", 0);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        uploadPictures = findViewById(R.id.cloth_add_image);
        uploadPictures.setOnClickListener(v -> {
            showDialog();
        });
        cloth_cancel = findViewById(R.id.cloth_cancel);
        cloth_cancel.setVisibility(View.INVISIBLE);
        cloth_cancel.setOnClickListener(v -> {
            imagesAdapter.hideCheckBoxes();
            backTohomePage.setVisibility(View.VISIBLE);
            cloth_title.setText("衣服");
            uploadPictures.setVisibility(View.VISIBLE);
            cloth_cancel.setVisibility(View.INVISIBLE);
            cloth_confirm.setVisibility(View.INVISIBLE);

        });
        cloth_confirm = findViewById(R.id.cloth_confirm);
        cloth_confirm.setVisibility(View.INVISIBLE);
        cloth_confirm.setOnClickListener(v -> {
            imagesAdapter.deleteSelectedImages();
            backTohomePage.setVisibility(View.VISIBLE);
            cloth_title.setText("衣服");
            imagesAdapter.hideCheckBoxes();
            uploadPictures.setVisibility(View.VISIBLE);
            cloth_cancel.setVisibility(View.INVISIBLE);
            cloth_confirm.setVisibility(View.INVISIBLE);
        });
    }

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
        imagesAdapter = new ImagesAdapter(imageUris,checkedStatus);
        //将实例化的适配器设置给recyclerView
        recyclerView.setAdapter(imagesAdapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //一行多个测试
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initShareDialog() {
        mDialog = new Dialog(this, R.style.dialogStyle);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);            //点击框外，框退出
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);      //位于底部
        //window.setWindowAnimations(R.style.dialog_share);    //弹出动画
        View inflate = View.inflate(this, R.layout.dialog_retrieve_password, null);
        //退出按钮
        inflate.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();      //消失，退出
                }
            }
        });
        inflate.findViewById(R.id.dialog_photo_library).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用本地相册
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);    //访问本地相册
                //访问选中的图片
                if (mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();      //消失，退出
                }
            }
        });
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null)
        {
            int num=getNum();
            //Toast.makeText(wardrobe_cloth.this,"文件中有"+num,Toast.LENGTH_SHORT).show();
            //Toast.makeText(wardrobe_cloth.this,""+requestCode,Toast.LENGTH_SHORT).show();
            Uri selectedImage = data.getData();
            try {
                // 获取图片的输入流
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                // 将输入流解码为Bitmap
                Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream);
                String user_frame_name=""+user.getUser_id();
                // 获取你之前创建的文件夹的路径
                File directory = getFilesDir();
                // 访问多级目录
                File user_frame = new File(directory, user_frame_name);
                File wardrobe = new File(user_frame, "wardrobe");
                File clothes = new File(wardrobe, "clothes");
//                if(clothes.exists())
//                {
//                    Toast.makeText(wardrobe_cloth.this,"衣柜文件夹已存在",Toast.LENGTH_SHORT).show();
//                }
                // 在这个文件夹中创建一个新的文件来保存图片
                File imageFile = new File(clothes, "clothes_"+num+".jpg");
                if(imageFile.exists())
                {
                    Toast.makeText(wardrobe_cloth.this,"上传成功",Toast.LENGTH_SHORT).show();
                }
                // 创建一个FileOutputStream来写入图片
                FileOutputStream fos = new FileOutputStream(imageFile);
                // 将Bitmap压缩为JPEG格式，并写入到FileOutputStream中
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                Log.d("Image Save", "Image saved to " + imageFile.getAbsolutePath());
                //Toast.makeText(wardrobe_cloth.this,"第"+i+"张",Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(requestCode == TAKE_PHOTO && resultCode == RESULT_OK && data != null)
        {
            //Toast.makeText(wardrobe_cloth.this,"文件中有"+num,Toast.LENGTH_SHORT).show();
            //Toast.makeText(wardrobe_cloth.this,""+requestCode,Toast.LENGTH_SHORT).show();
            int num=getNum();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // 获取你之前创建的文件夹的路径
            File directory = getFilesDir();
            String user_frame_name=""+user.getUser_id();
            // 访问多级目录
            File user_frame = new File(directory, user_frame_name);
            File wardrobe = new File(user_frame, "wardrobe");
            File clothes = new File(wardrobe, "clothes");
            File imageFile = new File(clothes, "clothes_"+num+".jpg");
            try {
                // 创建一个FileOutputStream来写入图片
                FileOutputStream fos = new FileOutputStream(imageFile);
                // 将Bitmap压缩为JPEG格式，并写入到FileOutputStream中
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                Log.d("Image Save", "Image saved to " + imageFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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

    private void showDialog() {
        if (mDialog ==null){
            initShareDialog();
        }
        mDialog.show();
    }
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
        return files.length;
    }

    private String getPath()
    {
        String user_frame_name=""+user.getUser_id();
        // 获取你之前创建的文件夹的路径
        File directory = getFilesDir();
        // 访问多级目录
        File user_frame = new File(directory, user_frame_name);
        File wardrobe = new File(user_frame, "wardrobe");
        File clothes = new File(wardrobe, "clothes");
        //File[] files = clothes.listFiles();
        return clothes.getPath();
    }


    //更改
    /*
    public void updateImageUris(List<Uri> newImageUris) {
        imageUris.addAll(newImageUris); // 或者用新的列表替换旧的列表
        adapter.setImageUris(imageUris); // 更新适配器中的图片列表
    }
    */

}
